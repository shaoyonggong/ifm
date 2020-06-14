package com.syg.idempotent.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.klock.lock.Lock;
import org.springframework.boot.autoconfigure.klock.lock.LockFactory;
import org.springframework.boot.autoconfigure.klock.model.LockInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Component
@ConditionalOnExpression("#{environment.containsProperty('spring.redis.host')}")
public class RedisManager implements CacheManager{
    private static Logger logger = LoggerFactory.getLogger(RedisManager.class);

    @Resource(name = "redisTemplate")
    ValueOperations<Object, Object> valOpsObj;
    @Autowired
    RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    LockFactory lockFactory;


    private ThreadLocal<LockRes> currentThreadLockRes = new ThreadLocal<>();
    private ThreadLocal<Lock> currentThreadLock = new ThreadLocal<>();

    @Override
    public Object get(String key) {

        return valOpsObj.get(key);
    }

    @Override
    public void set(String key, Object value, Long timeout, TimeUnit timeUnit) {
        valOpsObj.set(key, value, timeout, timeUnit);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 将幂等值存入redis
     *
     * @param lockInfo 加锁
     * @param md5
     * @param params
     * @param time
     */
    public boolean isRedis(LockInfo lockInfo, String md5, Object params, long time) {
        currentThreadLockRes.set(new LockRes(lockInfo, false));
        Lock lock = lockFactory.getLock(lockInfo);
        boolean lockRes = lock.acquire();

        if (!lockRes) {
            if (logger.isWarnEnabled()) {
                logger.warn("Timeout while acquiring Lock({})", lockInfo.getName());
            }
        }
        currentThreadLock.set(lock);
        currentThreadLockRes.get().setRes(true);
        if (get(md5) == null) {
            set(md5, params, time, TimeUnit.SECONDS);
            afterReturning();
            return false;
        } else {
            afterReturning();
            return true;
        }
    }

    /**
     * 整合释放锁与删除锁
     */
    public void afterReturning() {
        releaseLock();
        cleanUpThreadLocal();
    }

    /**
     * 释放锁
     */
    private void releaseLock() {
        LockRes lockRes = currentThreadLockRes.get();
        if (lockRes.getRes()) {
            boolean releaseRes = currentThreadLock.get().release();
            // avoid release lock twice when exception happens below
            lockRes.setRes(false);
            if (!releaseRes) {

            }
        }
    }

    /**
     * 删除锁
     */
    private void cleanUpThreadLocal() {
        currentThreadLockRes.remove();
        currentThreadLock.remove();
    }

    public class LockRes {
        private LockInfo lockInfo;
        private Boolean res;

        LockRes(LockInfo lockInfo, Boolean res) {
            this.lockInfo = lockInfo;
            this.res = res;
        }

        LockInfo getLockInfo() {
            return lockInfo;
        }

        Boolean getRes() {
            return res;
        }

        void setRes(Boolean res) {
            this.res = res;
        }
    }
}
