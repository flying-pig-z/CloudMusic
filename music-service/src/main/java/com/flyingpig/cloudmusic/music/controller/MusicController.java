package com.flyingpig.cloudmusic.music.controller;


import com.flyingpig.cloudmusic.music.common.Result;
import com.flyingpig.cloudmusic.music.dataobject.dto.MusicDT0;
import com.flyingpig.cloudmusic.music.service.MusicService;
import com.flyingpig.cloudmusic.music.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



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



}
