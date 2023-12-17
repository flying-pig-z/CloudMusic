package com.flyingpig.cloudmusic.songlist.controller;

import com.flyingpig.cloudmusic.songlist.dataobject.entity.SonglistMusic;
import com.flyingpig.cloudmusic.songlist.service.SonglistMusicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.flyingpig.cloudmusic.result.Result;

import java.util.List;

@Slf4j
@RestController
@Api("歌单相关接口")
@RequestMapping("/songlist-musics")
public class SonglistMusicController {
    @Autowired
    SonglistMusicService songlistMusicService;

    @ApiOperation("查看自己歌单中的所有歌曲信息")
    @GetMapping("/{id}/music-list")
    public Result listMusicBriefBySonglistId(@RequestHeader String Authorization, @PathVariable Long id){
        return Result.success(songlistMusicService.listMusicNameBySonglistId(id));
    }

    @ApiOperation("批量添加新的歌曲")
    @PostMapping("")
    public Result addSonglistMusicList(@RequestHeader String Authorization, @RequestBody List<SonglistMusic> songlistMusicList){
        songlistMusicService.addSonglistMusicList(songlistMusicList);
        return Result.success();
    }

    @ApiOperation("批量删除歌曲")
    @DeleteMapping("")
    public Result deleteSonglists(@RequestHeader String Authorization, @RequestParam List<Long> songlistMusicIds){
        songlistMusicService.deleteSonglists(songlistMusicIds);
        return Result.success();
    }


}
