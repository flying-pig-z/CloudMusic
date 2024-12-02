package com.flyingpig.cloudmusic.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.cloudmusic.cache.StringCacheUtil;
import com.flyingpig.cloudmusic.file.AliOSSUtils;
import com.flyingpig.cloudmusic.music.dataobject.dto.MusicDetail;
import com.flyingpig.cloudmusic.music.dataobject.dto.MusicInfo;
import com.flyingpig.cloudmusic.music.dataobject.dto.MusicInfoInRankList;
import com.flyingpig.cloudmusic.music.dataobject.dto.UploadMusicInfo;
import com.flyingpig.cloudmusic.music.dataobject.entity.Music;
import com.flyingpig.cloudmusic.music.mapper.MusicMapper;
import com.flyingpig.cloudmusic.security.util.UserContext;
import com.flyingpig.cloudmusic.music.service.MusicService;
import com.flyingpig.feign.clients.MusicRelatedClient;
import com.flyingpig.feign.clients.UserClients;
import com.flyingpig.feign.dataobject.dto.UserCollectInfo;
import com.flyingpig.feign.dataobject.dto.UserInfo;
import com.flyingpig.feign.dataobject.dto.UserLikeInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.flyingpig.cloudmusic.music.constant.RedisConstants.*;

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
    AliOSSUtils aliOSSUtils;

    @Autowired
    UserClients userClients;
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    StringCacheUtil myStringRedisTemplate;

    @Autowired
    MusicRelatedClient musicRelatedClient;


    @Override
    public MusicInfo selectMusicInfoByUserIdAndMusicId(Long userId, Long musicId) {
        Music music = myStringRedisTemplate.safeGetWithLock(MUSIC_INFO_KEY + musicId, Music.class, () -> {
            return musicMapper.selectById(musicId);
        }, MUSIC_INFO_TTL, TimeUnit.DAYS);
        MusicInfo result = new MusicInfo();
        BeanUtils.copyProperties(music, result); // 将 music 对象属性值复制到 result 对象上
        UserLikeInfo userLikeInfo = musicRelatedClient.getLikeInfoByUserIdAndMusicId(userId, musicId);
        result.setLikeOrNot(userLikeInfo.getLikeOrNot());
        UserCollectInfo userCollectInfo = musicRelatedClient.getCollectInfoByUserIdAndMusicId(userId, musicId);
        result.setCollectOrNot(userCollectInfo.getCollectOrNot());
        result.setUploadUserName(userClients.selectUserInfoByUserId(music.getUploadUser()).getUsername());
        return result;
    }

    @Override
    public List<MusicInfoInRankList> selectRankList() {
        ValueOperations<String, List<MusicInfoInRankList>> operation = redisTemplate.opsForValue();
        return operation.get(MUSIC_RANKLIST_KEY);
    }

    @Override
    public MusicDetail selectMusicDetailByMusicId(Long musicId) {
        Music music = myStringRedisTemplate.safeGetWithLock(MUSIC_INFO_KEY + musicId, Music.class, () -> {
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
        Music music = myStringRedisTemplate.safeGetWithLock(MUSIC_INFO_KEY + musicId, Music.class, () -> {
            return musicMapper.selectById(musicId);
        }, MUSIC_INFO_TTL, TimeUnit.DAYS);
        if (music.getUploadUser().equals(userId)) {
            aliOSSUtils.deleteFileByUrl(music.getCoverPath());
            aliOSSUtils.deleteFileByUrl(music.getMusicPath());
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

    @Override
    public Boolean incOrDecLikeNum(Long musicId, String mode) {
        musicMapper.update(null,
                new LambdaUpdateWrapper<Music>()
                        .eq(Music::getId, musicId)
                        .setSql("like_num = like_num + 1"));
        return true;
    }

    @Override
    public Boolean incOrDecCollectionNum(Long musicId, String mode) {
        musicMapper.update(null,
                new LambdaUpdateWrapper<Music>()
                        .eq(Music::getId, musicId)
                        .setSql("collect_num = collect_num + 1"));
        return true;
    }

    @Override
    public List<MusicInfo> listRandomMusics(Integer num) {
        List<Long> musicIds = musicMapper.getAllMusicIds(); // 先查询所有音乐 ID
        Collections.shuffle(musicIds); // 使用 Java 的随机化
        List<MusicInfo> musicInfos = new ArrayList<>();
        for (int i = 0; i < num && i < musicIds.size(); i++) {
            musicInfos.add(
                    selectMusicInfoByUserIdAndMusicId(
                            UserContext.getUser().getUserId(), musicIds.get(i)));
        }
        return musicInfos;
    }


}
