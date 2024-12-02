//package com.flyingpig.cloudmusic.task;
//
//import com.flyingpig.cloudmusic.mapper.MusicMapper;
//import com.xxl.job.core.handler.annotation.XxlJob;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import static com.flyingpig.cloudmusic.constant.RedisConstants.MUSIC_RANKLIST_KEY;
//
//@Component
//@Slf4j
//public class RankingListDistributedTask {
//    @Autowired
//    MusicMapper musicMapper;
//    @Autowired
//    RedisTemplate redisTemplate;
//    //每天0点和12点进行排行榜的更新
//    @XxlJob("rankingListJob")
//    public void rankingListTask(){
//        redisTemplate.opsForValue().set(MUSIC_RANKLIST_KEY, musicMapper.selectRankList());
//    }
//}
