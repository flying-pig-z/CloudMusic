package com.flyingpig.cloudmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.cloudmusic.dataobject.dto.*;
import com.flyingpig.cloudmusic.dataobject.entity.Collection;
import com.flyingpig.cloudmusic.dataobject.entity.Music;
import com.flyingpig.cloudmusic.mapper.CollectionMapper;
import com.flyingpig.cloudmusic.mapper.MusicMapper;
import com.flyingpig.cloudmusic.service.MusicService;
import com.flyingpig.cloudmusic.cache.LikeCache;
import com.flyingpig.cloudmusic.util.ElasticSearchUtil;
import com.flyingpig.cloudmusic.util.cache.MyStringRedisTemplate;
import com.flyingpig.cloudmusic.util.UserContext;
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
import java.util.concurrent.TimeUnit;

import static com.flyingpig.cloudmusic.constant.RedisConstants.*;

/**
 * @author flyingpig
 */
@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements MusicService {
    @Autowired
    MusicMapper musicMapper;
    @Autowired
    CollectionMapper collectionMapper;
    @Autowired
    UserClients userClients;
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    MyStringRedisTemplate myStringRedisTemplate;

    @Autowired
    LikeCache likeCache;

    @Autowired
    ElasticSearchUtil elasticSearchUtil;


    @Override
    public MusicInfo selectMusicInfoByUserIdAndMusicId(Long userId, Long musicId) {
        Music music = myStringRedisTemplate.safeGetWithLock(MUSIC_INFO_KEY + musicId, Music.class, () -> {
            return musicMapper.selectById(musicId);
        }, MUSIC_INFO_TTL, TimeUnit.DAYS);
        MusicInfo result = new MusicInfo();
        BeanUtils.copyProperties(music, result); // 将 music 对象属性值复制到 result 对象上
        result.setLikeOrNot(likeCache.judgeLikeOrNotByMusicIdAndUserId(musicId, userId));
        LambdaQueryWrapper<Collection> collectionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectionLambdaQueryWrapper.eq(Collection::getUserId, userId).eq(Collection::getMusicId, musicId);
        Collection collection = collectionMapper.selectOne(collectionLambdaQueryWrapper);
        result.setCollectOrNot(collection != null);
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
        Music music = myStringRedisTemplate.safeGetWithLock(MUSIC_INFO_KEY+musicId, Music.class, ()->{
            return musicMapper.selectById(musicId);
        }, MUSIC_INFO_TTL, TimeUnit.DAYS);
        MusicDetail result = new MusicDetail();
        BeanUtils.copyProperties(music, result); // 将 music 对象属性值复制到 result 对象上
        UserInfo uploadUser = userClients.selectUserInfoByUserId(music.getUploadUser());
        result.setUploadUserName(uploadUser.getUsername());
        return result;
    }



    @Override
    public void deleteMusicByIdAndUserId(Long musicId, Long userId) {
        if (myStringRedisTemplate.safeGetWithLock(MUSIC_INFO_KEY + musicId, Music.class, () -> {
                    return musicMapper.selectById(musicId);
                }, MUSIC_INFO_TTL, TimeUnit.DAYS)
                .getUploadUser().equals(userId)) {
            musicMapper.deleteById(musicId);
            myStringRedisTemplate.delete(MUSIC_INFO_KEY + musicId);
        }
    }

    @Override
    public List<UploadMusicInfo> selectUploadMusics() {
        LambdaQueryWrapper<Music> musicLambdaQueryWrapper = new LambdaQueryWrapper<>();
        musicLambdaQueryWrapper.eq(Music::getUploadUser, UserContext.getUser().getUserId());
        List<Music> musicList = musicMapper.selectList(musicLambdaQueryWrapper);
        List<UploadMusicInfo> uploadMusicInfos = new ArrayList<>();
        for (Music music : musicList) {
            UploadMusicInfo uploadMusicInfo = new UploadMusicInfo();
            BeanUtils.copyProperties(music, uploadMusicInfo);
            uploadMusicInfos.add(uploadMusicInfo);
        }
        return uploadMusicInfos;
    }

    @Override
    public void deleteMusicById(Long musicId) {
        musicMapper.deleteById(musicId);
        myStringRedisTemplate.delete(MUSIC_INFO_KEY + musicId);
    }


}
