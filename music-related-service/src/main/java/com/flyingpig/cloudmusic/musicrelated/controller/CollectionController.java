package com.flyingpig.cloudmusic.musicrelated.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.flyingpig.cloudmusic.musicrelated.service.CollectionService;
import com.flyingpig.cloudmusic.security.util.UserContext;
import com.flyingpig.cloudmusic.web.Result;
import com.flyingpig.feign.dataobject.dto.MusicDetail;
import com.flyingpig.feign.dataobject.dto.UserCollectInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.flyingpig.cloudmusic.musicrelated.dataobject.entity.Collection;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/collections")
@Api("收藏相关的接口")
public class CollectionController {
    @Autowired
    CollectionService collectionService;

    @SentinelResource("collection")
    @PostMapping("")
    @ApiOperation("收藏音乐和取消收藏")
    public Result collectMusic(@RequestParam @NotNull Long musicId) {
        Collection collection = new Collection();
        collection.setMusicId(musicId);
        collection.setUserId(UserContext.getUser().getUserId());
        if (collectionService.isCollectionExist(collection)) {
            collectionService.deleteCollection(collection);
            return Result.success("取消收藏成功");
        } else {
            collectionService.collectMusic(collection);
            return Result.success("收藏成功");
        }
    }

    @GetMapping("/user-collection-list")
    @ApiOperation("获取用户点赞集合")
    public Result getCollectionListByUserId() {
        return Result.success(collectionService.listCollectionByUserId());
    }

    @GetMapping("/user-collection-info")
    @ApiOperation("是否点赞[外部调用]")
    public UserCollectInfo getCollectionInfoByUserIdAndMusicId(Long userId, Long musicId) {
        Collection collection = new Collection(null, userId, musicId);
        return new UserCollectInfo(collectionService.getCollectionNum(collection), collectionService.isCollectionExist(collection));
    }
}
