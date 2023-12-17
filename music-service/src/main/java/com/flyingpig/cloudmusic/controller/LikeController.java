package com.flyingpig.cloudmusic.controller;

import com.flyingpig.cloudmusic.dataobject.entity.Like;
import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.cloudmusic.service.LikeService;
import com.flyingpig.cloudmusic.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/likes")
@Api("点赞相关的接口")
public class LikeController {
    @Autowired
    LikeService likeService;

    @PostMapping("")
    @ApiOperation("点赞")
    public Result likeMusic(@RequestHeader String Authorization, @RequestParam Long musicId) {
        Claims claims = JwtUtil.parseJwt(Authorization);
        Long userId = Long.parseLong(claims.getSubject());
        Like like = new Like();
        like.setMusicId(musicId);
        like.setUserId(userId);
        try {
            likeService.likeMusic(like);
            return Result.success();
        } catch (Exception exception) {
            return Result.error(500,"请误重复收藏");
        }
    }

    @DeleteMapping("")
    @ApiOperation("取消点赞")
    public Result deleteLike(@RequestHeader String Authorization, @RequestParam Long musicId) {
        Claims claims = JwtUtil.parseJwt(Authorization);
        Long userId = Long.parseLong(claims.getSubject());
        Like like = new Like();
        like.setMusicId(musicId);
        like.setUserId(userId);
        likeService.deleteLike(like);
        return Result.success();
    }
}
