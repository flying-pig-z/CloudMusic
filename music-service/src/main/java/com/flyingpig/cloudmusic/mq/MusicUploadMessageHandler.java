package com.flyingpig.cloudmusic.mq;

import com.flyingpig.cloudmusic.dataobject.message.MusicUploadMessage;
import com.flyingpig.cloudmusic.service.MusicService;
import com.flyingpig.cloudmusic.util.AliOSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class MusicUploadMessageHandler {

    @Autowired
    private MusicService musicService;
    @Autowired
    AliOSSUtils aliOSSUtils;
    @RabbitListener(queues = "music_queue")
    public void handleMusicUploadRequest(MusicUploadMessage request) throws IOException {
        // 处理文件上传请求
        try {
            String coverPath = aliOSSUtils.upload(request.getCoverFile());
            String musicPath = aliOSSUtils.upload(request.getMusicFile());
            request.getMusic().setCoverPath(coverPath);
            request.getMusic().setMusicPath(musicPath);
            musicService.addMusic(request.getMusic());
        } catch (Exception e) {
            // 异常处理
            log.error("处理音乐上传请求失败");
            throw new AmqpRejectAndDontRequeueException("处理音乐上传请求失败，将消息丢弃");
        }
    }
}