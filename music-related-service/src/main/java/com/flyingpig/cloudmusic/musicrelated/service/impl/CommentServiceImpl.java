package com.flyingpig.cloudmusic.musicrelated.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.cloudmusic.musicrelated.common.PageBean;
import com.flyingpig.cloudmusic.musicrelated.dataobject.dto.CommentDTO;
import com.flyingpig.cloudmusic.musicrelated.dataobject.entity.Comment;
import com.flyingpig.cloudmusic.musicrelated.dataobject.vo.AddComment;
import com.flyingpig.cloudmusic.musicrelated.mapper.CommentMapper;
import com.flyingpig.cloudmusic.musicrelated.service.CommentService;
import com.flyingpig.cloudmusic.security.util.UserContext;
import com.flyingpig.feign.clients.UserClients;
import com.flyingpig.feign.dataobject.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    UserClients userClients;

    @Override
    public PageBean pageCommentDTOByMusicId(Long musicId, Long pageNo, Long pageSize) {
        IPage<Comment> commentIPage = new Page<>(pageNo, pageSize);
        commentMapper.selectPage(commentIPage, new LambdaQueryWrapper<Comment>()
                .eq(Comment::getMusicId, musicId)
                .orderByDesc(Comment::getTime));
        //将实体类列表封装成dto列表
        List<CommentDTO> resultList = new ArrayList<>();
        for (Comment comment : commentIPage.getRecords()) {
            CommentDTO commentDTO = new CommentDTO();
            //封装评论信息
            BeanUtils.copyProperties(comment, commentDTO);
            //封装用户信息
            UserInfo userInfo = userClients.selectUserInfoByUserId(comment.getUserId());
            BeanUtils.copyProperties(userInfo, commentDTO);
            resultList.add(commentDTO);
        }
        return new PageBean(commentIPage.getTotal(), resultList);
    }

    @Override
    public void addComment(AddComment addComment) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(addComment, comment);
        comment.setUserId(UserContext.getUser().getUserId());
        comment.setTime(LocalDateTime.now());
        commentMapper.insert(comment);
    }
}
