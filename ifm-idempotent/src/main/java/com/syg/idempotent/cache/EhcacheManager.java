package com.syg.idempotent.cache;

import com.alibaba.fastjson.JSON;
import com.syg.idempotent.cache.CacheManager;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Component
@ConditionalOnExpression("#{!environment.containsProperty('spring.redis.host')}")
public class EhcacheManager implements CacheManager {

    @Autowired
    Cache<String, String> mineCache;

    @Override
    public Object get(String key) {

        return mineCache.get(key);

    }

    @Override
    public void set(String key, Object value, Long timeout, TimeUnit timeUnit) {
        mineCache.put(key, JSON.toJSONString(value));

    }

    @Override
    public void delete(String key) {
        mineCache.remove(key);

    }
}
