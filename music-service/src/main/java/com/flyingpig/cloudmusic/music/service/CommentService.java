package com.flyingpig.cloudmusic.music.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.flyingpig.cloudmusic.music.common.PageBean;
import com.flyingpig.cloudmusic.music.dataobject.dto.CommentDTO;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    PageBean pageCommentDTOByMusicId(Long musicId, Long pageNo, Long pageSize);
}
