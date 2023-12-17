package com.flyingpig.cloudmusic.controller;

import com.flyingpig.cloudmusic.dataobject.entity.Collection;
import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.cloudmusic.service.CollectionService;
import com.flyingpig.cloudmusic.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/collections")
@Api("收藏相关的接口")
public class CollectionController {
    @Autowired
    CollectionService collectionService;

    @PostMapping("")
    @ApiOperation("收藏音乐")
    public Result collectMusic(@RequestHeader String Authorization, @RequestParam Long musicId){
        Claims claims = JwtUtil.parseJwt(Authorization);
        Long userId = Long.parseLong(claims.getSubject());
        Collection collection=new Collection();
        collection.setMusicId(musicId);
        collection.setUserId(userId);
        try{
            collectionService.collectMusic(collection);
            return Result.success();
        }catch (Exception exception){
            return Result.error(500,"请误重复收藏");
        }

    }

    @DeleteMapping("")
    @ApiOperation("取消收藏")
    public Result deleteCollection(@RequestHeader String Authorization, @RequestParam Long musicId){
        Claims claims = JwtUtil.parseJwt(Authorization);
        Long userId = Long.parseLong(claims.getSubject());
        Collection collection=new Collection();
        collection.setMusicId(musicId);
        collection.setUserId(userId);
        collectionService.deleteCollection(collection);
        return Result.success();
    }

}
