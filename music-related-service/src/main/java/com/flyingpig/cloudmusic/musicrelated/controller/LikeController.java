package com.flyingpig.cloudmusic.musicrelated.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.flyingpig.cloudmusic.musicrelated.dataobject.entity.Like;
import com.flyingpig.cloudmusic.musicrelated.service.LikeService;
import com.flyingpig.cloudmusic.security.util.UserContext;
import com.flyingpig.cloudmusic.web.Result;
import com.flyingpig.feign.dataobject.dto.UserLikeInfo;
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
    @ApiOperation("点赞或取消点赞")
    public Result likeMusic(@RequestParam Long musicId) {
        if (likeService.likeMusic(new Like(null, UserContext.getUser().getUserId(), musicId))) {
            return Result.success("点赞成功");
        } else {
            return Result.success("取消点赞成功");
        }
    }

    @GetMapping("/user-like-list")
    @ApiOperation("获取用户点赞集合")
    public Result getLikeListByUserId() {
        return Result.success(likeService.listLikeByUserId());
    }

    @GetMapping("/user-like-info")
    @ApiOperation("是否点赞[外部调用]")
    public UserLikeInfo getLikeInfoByUserIdAndMusicId(Long userId, Long musicId) {
        return likeService.getLikeInfoByUserIdAndMusicId(userId, musicId);
    }
}
