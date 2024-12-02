package com.flyingpig.feign.clients;

import com.flyingpig.feign.dataobject.dto.UserCollectInfo;
import com.flyingpig.feign.dataobject.dto.UserLikeInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "music-related-service")
public interface MusicRelatedClient {

    @GetMapping("/collections/user-collection-info")
    UserCollectInfo getCollectInfoByUserIdAndMusicId(@RequestParam("userId") Long userId, @RequestParam("musicId") Long musicId);

    @GetMapping("/likes/user-like-info")
    UserLikeInfo getLikeInfoByUserIdAndMusicId(@RequestParam("userId") Long userId, @RequestParam("musicId") Long musicId);
}
