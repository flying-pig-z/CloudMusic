package com.flyingpig.feign.clients.fallback;

import com.flyingpig.feign.clients.MusicClients;
import com.flyingpig.feign.dataobject.dto.MusicDetail;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import javax.xml.transform.Result;

@Slf4j
public class MusicClientFallbackFactory implements FallbackFactory<MusicClients> {
    @Override
    public MusicClients create(Throwable throwable) {
        return new MusicClients() {
            @Override
            public MusicDetail selectMusicDetailById(Long musicId) {
                log.error("查询音乐信息异常");
                return new MusicDetail();
            }

            @Override
            public Boolean incOrDecLikeNum(Long musicId, String operation) {
                log.error("操作点赞数异常: musicId={}, operation={}", musicId, operation);
                // 这里可以添加默认的处理逻辑
                return false;
            }

            @Override
            public Boolean incOrDecCollectionNum(Long musicId, String mode) {
                return false;
            }
        };
    }
}
