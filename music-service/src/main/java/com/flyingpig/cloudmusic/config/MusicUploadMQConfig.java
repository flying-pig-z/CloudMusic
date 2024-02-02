package com.flyingpig.cloudmusic.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static com.flyingpig.cloudmusic.util.RabbitMQConstants.*;


@Configuration
public class MusicUploadMQConfig {
    //交换机


    @Bean
    public FanoutExchange musicUploadExchange(){
        return new FanoutExchange(MUSIC_UPLOAD_EXCHANGE_NAME);
    }

    //队列1


    @Bean
    public Queue musicUploadQueue1() {
        return new Queue(MUSIC_UPLOAD_QUEUE_NAME1);
    }

    /**
     * 绑定队列1和交换机
     */
    @Bean
    public Binding bindingQueue1(Queue musicUploadQueue1, FanoutExchange musicUploadExchange){
        return BindingBuilder.bind(musicUploadQueue1).to(musicUploadExchange);
    }

    //队列2


    @Bean
    public Queue musicUploadQueue2() {
        return new Queue(MUSIC_UPLOAD_QUEUE_NAME2);
    }

    /**
     * 绑定队列2和交换机
     */
    @Bean
    public Binding bindingQueue2(Queue musicUploadQueue2, FanoutExchange musicUploadExchange){
        return BindingBuilder.bind(musicUploadQueue2).to(musicUploadExchange);
    }

}
