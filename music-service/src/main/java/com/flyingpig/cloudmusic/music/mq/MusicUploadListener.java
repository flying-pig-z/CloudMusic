package com.flyingpig.cloudmusic.music.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flyingpig.cloudmusic.file.AliOSSUtils;
import com.flyingpig.cloudmusic.idempotent.annotation.Idempotent;
import com.flyingpig.cloudmusic.idempotent.enums.IdempotentSceneEnum;
import com.flyingpig.cloudmusic.idempotent.enums.IdempotentTypeEnum;
import com.flyingpig.cloudmusic.music.dataobject.es.MusicDoc;
import com.flyingpig.cloudmusic.music.dataobject.message.MusicUploadMessage;
import com.flyingpig.cloudmusic.music.mapper.MusicMapper;
import com.flyingpig.cloudmusic.music.util.ElasticSearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.flyingpig.cloudmusic.music.constant.MQConstants.MUSIC_UPLOAD_QUEUE_NAME1;
import static com.flyingpig.cloudmusic.music.constant.MQConstants.MUSIC_UPLOAD_QUEUE_NAME2;

@Component
@Slf4j
public class MusicUploadListener {

    @Autowired
    private MusicMapper musicMapper;
    @Autowired
    AliOSSUtils aliOSSUtils;

    @RabbitListener(queues = MUSIC_UPLOAD_QUEUE_NAME1)
    public void handleMusicUploadRequest1(MusicUploadMessage request) throws IOException {
        processMusicUpload(request);
    }

    @RabbitListener(queues = MUSIC_UPLOAD_QUEUE_NAME2)
    public void handleMusicUploadRequest2(MusicUploadMessage request) throws IOException {
        processMusicUpload(request);
    }

    @Idempotent(
            uniqueKeyPrefix = "music:upload:process:",
            key = "#request.getMusic().getUploadUser() + '_' + #request.getMusic().getName()",
            type = IdempotentTypeEnum.SPEL,
            scene = IdempotentSceneEnum.MQ,
            keyTimeout = 7200L
    )
    private void processMusicUpload(MusicUploadMessage request) {
        try {
            log.info("用户{}上传音乐{}处理", request.getMusic().getUploadUser(), request.getMusic().getName());

            // 加载敏感词列表
            SensitiveWordLoader loader = new SensitiveWordLoader();
            List<String> sensitiveWords = loader.getSensitiveWords(); // 加载 resources/sensitive-words.txt

            // 创建敏感词过滤器
            SensitiveWordFilter filter = new SensitiveWordFilter(sensitiveWords);

            // 审核歌曲，检查是否包含敏感词
            if (filter.hasSensitiveWords(request.getMusic())) {
                log.warn("歌曲{}包含敏感词，上传请求被拒绝", request.getMusic().getName());
                throw new IllegalArgumentException("歌曲包含敏感词，上传请求被拒绝");
            }

            log.info("用户{}上传音乐{}审核成功", request.getMusic().getUploadUser(), request.getMusic().getName());

            // 上传封面和音乐文件到 OSS,并设置音乐文件路径
            request.getMusic().setCoverPath(aliOSSUtils.upload(request.getCoverFile()));
            request.getMusic().setMusicPath(aliOSSUtils.upload(request.getMusicFile()));

            // 插入数据库
            musicMapper.insert(request.getMusic());

            log.info("用户{}上传音乐{}处理结束", request.getMusic().getUploadUser(), request.getMusic().getName());

        } catch (Exception e) {
            log.error("处理音乐上传请求失败", e);
            throw new AmqpRejectAndDontRequeueException("处理音乐上传请求失败，将消息丢弃");
        }
    }


// 构造并导入到 Elasticsearch（如果你使用了Elasticsearch把代码解除注释）

//    @Autowired
//    ElasticSearchUtil elasticSearchUtil;

//    private void syncToES(MusicUploadMessage request){
//        MusicDoc musicDoc = new MusicDoc(
//                request.getMusic().getId(),
//                request.getMusic().getIntroduce(),
//                request.getMusic().getName(),
//                request.getMusic().getSingerName()
//        );
//        elasticSearchUtil.importDocument(
//                "music",
//                String.valueOf(musicDoc.getId()),
//                new ObjectMapper().writeValueAsString(musicDoc)
//        );
//      }
}



