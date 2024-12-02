package com.flyingpig.cloudmusic.music.controller;

import com.flyingpig.cloudmusic.music.dataobject.dto.MusicIdAndName;
import com.flyingpig.cloudmusic.security.aop.BeforeAuthorize;
import com.flyingpig.cloudmusic.music.service.MusicSearchService;
import com.flyingpig.cloudmusic.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/musics")
@Api("音乐相关的接口")
public class MusicSearchController {

    @Autowired
    private MusicSearchService musicSearchService;

    @GetMapping("/search")
    @ApiOperation("根据关键词搜索音乐")
    public Result searchMusic(@RequestParam String keyword) {
        List<MusicIdAndName> result = musicSearchService.searchMusic(keyword);
        return Result.success(result);
    }

    @GetMapping("/es-search")
    @ApiOperation("根据关键词搜索音乐,使用ES")
    public Result searchMusicByEs(@RequestParam String keyword) throws IOException {
        return Result.success(musicSearchService.searchMusicByEs(keyword));
    }

    @GetMapping("/es-init")
    @ApiOperation("根据关键词搜索音乐,使用ES")
    @BeforeAuthorize(role = "admin")
    public Result musicEsInit() throws IOException {
        musicSearchService.musicEsInit();
        return Result.success();
    }
}
