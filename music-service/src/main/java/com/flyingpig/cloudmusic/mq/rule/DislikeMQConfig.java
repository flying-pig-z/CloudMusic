package com.flyingpig.cloudmusic.mq.rule;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.flyingpig.cloudmusic.util.RabbitMQConstants.*;

@Configuration
public class DislikeMQConfig {
    //交换机


    @Bean
    public FanoutExchange dislikeFanoutExchange(){
        return new FanoutExchange(MUSIC_DISLIKE_EXCHANGE_NAME);
    }

    //队列1


    @Bean
    public Queue dislikeFanoutQueue1() {
        return new Queue(MUSIC_DISLIKE_QUEUE_NAME1);
    }

    /**
     * 绑定队列1和交换机
     */
    @Bean
    public Binding dislikeBindingQueue1(Queue dislikeFanoutQueue1, FanoutExchange dislikeFanoutExchange){
        return BindingBuilder.bind(dislikeFanoutQueue1).to(dislikeFanoutExchange);
    }

    //队列2


    @Bean
    public Queue dislikeFanoutQueue2() {
        return new Queue(MUSIC_DISLIKE_QUEUE_NAME2);
    }

    /**
     * 绑定队列2和交换机
     */
    @Bean
    public Binding dislikeBindingQueue2(Queue dislikeFanoutQueue2, FanoutExchange dislikeFanoutExchange){
        return BindingBuilder.bind(dislikeFanoutQueue2).to(dislikeFanoutExchange);
    }
}
