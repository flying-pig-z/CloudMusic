package com.flyingpig.cloudmusic.songlist.controller;


import com.flyingpig.cloudmusic.songlist.dataobject.dto.SonglistInfo;
import com.flyingpig.cloudmusic.songlist.dataobject.entity.Songlist;
import com.flyingpig.cloudmusic.songlist.service.SonglistService;
import com.flyingpig.cloudmusic.util.UserContext;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.cloudmusic.util.JwtUtil;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/songlists")
public class SonglistController {
    @Autowired
    private SonglistService songlistService;

    @ApiOperation("添加歌单")
    @PostMapping
    public Result addSonglist(@RequestParam String songlistName){
        try {
            Long userId= UserContext.getUser();
            Songlist songlist=new Songlist(null,songlistName, userId);
            songlistService.addSonglist(songlist);
            return Result.success();
        } catch (Exception exception){
            return Result.error(500,"重复添加歌单");
        }


    }

    @ApiOperation("删除歌单")
    @DeleteMapping("/{id}")
    public Result deleteSonglistById(@PathVariable Long id){
        songlistService.deleteSonglistById(id);
        return Result.success();
    }

    @ApiOperation("返回用户所有歌单")
    @GetMapping
    public Result listSonglistByUserId(){
        Long userId=UserContext.getUser();
        List<SonglistInfo> songlistInfoList=songlistService.listSonglistByUserId(userId);
        return Result.success(songlistInfoList);
    }


}
