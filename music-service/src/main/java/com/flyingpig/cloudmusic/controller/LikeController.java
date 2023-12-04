package com.flyingpig.cloudmusic.controller;

import com.flyingpig.cloudmusic.common.Result;
import com.flyingpig.cloudmusic.dataobject.entity.Like;
import com.flyingpig.cloudmusic.service.LikeService;
import com.flyingpig.cloudmusic.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/likes")
public class LikeController {
    @Autowired
    LikeService likeService;
    @PostMapping("")
    public Result likeMusic(@RequestHeader String Authorization, @RequestParam Long musicId){
        Claims claims = JwtUtil.parseJwt(Authorization);
        Long userId = Long.parseLong(claims.getSubject());
        Like like=new Like();
        like.setMusicId(musicId);
        like.setUserId(userId);
        likeService.likeMusic(like);
        return Result.success();
    }

    @DeleteMapping("")
    public Result deleteLike(@RequestHeader String Authorization, @RequestParam Long musicId){
        Claims claims = JwtUtil.parseJwt(Authorization);
        Long userId = Long.parseLong(claims.getSubject());
        Like like=new Like();
        like.setMusicId(musicId);
        like.setUserId(userId);
        likeService.deleteLike(like);
        return Result.success();
    }
}
