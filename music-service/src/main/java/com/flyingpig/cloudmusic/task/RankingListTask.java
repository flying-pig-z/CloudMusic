package com.flyingpig.cloudmusic.task;

import com.flyingpig.cloudmusic.dataobject.dto.MusicInfoInRankList;
import com.flyingpig.cloudmusic.mapper.MusicMapper;
import com.flyingpig.cloudmusic.service.MusicService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        redisTemplate.opsForValue().set("musicCache::musicRankList", musicMapper.selectRankList());
    }
}
