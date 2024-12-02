package com.flyingpig.cloudmusic.music.controller;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.flyingpig.cloudmusic.file.AliOSSUtils;
import com.flyingpig.cloudmusic.idempotent.annotation.Idempotent;
import com.flyingpig.cloudmusic.idempotent.enums.IdempotentTypeEnum;
import com.flyingpig.cloudmusic.music.dataobject.entity.Music;
import com.flyingpig.cloudmusic.music.dataobject.message.MusicUploadMessage;
import com.flyingpig.cloudmusic.music.dataobject.dto.MusicDetail;
import com.flyingpig.cloudmusic.music.dataobject.dto.UploadMusicInfo;
import com.flyingpig.cloudmusic.music.mapper.MusicMapper;
import com.flyingpig.cloudmusic.music.mq.MusicUploadListener;
import com.flyingpig.cloudmusic.music.mq.SensitiveWordFilter;
import com.flyingpig.cloudmusic.music.mq.SensitiveWordLoader;
import com.flyingpig.cloudmusic.security.aop.BeforeAuthorize;
import com.flyingpig.cloudmusic.security.util.UserContext;
import com.flyingpig.cloudmusic.music.service.MusicService;
import com.flyingpig.cloudmusic.music.util.MultipartFileUtil;
import com.flyingpig.cloudmusic.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.flyingpig.cloudmusic.music.constant.MQConstants.MUSIC_UPLOAD_EXCHANGE_NAME;


@Slf4j
@RestController
@RequestMapping("/musics")
@Api("音乐相关的接口")
public class MusicController {
    @Autowired
    private MusicService musicService;

    @Autowired
    RabbitTemplate rabbitTemplate;


    @Autowired
    AliOSSUtils aliOSSUtils;

    @GetMapping("/{musicId}")
    @ApiOperation("音乐界面查看返回该音乐的所有信息")
    public Result selectMusicInfoById(@PathVariable Long musicId) {
        return Result.success(musicService.selectMusicInfoByUserIdAndMusicId(UserContext.getUser().getUserId(), musicId));
    }

    @GetMapping("/random")
    @ApiOperation("返回三首随机音乐")
    public Result selectRandomMusic(Integer num) {
        return Result.success(musicService.listRandomMusics(num));
    }

    @SentinelResource("rank-list")
    @GetMapping("/rank-list")
    @ApiOperation("返回排行榜的音乐及这些音乐的相关信息")
    public Result selectRankList() {
        return Result.success(musicService.selectRankList());
    }

    @PostMapping
    @ApiOperation("上传音乐")
    @Idempotent(
            type = IdempotentTypeEnum.SPEL,
            uniqueKeyPrefix = "music-upload:lock_upload_music:",
            key = "#userId + ':' + #name + ':' + #singerName"
    )
    public Result uploadMusic(@RequestHeader Long userId, @RequestParam @NotNull String name, @RequestParam @NotNull String introduce,
                              @RequestParam @NotNull String singerName, @RequestParam MultipartFile coverFile, @RequestParam MultipartFile musicFile) throws IOException {
        log.info("上传用户：{}，上传封面：{}, 上传音乐：{}", UserContext.getUser().getUserId(), coverFile.getOriginalFilename(), musicFile.getOriginalFilename());
        if (!MultipartFileUtil.isMusicFile(musicFile) || !MultipartFileUtil.isImageFile(coverFile)) {
            return Result.error(500, "文件类型错误");
        }
        // 异步审核，上传，保存音乐文件
        rabbitTemplate.convertAndSend(MUSIC_UPLOAD_EXCHANGE_NAME, "", new MusicUploadMessage(
                new Music().setName(name).setIntroduce(introduce)
                        .setLikeNum(0L).setCollectNum(0L).setUploadTime(LocalDateTime.now())
                        .setUploadUser(UserContext.getUser().getUserId()).setSingerName(singerName), coverFile, musicFile));
        log.info("上传结束");
        return Result.success();
    }


    @DeleteMapping("")
    @ApiOperation("删除自己上传的音乐")
    public Result deleteMusic(@RequestParam Long musicId) {
        musicService.deleteMusicByIdAndUserId(musicId, UserContext.getUser().getUserId());
        return Result.success();
    }

    @GetMapping("/upload-music")
    @ApiOperation("查询自己上传的音乐")
    public Result selectUploadMusics() {
        List<UploadMusicInfo> result = musicService.selectUploadMusics();
        return Result.success(result);
    }


    @GetMapping("/{musicId}/detail")
    @ApiOperation("返回音乐详细信息[远程调用]")
    public MusicDetail selectMusicDetailById(@PathVariable Long musicId) {
        return musicService.selectMusicDetailByMusicId(musicId);
    }

    @PutMapping("/{musicId}/like-num")
    @ApiOperation("删除或更新点赞数量[远程调用]")
    public Boolean incOrDecLikeNum(@PathVariable("musicId") Long musicId, String mode) {
        return musicService.incOrDecLikeNum(musicId, mode);
    }

    @PutMapping("/{musicId}/collection-num")
    @ApiOperation("删除或更新收藏数量[远程调用]")
    public Boolean incOrDecCollectionNum(@PathVariable("musicId") Long musicId, String mode) {
        return musicService.incOrDecCollectionNum(musicId, mode);
    }


    @DeleteMapping("/admin")
    @BeforeAuthorize(role = "admin")
    @ApiOperation("管理员删除任何上传的音乐")
    public Result deleteMusicByAdmin(Long musicId) {
        musicService.deleteMusicById(musicId);
        return Result.success();
    }

}
