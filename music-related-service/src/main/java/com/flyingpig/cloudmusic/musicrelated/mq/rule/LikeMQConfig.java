package com.flyingpig.cloudmusic.musicrelated.mq.rule;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.flyingpig.cloudmusic.musicrelated.constant.MQConstants.*;

@Configuration
public class LikeMQConfig {
    //交换机


    @Bean
    public DirectExchange likeDirectExchange(){
        return new DirectExchange(MUSIC_LIKE_EXCHANGE_NAME);
    }

    //队列1


    @Bean
    public Queue likeDirectQueue1() {
        return new Queue(MUSIC_LIKE_QUEUE_NAME1);
    }

    /**
     * 绑定队列1和交换机
     */
    @Bean
    public Binding likeBindingQueue1(Queue likeDirectQueue1, DirectExchange likeDirectExchange){
        return BindingBuilder.bind(likeDirectQueue1).to(likeDirectExchange).with("1");
    }

    //队列2


    @Bean
    public Queue likeDirectQueue2() {
        return new Queue(MUSIC_LIKE_QUEUE_NAME2);
    }

    /**
     * 绑定队列2和交换机
     */
    @Bean
    public Binding likeBindingQueue2(Queue likeDirectQueue2, DirectExchange likeDirectExchange){
        return BindingBuilder.bind(likeDirectQueue2).to(likeDirectExchange).with("2");
    }
}
