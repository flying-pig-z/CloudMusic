package com.flyingpig.cloudmusic.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.flyingpig.cloudmusic.dataobject.entity.Collection;
import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.cloudmusic.service.CollectionService;
import com.flyingpig.cloudmusic.util.JwtUtil;
import com.flyingpig.cloudmusic.util.UserContext;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/collections")
@Api("收藏相关的接口")
public class CollectionController {
    @Autowired
    CollectionService collectionService;

    @SentinelResource("collection")
    @PostMapping("")
    @ApiOperation("收藏音乐")
    public Result collectMusic(@RequestParam @NotNull Long musicId){
        Long userId= UserContext.getUser();
        Collection collection=new Collection();
        collection.setMusicId(musicId);
        collection.setUserId(userId);
        boolean isCollectionExist=collectionService.isCollectionExist(collection);
        if(isCollectionExist){
            return Result.error(500,"请误重复收藏");
        }else {
            collectionService.collectMusic(collection);
            return Result.success();
        }

    }

    @DeleteMapping("")
    @ApiOperation("取消收藏")
    public Result deleteCollection(@RequestParam @NotNull Long musicId){
        Long userId=UserContext.getUser();
        Collection collection=new Collection();
        collection.setMusicId(musicId);
        collection.setUserId(userId);
        collectionService.deleteCollection(collection);
        return Result.success();
    }

}
