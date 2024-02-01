package com.flyingpig.cloudmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.cloudmusic.dataobject.dto.MusicDetail;
import com.flyingpig.cloudmusic.dataobject.dto.MusicIdAndName;
import com.flyingpig.cloudmusic.dataobject.dto.MusicInfoInRankList;
import com.flyingpig.cloudmusic.dataobject.entity.Collection;
import com.flyingpig.cloudmusic.dataobject.entity.Like;
import com.flyingpig.cloudmusic.dataobject.entity.Music;
import com.flyingpig.cloudmusic.dataobject.dto.MusicInfo;
import com.flyingpig.cloudmusic.mapper.CollectionMapper;
import com.flyingpig.cloudmusic.mapper.LikeMapper;
import com.flyingpig.cloudmusic.mapper.MusicMapper;
import com.flyingpig.cloudmusic.service.MusicService;
import com.flyingpig.feign.clients.UserClients;
import com.flyingpig.feign.dataobject.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.flyingpig.cloudmusic.util.RedisConstants.MUSIC_RANKLIST_KEY;

/**
 * @author flyingpig
 */
@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class MusicServiceImpl implements MusicService {
    @Autowired
    private MusicMapper musicMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private CollectionMapper collectionMapper;
    @Autowired
    private UserClients userClients;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public MusicInfo selectMusicInfoByUserIdAndMusicId(Long userId, Long musicId) {
        Music music = musicMapper.selectById(musicId);
        MusicInfo result = new MusicInfo();
        BeanUtils.copyProperties(music, result); // 将 music 对象属性值复制到 result 对象上
        LambdaQueryWrapper<Like> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        likeLambdaQueryWrapper.eq(Like::getUserId, userId).eq(Like::getMusicId, musicId);
        Like like = likeMapper.selectOne(likeLambdaQueryWrapper);
        if (like == null) {
            result.setLikeOrNot(false);
        } else {
            result.setLikeOrNot(true);
        }
        LambdaQueryWrapper<Collection> collectionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectionLambdaQueryWrapper.eq(Collection::getUserId, userId).eq(Collection::getMusicId, musicId);
        Collection collection = collectionMapper.selectOne(collectionLambdaQueryWrapper);
        if (collection == null) {
            result.setCollectOrNot(false);
        } else {
            result.setCollectOrNot(true);
        }
        UserInfo uploadUser = userClients.selectUserInfoByUserId(music.getUploadUser());
        result.setUploadUserName(uploadUser.getUsername());
        return result;
    }

    @Override
    public List<MusicInfoInRankList> selectRankList() {
        ValueOperations<String, List<MusicInfoInRankList>> operation = redisTemplate.opsForValue();
        List<MusicInfoInRankList> result = operation.get(MUSIC_RANKLIST_KEY);
        return result;
    }

    @Override
    public MusicDetail selectMusicDetailByMusicId(Long musicId) {
        Music music = musicMapper.selectById(musicId);
        MusicDetail result = new MusicDetail();
        BeanUtils.copyProperties(music, result); // 将 music 对象属性值复制到 result 对象上
        UserInfo uploadUser = userClients.selectUserInfoByUserId(music.getUploadUser());
        result.setUploadUserName(uploadUser.getUsername());
        return result;
    }

    @Override
    public List<MusicIdAndName> searchMusic(String keyword) {
        LambdaQueryWrapper<Music> musicLambdaQueryWrapper = new LambdaQueryWrapper<>();
        musicLambdaQueryWrapper.like(Music::getName, keyword);
        List<Music> musics = musicMapper.selectList(musicLambdaQueryWrapper);
        List<MusicIdAndName> result = new ArrayList<>();
        for (Music music : musics) {
            MusicIdAndName musicIdAndName = new MusicIdAndName();
            BeanUtils.copyProperties(music, musicIdAndName);
            result.add(musicIdAndName);
        }
        return result;
    }

    @Override
    public void addMusic(Music music) {
        musicMapper.insert(music);
    }


}
