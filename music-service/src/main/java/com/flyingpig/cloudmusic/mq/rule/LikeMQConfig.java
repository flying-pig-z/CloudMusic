package com.flyingpig.cloudmusic.mq.rule;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.flyingpig.cloudmusic.util.RabbitMQConstants.*;

@Configuration
public class LikeMQConfig {
    //交换机


    @Bean
    public FanoutExchange likeFanoutExchange(){
        return new FanoutExchange(MUSIC_LIKE_EXCHANGE_NAME);
    }

    //队列1


    @Bean
    public Queue likeFanoutQueue1() {
        return new Queue(MUSIC_LIKE_QUEUE_NAME1);
    }

    /**
     * 绑定队列1和交换机
     */
    @Bean
    public Binding likeBindingQueue1(Queue likeFanoutQueue1, FanoutExchange likeFanoutExchange){
        return BindingBuilder.bind(likeFanoutQueue1).to(likeFanoutExchange);
    }

    //队列2


    @Bean
    public Queue likeFanoutQueue2() {
        return new Queue(MUSIC_LIKE_QUEUE_NAME2);
    }

    /**
     * 绑定队列2和交换机
     */
    @Bean
    public Binding likeBindingQueue2(Queue likeFanoutQueue2, FanoutExchange likeFanoutExchange){
        return BindingBuilder.bind(likeFanoutQueue2).to(likeFanoutExchange);
    }
}
