package com.syg.ifmsession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Configuration
@ConditionalOnExpression("#{environment.containsProperty('auth-session.redis.host')}")
public class SessionConfig {
    @Value("${auth-session.redis.host}")
    String redisHost;
    @Value("${auth-session.redis.port}")
    int redisPort;
    @Value("${auth-session.redis.password}")
    String password;


    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(redisHost);
        redisManager.setPort(redisPort);
        redisManager.setPassword(password);
        return redisManager;
    }

    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }
}
