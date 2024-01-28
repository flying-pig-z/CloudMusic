package com.flyingpig.cloudmusic.controller;

import com.flyingpig.cloudmusic.common.PageBean;
import com.flyingpig.cloudmusic.dataobject.entity.Comment;
import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.cloudmusic.service.CommentService;
import com.flyingpig.cloudmusic.util.JwtUtil;
import com.flyingpig.cloudmusic.util.UserContext;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/comments")
@Api("评论相关的接口")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("")
    @ApiOperation("查看音乐的评论")
    public Result pageCommentsByMusicId(@RequestParam Long musicId,
                                        @RequestParam Long pageNo, @RequestParam Long pageSize){
        Long userId= UserContext.getUser();
        PageBean result=commentService.pageCommentDTOByMusicId(musicId,pageNo,pageSize);
        return Result.success(result);
    }

    @PostMapping("")
    @ApiOperation("发表评论")
    public Result addComment(@RequestBody Comment addComment){
        if(addComment.getContent().equals(null)){
            return Result.error(500,"评论不能为空");
        }
        Long userId=UserContext.getUser();
        addComment.setUserId(userId);
        addComment.setTime(LocalDateTime.now());
        commentService.addComment(addComment);
        return Result.success();
    }

}
