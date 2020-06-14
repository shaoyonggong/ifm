package com.syg.idempotent.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.klock.config.KlockConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Configuration
@ConditionalOnExpression("#{environment.containsProperty('spring.redis.host')}")
public class LockBean {

    @Value("${spring.redis.host:127.0.0.1}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private String port;

    @Bean
    KlockConfig klockConfig(){
        KlockConfig klockConfig=new KlockConfig();
        klockConfig.setAddress(host+":"+port);
        return klockConfig;
    }
}
