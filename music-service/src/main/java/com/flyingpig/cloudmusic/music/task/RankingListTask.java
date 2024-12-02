package com.flyingpig.cloudmusic.music.task;

import com.flyingpig.cloudmusic.music.mapper.MusicMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.flyingpig.cloudmusic.music.constant.RedisConstants.MUSIC_RANKLIST_KEY;

@Component
@Slf4j
public class RankingListTask {
    @Autowired
    MusicMapper musicMapper;
    @Autowired
    RedisTemplate redisTemplate;
    //每天0点和12点进行排行榜的更新
    @Scheduled(cron = "0 */5 * * * *")
    public void rankingListTask(){
        redisTemplate.opsForValue().set(MUSIC_RANKLIST_KEY, musicMapper.selectRankList());
    }
}
