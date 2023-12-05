package com.flyingpig.cloudmusic.songlist.controller;


import com.flyingpig.cloudmusic.songlist.dataobject.dto.SonglistInfo;
import com.flyingpig.cloudmusic.songlist.dataobject.entity.Songlist;
import com.flyingpig.cloudmusic.songlist.service.SonglistService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import result.Result;
import util.JwtUtil;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/songlists")
public class SonglistController {
    @Autowired
    private SonglistService songlistService;

    @PostMapping
    public Result addSonglist(@RequestHeader String Authorization, @RequestParam String songlistName){
        try {
            Claims claims = JwtUtil.parseJwt(Authorization);
            Long userId = Long.parseLong(claims.getSubject());
            Songlist songlist=new Songlist(null,songlistName, userId);
            songlistService.addSonglist(songlist);
            return Result.success();
        } catch (Exception exception){
            return Result.error("重复添加歌单");
        }


    }

    @DeleteMapping("/{id}")
    public Result deleteSonglistById(@RequestHeader String Authorization, @PathVariable Long id){
        songlistService.deleteSonglistById(id);
        return Result.success();
    }

    @GetMapping
    public Result listSonglistByUserId(@RequestHeader String Authorization){
        Claims claims = JwtUtil.parseJwt(Authorization);
        Long userId = Long.parseLong(claims.getSubject());
        List<SonglistInfo> songlistInfoList=songlistService.listSonglistByUserId(userId);
        return Result.success(songlistInfoList);
    }


}
