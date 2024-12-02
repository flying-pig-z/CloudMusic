package com.flyingpig.cloudmusic.musicrelated.controller;

import com.flyingpig.cloudmusic.musicrelated.common.PageBean;
import com.flyingpig.cloudmusic.musicrelated.dataobject.entity.Comment;
import com.flyingpig.cloudmusic.musicrelated.dataobject.vo.AddComment;
import com.flyingpig.cloudmusic.musicrelated.service.CommentService;
import com.flyingpig.cloudmusic.security.util.UserContext;
import com.flyingpig.cloudmusic.web.Result;
import com.flyingpig.cloudmusic.web.StatusCode;
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
                                        @RequestParam Long pageNo, @RequestParam Long pageSize) {
        return Result.success(commentService.pageCommentDTOByMusicId(musicId, pageNo, pageSize));
    }

    @PostMapping("")
    @ApiOperation("发表评论")
    public Result addComment(@RequestBody AddComment addComment) {
        if ("".equals(addComment.getContent()) || addComment.getContent() == null) {
            return Result.error(StatusCode.SERVERERROR, "评论不能为空");
        }
        commentService.addComment(addComment);
        return Result.success();
    }

}
