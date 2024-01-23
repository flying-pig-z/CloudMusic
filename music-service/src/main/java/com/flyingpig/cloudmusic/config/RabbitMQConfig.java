package com.flyingpig.cloudmusic.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private final String host = "111.229.173.12";
    private final int port = 5672;
    private final String virtualHost = "/";
    private final String username = "flyingpig";
    private final String password = "Aa123456";

    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory(host);
        factory.setPort(port);
        factory.setVirtualHost(virtualHost);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }

    // 配置RabbitMQ连接信息
    private final String QUEUE_NAME = "music_queue";

    @Bean
    public Queue musicQueue() {
        return new Queue(QUEUE_NAME);
    }
}
