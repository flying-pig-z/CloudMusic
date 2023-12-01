package com.flyingpig.cloudmusic.music.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.flyingpig.cloudmusic.music.common.PageBean;
import com.flyingpig.cloudmusic.music.dataobject.dto.CommentDTO;
import com.flyingpig.cloudmusic.music.dataobject.entity.Comment;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    PageBean pageCommentDTOByMusicId(Long musicId, Long pageNo, Long pageSize);

    void addComment(Comment addComment);
}
