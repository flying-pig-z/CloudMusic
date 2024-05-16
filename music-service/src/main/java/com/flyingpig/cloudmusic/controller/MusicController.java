package com.flyingpig.cloudmusic.controller;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.flyingpig.cloudmusic.aop.BeforeAuthorize;
import com.flyingpig.cloudmusic.dataobject.dto.*;
import com.flyingpig.cloudmusic.dataobject.entity.Music;
import com.flyingpig.cloudmusic.dataobject.message.MusicUploadMessage;
import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.cloudmusic.service.MusicService;
import com.flyingpig.cloudmusic.util.AliOSSUtils;
import com.flyingpig.cloudmusic.util.MultipartFileUtil;
import com.flyingpig.cloudmusic.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.flyingpig.cloudmusic.constant.RabbitMQConstants.MUSIC_UPLOAD_EXCHANGE_NAME;


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


    @GetMapping("/{musicId}")
    @ApiOperation("音乐界面查看返回该音乐的所有信息")
    public Result selectMusicInfoById(@PathVariable Long musicId) {
        MusicInfo musicInfo = musicService.selectMusicInfoByUserIdAndMusicId(UserContext.getUser().getUserId(), musicId);
        return Result.success(musicInfo);
    }

    @SentinelResource("rank-list")
    @GetMapping("/rank-list")
    @ApiOperation("返回排行榜的音乐及这些音乐的相关信息")
    public Result selectRankList() {
        List<MusicInfoInRankList> result = musicService.selectRankList();
        return Result.success(result);
    }

    @GetMapping("/{musicId}/detail")
    public MusicDetail selectMusicDetailById(@PathVariable Long musicId) {
        return musicService.selectMusicDetailByMusicId(musicId);
    }



    @PostMapping
    @ApiOperation("上传音乐")
    public Result addMusic(@RequestParam String name, @RequestParam String introduce, @RequestParam String singerName,
                           @RequestParam MultipartFile coverFile, @RequestParam MultipartFile musicFile) throws IOException {
        if (!MultipartFileUtil.isMusicFile(musicFile) || !MultipartFileUtil.isImageFile(coverFile)) {
            return Result.error(500, "文件类型错误");
        }
        System.out.println(coverFile.getOriginalFilename());
        Music music = new Music(null, name, introduce, null, null, null, null, null, singerName);
        music.setLikeNum(Long.parseLong("0"));
        music.setCollectNum(Long.parseLong("0"));
        music.setUploadUser(UserContext.getUser().getUserId());
        // 将文件信息发送到RabbitMQ队列中
        rabbitTemplate.convertAndSend(MUSIC_UPLOAD_EXCHANGE_NAME, "", new MusicUploadMessage());
        return Result.success();
    }

    @DeleteMapping("")
    @ApiOperation("删除自己上传的音乐")
    public Result deleteMusic(@RequestParam Long musicId) {
        musicService.deleteMusicByIdAndUserId(musicId, UserContext.getUser().getUserId());
        return Result.success();
    }

    @GetMapping("/upload-music")
    public Result selectUploadMusics() {
        List<UploadMusicInfo> result = musicService.selectUploadMusics();
        return Result.success(result);
    }

    @DeleteMapping("/admin")
    @BeforeAuthorize(role = "admin")
    @ApiOperation("管理员删除任何上传的音乐")
    public Result deleteMusicByAdmin(Long musicId) {
        musicService.deleteMusicById(musicId);
        return Result.success();
    }

}
