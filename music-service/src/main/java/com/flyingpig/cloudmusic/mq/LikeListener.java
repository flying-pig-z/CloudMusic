package com.flyingpig.cloudmusic.mq;

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

import static com.flyingpig.cloudmusic.constant.RabbitMQConstants.*;

@Component
@Slf4j
public class LikeListener {
    @Autowired
    MusicMapper musicMapper;

    @Autowired
    LikeMapper likeMapper;

    @RabbitListener(queues = MUSIC_LIKE_QUEUE_NAME1)
    public void handleMusicLikeRequest1(LikeMessage likeMessage) throws IOException {

        // 处理文件上传请求
        try {
            Music music=new Music();
            music.setId(likeMessage.getMusicId());
            music.setLikeNum(likeMessage.getLikeNum());
            musicMapper.updateById(music);
            Like like=new Like(null, likeMessage.getUserId(), likeMessage.getMusicId());
            likeMapper.insert(like);
        } catch (Exception e) {
            // 异常处理
            log.error("点赞失败");
            throw new AmqpRejectAndDontRequeueException("处理音乐点赞失败，将消息丢弃");
        }
    }

    @RabbitListener(queues = MUSIC_LIKE_QUEUE_NAME2)
    public void handleMusicLikeRequest2(LikeMessage likeMessage) throws IOException {

        // 处理文件上传请求
        try {
            Music music=new Music();
            music.setId(likeMessage.getMusicId());
            music.setLikeNum(likeMessage.getLikeNum());
            musicMapper.updateById(music);
            Like like=new Like(null, likeMessage.getUserId(), likeMessage.getMusicId());
            likeMapper.insert(like);
        } catch (Exception e) {
            // 异常处理
            log.error("点赞失败-------");
            log.error(e.toString());
            throw new AmqpRejectAndDontRequeueException("处理音乐点赞请求失败，将消息丢弃");
        }
    }
}
