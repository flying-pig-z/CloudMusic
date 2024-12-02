package com.flyingpig.cloudmusic.gateway.filter;

import com.alibaba.cloud.commons.lang.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.flyingpig.feign.clients.UserClients;
import com.flyingpig.cloudmusic.gateway.common.JwtUtil;
import com.flyingpig.cloudmusic.gateway.common.Result;
import com.flyingpig.cloudmusic.gateway.common.StatusCode;
import io.jsonwebtoken.Claims;
import io.netty.channel.ConnectTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    UserClients userClients;

    //白名单
    private static final List<String> whitelist = new ArrayList<>();

    static {
        //加载白名单
        loadWhitelist();
    }


    //认证的基本逻辑：对登录之类的部分接口放行，除此之外
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUrl = exchange.getRequest().getPath().value();
        ServerHttpResponse response = exchange.getResponse();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        //1.白名单放行
        for (String url : whitelist) {
            if (pathMatcher.match(url, requestUrl)) {
                return chain.filter(exchange);
            }
        }

        //2.检查token是否存在
        String tokenStr = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (StringUtils.isBlank(tokenStr)) {
            return getVoidMono(response, Result.error(StatusCode.UNAUTHORIZED, "token为空"));
        }
        //3.判断是否是有效的token
        String userId = null;
        try {
            Claims claims = JwtUtil.parseJwt(tokenStr);
            userId = claims.getSubject();
            // 4.判断是否在黑名单里
//            String uuid = claims.getId();
//            if (!userClients.uuidIsInWhiteListOrNot(userId, uuid)) {
//                return getVoidMono(response, Result.error(StatusCode.UNAUTHORIZED, "该token已失效"));
//            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return getVoidMono(response, Result.error(StatusCode.UNAUTHORIZED, "身份过期或身份证明非法"));
        }
        //5.传递用户id和用户权限信息给各个微服务，避免再次解析token
        String finalUserId = userId;
        String userRole = userClients.selectUserInfoByUserId(Long.parseLong(userId)).getRole();
        ServerWebExchange swe = exchange.mutate()
                .request(builder -> builder.header("userId", finalUserId))
                .request(builder -> builder.header("userRole", userRole))
                .build();
        log.info("userId:{} requestUrl:{}", finalUserId, requestUrl);
        //6.检查通过放行
        return chain.filter(swe);
    }


    //getOrder方法是GlobalFilter接口中的另一个方法，用于指定过滤器的执行顺序。Gateway会根据过滤器的order值进行排序，order值越小，
    // 优先级越高，会先执行。可以通过重写getOrder方法来设置过滤器的执行顺序，确保过滤器按照预期的顺序执行。
    @Override
    public int getOrder() {
        return 0;
    }

    //响应式的response包装
    private Mono<Void> getVoidMono(ServerHttpResponse serverHttpResponse, Result result) {
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONString(result).getBytes());
        return serverHttpResponse.writeWith(Flux.just(dataBuffer));
    }

    private static void loadWhitelist() {
        try (InputStream resourceAsStream = AuthFilter.class.getResourceAsStream("/filter/whitelist.properties")) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            Set<String> strings = properties.stringPropertyNames();
            whitelist.addAll(strings);
        } catch (Exception e) {
            log.error("加载/whitelist.properties出错:{}", e.getMessage());
        }
    }
}
