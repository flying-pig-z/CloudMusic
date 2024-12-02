package com.flyingpig.cloudmusic.musicrelated.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(Object.class);
        //只不过写入前会把Key和value序列化为字节形式，默认是采用JDK序列化，得到的结果可读性差
        //默认的序列化器为：JdkSerializationRedisSerializer
        //这里使用StringRedisSerializer来序列化和反序列化redis的key值
        // 使得在代码里key和value写的是什么，存进redis里的就是什么，不做变化

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);//其实value的序列化器可以不用更改，存进去什么取出来还会反序列化一次

        // Hash的key和value也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}