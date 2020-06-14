package com.syg.idempotent.config;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Configuration
@EnableCaching
@ConditionalOnExpression("#{!environment.containsProperty('spring.redis.host')}")
public class CacheConfig {
    private static Logger logger = LoggerFactory.getLogger(CacheConfig.class);



    @Bean
    Cache<String, String> getMineCache() {
        return getCacheManager().getCache("defaultCache", String.class, String.class);
    }

    /**
     * 条件判断 如果不包含key spring.redis.host 将redis链接返回null，避免启动时连接redis抛异常
     */
    @Bean
    RedissonClient getRedissonClient() {//如果不包含redis配置文件，直接返回null避免启动后lian接redis导致异常
        return null;
    }

    @Bean(name = "redisHealthIndicator")
    String getRedisHealthIndicator() {//如果不包含redis配置文件，手动注入这个bean避免启动后redis健康检查失败导致consul健康检查异常
        return null;
    }

    /**
     * 初始化Ehcache缓存对象
     */
    CacheManager getCacheManager() {
        logger.info("[Ehcache配置初始化<开始>]");
        // 配置默认缓存属性
        CacheConfiguration<String, String> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(
                // 缓存数据K和V的数值类型
                // 在ehcache3.3中必须指定缓存键值类型,如果使用中类型与配置的不同,会报类转换异常
                String.class, String.class,
                ResourcePoolsBuilder
                        .newResourcePoolsBuilder()
                        //设置缓存堆容纳元素个数(JVM内存空间)超出个数后会存到offheap中
                        .heap(1000L, EntryUnit.ENTRIES)
                        //设置堆外储存大小(内存存储) 超出offheap的大小会淘汰规则被淘汰
                        .offheap(100L, MemoryUnit.MB)
                        // 配置磁盘持久化储存(硬盘存储)用来持久化到磁盘,这里设置为false不启用
                        .disk(500L, MemoryUnit.MB, true)
        ).withExpiry(Expirations.timeToLiveExpiration(
                //设置缓存过期时间
                Duration.of(30L, TimeUnit.MINUTES))
        ).withExpiry(Expirations.timeToIdleExpiration(
                //设置被访问后过期时间(同时设置和TTL和TTI之后会被覆盖,这里TTI生效,之前版本xml配置后是两个配置了都会生效)
                Duration.of(60L, TimeUnit.MINUTES))
        )
                // 缓存淘汰策略 默认策略是LRU（最近最少使用）。可以设置为FIFO（先进先出）或是LFU（较少使用）。
                /*.withEvictionAdvisor(
                        new OddKeysEvictionAdvisor<Long, String>())*/
                .build();
        // CacheManager管理缓存
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                // 硬盘持久化地址
                .with(CacheManagerBuilder.persistence("java.io.tmpdir"))
                // 设置一个默认缓存配置
                .withCache("defaultCache", cacheConfiguration)
                //创建之后立即初始化
                .build(true);

        logger.info("[Ehcache配置初始化<完成>]");
        return cacheManager;
    }
}
