package com.flyingpig.cloudmusic.controller;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.flyingpig.cloudmusic.dataobject.dto.*;
import com.flyingpig.cloudmusic.dataobject.entity.Music;
import com.flyingpig.cloudmusic.dataobject.message.MusicUploadMessage;
import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.cloudmusic.service.MusicService;
import com.flyingpig.cloudmusic.util.AliOSSUtils;
import com.flyingpig.cloudmusic.util.JwtUtil;
import com.flyingpig.cloudmusic.util.MultipartFileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.amqp.core.Queue;

import java.io.IOException;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/musics")
@Api("音乐相关的接口")
public class MusicController {
    @Autowired
    private MusicService musicService;
    @Autowired
    AliOSSUtils aliOSSUtils;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    Queue musicQueue;

    @GetMapping("/{musicId}")
    @ApiOperation("音乐界面查看返回该音乐的所有信息")
    public Result selectMusicInfoById(@RequestHeader String Authorization, @PathVariable Long musicId){
        Long userId = Long.parseLong(JwtUtil.parseJwt(Authorization).getSubject());
        MusicInfo musicInfo=musicService.selectMusicInfoByUserIdAndMusicId(userId,musicId);
        return Result.success(musicInfo);
    }

    @SentinelResource("rank-list")
    @GetMapping("/rank-list")
    @ApiOperation("返回排行榜的音乐及这些音乐的相关信息")
    public Result selectRankList(){
        List<MusicInfoInRankList> result= musicService.selectRankList();
        return Result.success(result);
    }

    @GetMapping("/{musicId}/detail")
    public MusicDetail selectMusicDetailById(@PathVariable Long musicId){
        return musicService.selectMusicDetailByMusicId(musicId);
    }

    @GetMapping("/search")
    @ApiOperation("根据关键词搜索音乐")
    public Result searchMusic(@RequestParam String keyword){
        List<MusicIdAndName> result= musicService.searchMusic(keyword);
        return Result.success(result);
    }

    @PostMapping
    @ApiOperation("上传音乐")
    public Result addMusic(@RequestHeader String Authorization,
                           @RequestParam String name, @RequestParam String introduce, @RequestParam String singerName,
                           @RequestParam MultipartFile coverFile ,@RequestParam MultipartFile musicFile)throws IOException {
        if(!MultipartFileUtil.isMusicFile(musicFile)||!MultipartFileUtil.isImageFile(coverFile)){
            return Result.error(500,"文件类型错误");
        }
        System.out.println(coverFile.getOriginalFilename());
        Music music=new Music(null,name,introduce,null,null,null,null,null,singerName);
        music.setLikeNum(Long.parseLong("0"));
        music.setCollectNum(Long.parseLong("0"));
        music.setUploadUser(Long.parseLong(JwtUtil.parseJwt(Authorization).getSubject()));
        // 将文件信息发送到RabbitMQ队列中
        rabbitTemplate.convertAndSend(musicQueue.getName(), new MusicUploadMessage(music, coverFile, musicFile));
        return Result.success();
    }





}
