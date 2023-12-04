package com.flyingpig.cloudmusic.controller;

import com.flyingpig.cloudmusic.common.Result;
import com.flyingpig.cloudmusic.dataobject.entity.Collection;
import com.flyingpig.cloudmusic.service.CollectionService;
import com.flyingpig.cloudmusic.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/collections")
public class CollectionController {
    @Autowired
    CollectionService collectionService;

    @PostMapping("")
    public Result collectMusic(@RequestHeader String Authorization, @RequestParam Long musicId){
        Claims claims = JwtUtil.parseJwt(Authorization);
        Long userId = Long.parseLong(claims.getSubject());
        Collection collection=new Collection();
        collection.setMusicId(musicId);
        collection.setUserId(userId);
        collectionService.collectMusic(collection);
        return Result.success();
    }

    @DeleteMapping("")
    public Result deleteLike(@RequestHeader String Authorization, @RequestParam Long musicId){
        Claims claims = JwtUtil.parseJwt(Authorization);
        Long userId = Long.parseLong(claims.getSubject());
        Collection collection=new Collection();
        collection.setMusicId(musicId);
        collection.setUserId(userId);
        collectionService.deleteCollection(collection);
        return Result.success();
    }

}
