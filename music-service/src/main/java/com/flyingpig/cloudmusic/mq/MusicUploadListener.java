package com.flyingpig.cloudmusic.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flyingpig.cloudmusic.dataobject.es.MusicDoc;
import com.flyingpig.cloudmusic.dataobject.message.MusicUploadMessage;
import com.flyingpig.cloudmusic.mapper.MusicMapper;
import com.flyingpig.cloudmusic.service.MusicService;
import com.flyingpig.cloudmusic.util.AliOSSUtils;
import com.flyingpig.cloudmusic.util.ElasticSearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.flyingpig.cloudmusic.constant.RabbitMQConstants.MUSIC_UPLOAD_QUEUE_NAME1;
import static com.flyingpig.cloudmusic.constant.RabbitMQConstants.MUSIC_UPLOAD_QUEUE_NAME2;

@Component
@Slf4j
public class MusicUploadListener {

    @Autowired
    private MusicMapper musicMapper;
    @Autowired
    AliOSSUtils aliOSSUtils;

    @Autowired
    ElasticSearchUtil elasticSearchUtil;

    @RabbitListener(queues = MUSIC_UPLOAD_QUEUE_NAME1)
    public void handleMusicUploadRequest1(MusicUploadMessage request) throws IOException {
        //处理文件上传请求
        try {
            String coverPath = aliOSSUtils.upload(request.getCoverFile());
            String musicPath = aliOSSUtils.upload(request.getMusicFile());
            request.getMusic().setCoverPath(coverPath);
            request.getMusic().setMusicPath(musicPath);
            musicMapper.insert(request.getMusic());
            MusicDoc musicDoc = new MusicDoc( request.getMusic().getId(), request.getMusic().getIntroduce(), request.getMusic().getName(), request.getMusic().getSingerName());
            elasticSearchUtil.importDocument("music", String.valueOf(musicDoc.getId()), new ObjectMapper().writeValueAsString(musicDoc));
        } catch (Exception e) {
            // 异常处理
            log.error("处理音乐上传请求失败");
            throw new AmqpRejectAndDontRequeueException("处理音乐上传请求失败，将消息丢弃");
        }
    }

    @RabbitListener(queues = MUSIC_UPLOAD_QUEUE_NAME2)
    public void handleMusicUploadRequest2(MusicUploadMessage request) throws IOException {

        // 处理文件上传请求
        try {
            String coverPath = aliOSSUtils.upload(request.getCoverFile());
            String musicPath = aliOSSUtils.upload(request.getMusicFile());
            request.getMusic().setCoverPath(coverPath);
            request.getMusic().setMusicPath(musicPath);
            musicMapper.insert(request.getMusic());
            MusicDoc musicDoc = new MusicDoc( request.getMusic().getId(), request.getMusic().getIntroduce(), request.getMusic().getName(), request.getMusic().getSingerName());
            elasticSearchUtil.importDocument("music", String.valueOf(musicDoc.getId()), new ObjectMapper().writeValueAsString(musicDoc));
        } catch (Exception e) {
            // 异常处理
            log.error("处理音乐上传请求失败");
            throw new AmqpRejectAndDontRequeueException("处理音乐上传请求失败，将消息丢弃");
        }
    }
}
