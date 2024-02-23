package com.flyingpig.cloudmusic.mq.rule;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.flyingpig.cloudmusic.util.RabbitMQConstants.*;

@Configuration
public class DislikeMQConfig {
    //交换机


    @Bean
    public DirectExchange dislikeDirectExchange(){
        return new DirectExchange(MUSIC_DISLIKE_EXCHANGE_NAME);
    }

    //队列1


    @Bean
    public Queue dislikeDirectQueue1() {
        return new Queue(MUSIC_DISLIKE_QUEUE_NAME1);
    }

    /**
     * 绑定队列1和交换机
     */
    @Bean
    public Binding dislikeBindingQueue1(Queue dislikeDirectQueue1, DirectExchange dislikeDirectExchange){
        return BindingBuilder.bind(dislikeDirectQueue1).to(dislikeDirectExchange).with("1");
    }

    //队列2


    @Bean
    public Queue dislikeDirectQueue2() {
        return new Queue(MUSIC_DISLIKE_QUEUE_NAME2);
    }

    /**
     * 绑定队列2和交换机
     */
    @Bean
    public Binding dislikeBindingQueue2(Queue dislikeDirectQueue2, DirectExchange dislikeDirectExchange){
        return BindingBuilder.bind(dislikeDirectQueue2).to(dislikeDirectExchange).with("2");
    }
}
