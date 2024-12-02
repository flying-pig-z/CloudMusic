package com.flyingpig.cloudmusic.musicrelated.service;


import com.flyingpig.cloudmusic.musicrelated.common.PageBean;
import com.flyingpig.cloudmusic.musicrelated.dataobject.entity.Comment;
import com.flyingpig.cloudmusic.musicrelated.dataobject.vo.AddComment;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    PageBean pageCommentDTOByMusicId(Long musicId, Long pageNo, Long pageSize);

    void addComment(AddComment addComment);
}
