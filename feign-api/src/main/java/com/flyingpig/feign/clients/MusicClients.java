package com.flyingpig.feign.clients;

import com.flyingpig.feign.clients.fallback.MusicClientFallbackFactory;
import com.flyingpig.feign.dataobject.dto.MusicDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "music-service",fallbackFactory = MusicClientFallbackFactory.class)
public interface MusicClients {
    @GetMapping("/musics/{musicId}/detail")
    MusicDetail selectMusicDetailById(@PathVariable Long musicId);
}
