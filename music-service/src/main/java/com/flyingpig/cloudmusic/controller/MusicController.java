package com.flyingpig.cloudmusic.controller;


import com.flyingpig.cloudmusic.common.Result;
import com.flyingpig.cloudmusic.dataobject.dto.MusicDT0;
import com.flyingpig.cloudmusic.dataobject.dto.MusicInfoInRankList;
import com.flyingpig.cloudmusic.service.MusicService;
import com.flyingpig.cloudmusic.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@Slf4j
@RestController
@RequestMapping("/musics")
public class MusicController {
    @Autowired
    private MusicService musicService;

    @GetMapping("/{musicId}")
    public Result selectMusicVOById(@RequestHeader String Authorization, @PathVariable Long musicId){
        Claims claims = JwtUtil.parseJwt(Authorization);
        Long userId = Long.parseLong(claims.getSubject());
        MusicDT0 musicVO=musicService.selectMusicVOByUserIdAndMusicId(userId,musicId);
        return Result.success(musicVO);
    }

    @GetMapping("/rank-list")
    public Result selectRankList(){
        List<MusicInfoInRankList> result= musicService.selectRankList();
        return Result.success(result);
    }





}
