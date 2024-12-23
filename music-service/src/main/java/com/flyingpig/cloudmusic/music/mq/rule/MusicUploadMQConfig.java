package com.flyingpig.cloudmusic.music.mq.rule;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.flyingpig.cloudmusic.music.constant.MQConstants.*;


@Configuration
public class MusicUploadMQConfig {
    // 交换机


    @Bean
    public FanoutExchange musicUploadFanoutExchange(){
        return new FanoutExchange(MUSIC_UPLOAD_EXCHANGE_NAME);
    }

    //队列


    @Bean
    public Queue musicUploadFanoutQueue1() {
        return new Queue(MUSIC_UPLOAD_QUEUE_NAME1);
    }

    // 绑定队列和交换机
    @Bean
    public Binding bindingQueue1(Queue musicUploadFanoutQueue1, FanoutExchange musicUploadFanoutExchange){
        return BindingBuilder.bind(musicUploadFanoutQueue1).to(musicUploadFanoutExchange);
    }

}
