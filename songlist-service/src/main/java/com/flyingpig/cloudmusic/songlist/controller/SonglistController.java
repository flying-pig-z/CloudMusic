package com.flyingpig.cloudmusic.songlist.controller;


import com.flyingpig.cloudmusic.security.aop.BeforeAuthorize;
import com.flyingpig.cloudmusic.security.util.UserContext;
import com.flyingpig.cloudmusic.songlist.dataobject.dto.SonglistInfo;
import com.flyingpig.cloudmusic.songlist.dataobject.entity.Songlist;
import com.flyingpig.cloudmusic.songlist.service.SonglistService;
import com.flyingpig.cloudmusic.web.StatusCode;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import com.flyingpig.cloudmusic.result.Result;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/songlists")
public class SonglistController {
    @Autowired
    private SonglistService songlistService;

    @ApiOperation("添加歌单")
    @PostMapping
    public Result addSonglist(@RequestParam String songlistName) {
        try {
            return Result.success(songlistService.addSonglist(new Songlist(null, songlistName, UserContext.getUser().getUserId())));
        } catch (DuplicateKeyException exception) {
            throw new RuntimeException("重复添加歌单");
        }
    }

    @ApiOperation("删除歌单")
    @DeleteMapping("/{id}")
    public Result deleteSonglistById(@PathVariable Long id) {
        songlistService.deleteSonglistById(id);
        return Result.success();
    }

    @ApiOperation("返回用户所有歌单")
    @GetMapping
    public Result listSonglistByUserId() {
        return Result.success(songlistService.listSonglistByUserId(UserContext.getUser().getUserId()));
    }

    @ApiOperation("管理员查看用户所有歌单")
    @BeforeAuthorize(role = "admin")
    @GetMapping("/admin")
    public Result adminListSonglistByUserId(Long userId) {
        return Result.success(songlistService.listSonglistByUserId(userId));
    }


}
