package com.flyingpig.cloudmusic.idempotent.config;

import com.flyingpig.cloudmusic.cache.StringCacheUtil;
import com.flyingpig.cloudmusic.idempotent.core.IdempotentAspect;
import com.flyingpig.cloudmusic.idempotent.core.bases.ApplicationContextHolder;
import com.flyingpig.cloudmusic.idempotent.core.param.IdempotentParamExecuteHandler;
import com.flyingpig.cloudmusic.idempotent.core.param.IdempotentParamService;
import com.flyingpig.cloudmusic.idempotent.core.spel.IdempotentSpELByMQExecuteHandler;
import com.flyingpig.cloudmusic.idempotent.core.spel.IdempotentSpELByRestAPIExecuteHandler;
import com.flyingpig.cloudmusic.idempotent.core.spel.IdempotentSpELService;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 幂等自动装配
 */
@EnableConfigurationProperties(IdempotentProperties.class)
public class IdempotentAutoConfiguration {

    /**
     * 幂等切面
     */
    @Bean
    public IdempotentAspect idempotentAspect() {
        return new IdempotentAspect();
    }

    /**
     * 参数方式幂等实现，基于 RestAPI 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentParamService idempotentParamExecuteHandler(RedissonClient redissonClient) {
        return new IdempotentParamExecuteHandler(redissonClient);
    }

    /**
     * SpEL 方式幂等实现，基于 RestAPI 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentSpELService idempotentSpELByRestAPIExecuteHandler(RedissonClient redissonClient) {
        return new IdempotentSpELByRestAPIExecuteHandler(redissonClient);
    }

    /**
     * SpEL 方式幂等实现，基于 MQ 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentSpELByMQExecuteHandler idempotentSpELByMQExecuteHandler(StringCacheUtil stringCacheUtil, StringRedisTemplate stringRedisTemplate) {
        return new IdempotentSpELByMQExecuteHandler(stringCacheUtil, stringRedisTemplate);
    }


    /**
     * 应用基础自动装配
     * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
     */
    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextHolder congoApplicationContextHolder() {
        return new ApplicationContextHolder();
    }
}
