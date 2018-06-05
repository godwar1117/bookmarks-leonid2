package com.bookmarks.distributed;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 分布式锁
 * Created by Leonid on 18/6/5.
 */
public class DistributedLock extends ReentrantLock {

    private final RedisTemplate<String, String> redisTemplate;

    public DistributedLock(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 尝试获取锁
     * @param lockname
     * @param time
     * @param unit
     * @return
     * @throws InterruptedException
     */
    public boolean tryLock(String lockname, long time, TimeUnit unit) throws InterruptedException {
        boolean res = super.tryLock(time, unit);
        if (!res) {
            return res;
        }
        String lock = redisTemplate.opsForValue().get(lockname);
        if (lock != null && lock.equals("1")) {
            return false;
        }
        redisTemplate.opsForValue().set(lockname, "1");
        return true;
    }

    /**
     * 释放锁
     */
    public void unlock(String lockname) {
        super.unlock();
        redisTemplate.opsForValue().set(lockname, "0");
    }
}
