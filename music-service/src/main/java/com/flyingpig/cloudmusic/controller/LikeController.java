package com.flyingpig.cloudmusic.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.flyingpig.cloudmusic.dataobject.entity.Like;
import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.cloudmusic.service.LikeService;
import com.flyingpig.cloudmusic.util.UserContext;
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

    @SentinelResource("like")
    @PostMapping("")
    @ApiOperation("点赞")
    public Result likeMusic(@RequestParam Long musicId) {
        Like like = new Like();
        like.setMusicId(musicId);
        like.setUserId(UserContext.getUserId());
        boolean isLikeExist = likeService.isLikeExist(like);
        if (isLikeExist) {
            return Result.error(500, "请误重复点赞");
        } else {
            likeService.likeMusic(like);
            return Result.success();
        }
    }

    @DeleteMapping("")
    @ApiOperation("取消点赞")
    public Result deleteLike(@RequestParam Long musicId) {
        Like like = new Like();
        like.setMusicId(musicId);
        like.setUserId(UserContext.getUserId());
        likeService.deleteLike(like);
        return Result.success();
    }
}
