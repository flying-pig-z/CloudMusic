package com.flyingpig.cloudmusic.music.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.flyingpig.cloudmusic.music.common.PageBean;
import com.flyingpig.cloudmusic.music.common.Result;
import com.flyingpig.cloudmusic.music.dataobject.dto.CommentDTO;
import com.flyingpig.cloudmusic.music.dataobject.dto.MusicDT0;
import com.flyingpig.cloudmusic.music.dataobject.entity.Comment;
import com.flyingpig.cloudmusic.music.service.CommentService;
import com.flyingpig.cloudmusic.music.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @GetMapping("")
    public Result pageCommentsByMusicId(@RequestHeader String Authorization, @RequestParam Long musicId,
                                        @RequestParam Long pageNo,@RequestParam Long pageSize){
        Claims claims = JwtUtil.parseJwt(Authorization);
        Long userId = Long.parseLong(claims.getSubject());
        PageBean result=commentService.pageCommentDTOByMusicId(musicId,pageNo,pageSize);
        return Result.success(result);
    }

    @PostMapping("")
    public Result addComment(@RequestHeader String Authorization, @RequestBody Comment addComment){
        Claims claims = JwtUtil.parseJwt(Authorization);
        Long userId = Long.parseLong(claims.getSubject());
        addComment.setUserId(userId);
        addComment.setTime(LocalDateTime.now());
        commentService.addComment(addComment);
        return Result.success();
    }
}
