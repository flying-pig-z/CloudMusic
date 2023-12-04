package com.flyingpig.cloudmusic.service;


import com.flyingpig.cloudmusic.common.PageBean;
import com.flyingpig.cloudmusic.dataobject.entity.Comment;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    PageBean pageCommentDTOByMusicId(Long musicId, Long pageNo, Long pageSize);

    void addComment(Comment addComment);
}
