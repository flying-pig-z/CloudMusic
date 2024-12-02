package com.flyingpig.cloudmusic.musicrelated.util;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class RedissionUtil {

    public Boolean tryLockWithRedisson(RLock lock, long timeout, TimeUnit timeUnit) {
        try {
            return lock.tryLock(timeout, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // 记录日志
            log.error("线程 {} 在尝试获取锁时被中断: {}", Thread.currentThread().getName(), lock.getName(), e);
            // 或者根据业务需求选择是否抛出异常
            throw new RuntimeException("获取锁时发生中断异常", e);
        }
    }
}
