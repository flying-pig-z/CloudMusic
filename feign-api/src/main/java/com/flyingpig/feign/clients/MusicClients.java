package com.flyingpig.feign.clients;

import com.flyingpig.feign.clients.fallback.MusicClientFallbackFactory;
import com.flyingpig.feign.dataobject.dto.MusicDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "music-service",fallbackFactory = MusicClientFallbackFactory.class)
public interface MusicClients {
    @GetMapping("/musics/{musicId}/detail")
    MusicDetail selectMusicDetailById(@PathVariable("musicId") Long musicId);

    @PutMapping("/musics/{musicId}/like-num")
    Boolean incOrDecLikeNum(@PathVariable("musicId") Long musicId, String mode);

    @PutMapping("/musics/{musicId}/collection-num")
    Boolean incOrDecCollectionNum(@PathVariable("musicId") Long musicId, String mode);

}
