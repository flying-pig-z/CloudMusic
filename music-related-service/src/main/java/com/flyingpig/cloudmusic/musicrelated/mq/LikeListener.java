package com.flyingpig.cloudmusic.musicrelated.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.cloudmusic.musicrelated.dataobject.entity.Like;
import com.flyingpig.cloudmusic.musicrelated.dataobject.entity.Music;
import com.flyingpig.cloudmusic.musicrelated.dataobject.message.LikeMessage;
import com.flyingpig.cloudmusic.musicrelated.mapper.LikeMapper;
import com.flyingpig.feign.clients.MusicClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Objects;
import static com.flyingpig.cloudmusic.musicrelated.constant.MQConstants.MUSIC_LIKE_QUEUE_NAME1;
import static com.flyingpig.cloudmusic.musicrelated.constant.MQConstants.MUSIC_LIKE_QUEUE_NAME2;

@Component
@Slf4j
public class LikeListener {

    @Autowired
    LikeMapper likeMapper;

    @Autowired
    MusicClients musicClients;

    @RabbitListener(queues = MUSIC_LIKE_QUEUE_NAME1)
    public void handleMusicLikeRequest1(LikeMessage likeMessage) throws IOException {
        processLikeMessage(likeMessage);
    }

    @RabbitListener(queues = MUSIC_LIKE_QUEUE_NAME2)
    public void handleMusicLikeRequest2(LikeMessage likeMessage) throws IOException {
        processLikeMessage(likeMessage);
    }


    private void processLikeMessage(LikeMessage likeMessage) {
        try {
            Music music = new Music();
            music.setId(Long.parseLong(likeMessage.getMusicId()));
            if (Objects.equals(likeMessage.getMode(), LikeMessage.INCREASE)) {
                if(musicClients.incOrDecLikeNum(music.getId(), LikeMessage.INCREASE))
                    likeMapper.insert(new Like(null, Long.parseLong(likeMessage.getUserId()), Long.parseLong(likeMessage.getMusicId())));
            } else {
                if(musicClients.incOrDecLikeNum(music.getId(), LikeMessage.DECREASE))
                    likeMapper.delete(new LambdaQueryWrapper<Like>()
                            .eq(Like::getMusicId, likeMessage.getMusicId())
                            .eq(Like::getUserId, likeMessage.getUserId()));
            }
        } catch (Exception e) {
            log.error("点赞失败", e);
            e.printStackTrace();
            System.out.println(likeMessage.getMusicId() + "  " + likeMessage.getUserId());
            throw new AmqpRejectAndDontRequeueException("处理音乐点赞失败，将消息丢弃");
        }
    }
}
