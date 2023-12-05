package com.flyingpig.cloudmusic.songlist.controller;

import com.flyingpig.cloudmusic.songlist.dataobject.dto.SonglistInfo;
import com.flyingpig.cloudmusic.songlist.service.SonglistMusicService;
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
public class SonglistMusicController {
    @Autowired
    SonglistMusicService songlistMusicService;

//    @GetMapping("/{id}/music-list")
//    public Result listMusicInfoBySonglistId(@RequestHeader String Authorization, @PathVariable Long id){
//        Claims claims = JwtUtil.parseJwt(Authorization);
//        Long userId = Long.parseLong(claims.getSubject());
//        List<SonglistInfo> songlistInfoList=songlistService.listSonglistByUserId(userId);
//        return Result.success();
//    }




}
