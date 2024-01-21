package com.flyingpig.feign.clients.fallback;

import com.flyingpig.feign.clients.MusicClients;
import com.flyingpig.feign.dataobject.dto.MusicDetail;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

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
        };
    }
}
