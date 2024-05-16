package com.flyingpig.cloudmusic.mq.rule;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static com.flyingpig.cloudmusic.constant.RabbitMQConstants.*;


@Configuration
public class MusicUploadMQConfig {
    //交换机


    @Bean
    public FanoutExchange musicUploadFanoutExchange(){
        return new FanoutExchange(MUSIC_UPLOAD_EXCHANGE_NAME);
    }

    //队列1


    @Bean
    public Queue musicUploadFanoutQueue1() {
        return new Queue(MUSIC_UPLOAD_QUEUE_NAME1);
    }

    /**
     * 绑定队列1和交换机
     */
    @Bean
    public Binding bindingQueue1(Queue musicUploadFanoutQueue1, FanoutExchange musicUploadFanoutExchange){
        return BindingBuilder.bind(musicUploadFanoutQueue1).to(musicUploadFanoutExchange);
    }

    //队列2


    @Bean
    public Queue musicUploadFanoutQueue2() {
        return new Queue(MUSIC_UPLOAD_QUEUE_NAME2);
    }

    /**
     * 绑定队列2和交换机
     */
    @Bean
    public Binding bindingQueue2(Queue musicUploadFanoutQueue2, FanoutExchange musicUploadFanoutExchange){
        return BindingBuilder.bind(musicUploadFanoutQueue2).to(musicUploadFanoutExchange);
    }

}
