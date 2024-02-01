package com.flyingpig.cloudmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.cloudmusic.dataobject.dto.CommentDTO;
import com.flyingpig.cloudmusic.mapper.CommentMapper;
import com.flyingpig.cloudmusic.common.PageBean;
import com.flyingpig.cloudmusic.dataobject.entity.Comment;
import com.flyingpig.cloudmusic.service.CommentService;
import com.flyingpig.feign.clients.UserClients;
import com.flyingpig.feign.dataobject.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getMusicId, musicId);
        commentLambdaQueryWrapper.orderByDesc(Comment::getTime); // 按照时间字段降序排序
        commentMapper.selectPage(commentIPage, commentLambdaQueryWrapper);
        List<Comment> commentList = commentIPage.getRecords();
        //将实体类列表封装成dto列表
        List<CommentDTO> resultList = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentDTO commentDTO = new CommentDTO();
            //封装评论信息
            BeanUtils.copyProperties(comment, commentDTO);
            //封装用户信息
            UserInfo userInfo = userClients.selectUserInfoByUserId(comment.getUserId());
            BeanUtils.copyProperties(userInfo, commentDTO);
            resultList.add(commentDTO);
        }
        PageBean resultPage = new PageBean(commentIPage.getTotal(), resultList);
        return resultPage;
    }

    @Override
    public void addComment(Comment addComment) {
        commentMapper.insert(addComment);
    }
}
