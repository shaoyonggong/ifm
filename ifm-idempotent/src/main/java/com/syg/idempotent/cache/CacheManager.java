package com.syg.idempotent.cache;

import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
public interface CacheManager {

    Object get(String key);

    void set(String key, Object value, Long timeout, TimeUnit timeUnit);

    void delete(String key);
}
