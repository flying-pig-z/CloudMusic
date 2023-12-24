package com.flyingpig.gateway.filter;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.cloudmusic.util.JwtUtil;
import com.flyingpig.feign.clients.UserClients;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
    private static List<String> whitelist = null;
    static {
        //加载白名单
        try (
                InputStream resourceAsStream = AuthFilter.class.getResourceAsStream("/filter/whitelist.properties");
        ) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            Set<String> strings = properties.stringPropertyNames();
            whitelist= new ArrayList<>(strings);
        } catch (Exception e) {
            log.error("加载/whitelist.properties出错:{}",e.getMessage());
            e.printStackTrace();
        }
    }
    //filter方法是GlobalFilter接口中定义的核心方法
    // 用于实现过滤逻辑。当请求经过GlobalFilter时，会调用该方法对请求进行处理。
    // 在filter方法中，你可以对请求进行修改、添加头信息、验证身份等操作，
    // 也可以对响应进行处理，比如添加自定义的数据、修改响应状态码等。

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
        System.out.println(requestUrl);
        //2.检查token是否存在
        String tokenStr = exchange.getRequest().getHeaders().getFirst("Authorization");
        if(StringUtils.isBlank(tokenStr)){
            return getVoidMono(response,Result.error(401,"token为空"));
        }
        //3.判断是否是有效的token
        try {
            Claims claims = JwtUtil.parseJwt(tokenStr);
            //4.判断是否在黑名单里
            String uuid=claims.getId();
            if(userClients.uuidIsInBlackListOrNot(uuid)){
                return getVoidMono(response,Result.error(401,"该token已登出"));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return getVoidMono(response,Result.error(401,"身份过期或身份证明非法"));
        }
        //检查通过放行
        return chain.filter(exchange);
    }

    //getOrder方法是GlobalFilter接口中的另一个方法，用于指定过滤器的执行顺序。Gateway会根据过滤器的order值进行排序，order值越小，
    // 优先级越高，会先执行。可以通过重写getOrder方法来设置过滤器的执行顺序，确保过滤器按照预期的顺序执行。
    @Override
    public int getOrder() {
        return 0;
    }

    //响应式的response包装
    private Mono<Void> getVoidMono(ServerHttpResponse serverHttpResponse,Result result) {
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONString(result).getBytes());
        return serverHttpResponse.writeWith(Flux.just(dataBuffer));
    }
}
