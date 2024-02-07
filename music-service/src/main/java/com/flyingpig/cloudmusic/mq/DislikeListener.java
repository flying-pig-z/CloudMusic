package com.flyingpig.cloudmusic.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.cloudmusic.dataobject.entity.Like;
import com.flyingpig.cloudmusic.dataobject.entity.Music;
import com.flyingpig.cloudmusic.dataobject.message.LikeMessage;
import com.flyingpig.cloudmusic.mapper.LikeMapper;
import com.flyingpig.cloudmusic.mapper.MusicMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.flyingpig.cloudmusic.util.RabbitMQConstants.*;

@Component
@Slf4j
public class DislikeListener {
    @Autowired
    MusicMapper musicMapper;

    @Autowired
    LikeMapper likeMapper;

    @RabbitListener(queues = MUSIC_DISLIKE_QUEUE_NAME1)
    public void handleMusicDislikeRequest1(LikeMessage likeMessage) throws IOException {

        // 处理文件上传请求
        try {
            Music music=new Music();
            music.setId(likeMessage.getMusicId());
            music.setLikeNum(likeMessage.getLikeNum());
            musicMapper.updateById(music);
            LambdaQueryWrapper<Like> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            likeLambdaQueryWrapper.eq(Like::getMusicId, likeMessage.getMusicId());
            likeLambdaQueryWrapper.eq(Like::getUserId, likeMessage.getUserId());
            likeMapper.delete(likeLambdaQueryWrapper);
        } catch (Exception e) {
            // 异常处理
            log.error("取消点赞失败");
            throw new AmqpRejectAndDontRequeueException("处理音乐取消点赞请求失败，将消息丢弃");
        }
    }

    @RabbitListener(queues = MUSIC_DISLIKE_QUEUE_NAME2)
    public void handleMusicDislikeRequest2(LikeMessage likeMessage) throws IOException {

        // 处理文件上传请求
        try {
            Music music=new Music();
            music.setId(likeMessage.getMusicId());
            music.setLikeNum(likeMessage.getLikeNum());
            musicMapper.updateById(music);
            LambdaQueryWrapper<Like> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            likeLambdaQueryWrapper.eq(Like::getMusicId, likeMessage.getMusicId());
            likeLambdaQueryWrapper.eq(Like::getUserId, likeMessage.getUserId());
            likeMapper.delete(likeLambdaQueryWrapper);
        } catch (Exception e) {
            // 异常处理
            log.error("取消点赞失败");
            throw new AmqpRejectAndDontRequeueException("处理音乐取消点赞失败，将消息丢弃");
        }
    }
}
