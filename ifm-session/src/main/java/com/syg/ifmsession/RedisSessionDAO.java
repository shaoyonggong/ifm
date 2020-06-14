package com.syg.ifmsession;

import com.syg.ifmsession.utils.SerializeUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
public class RedisSessionDAO {
    private static Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);
    private RedisManager redisManager;
    private String keyPrefix = "shiro_redis_session:";


    public Session readSession(Serializable sessionId) {
        if (sessionId == null) {
            logger.error("session id is null");
            return null;
        } else {
            return (Session) SerializeUtils.deserialize(this.redisManager.get(this.getByteKey(sessionId)));
        }
    }

    public void flushTtl(String sessionId){
        this.redisManager.flushTtl(this.getByteKey(sessionId),14400);
    }

    private byte[] getByteKey(Serializable sessionId) {
        String preKey = this.keyPrefix + sessionId;
        return preKey.getBytes();
    }

    public RedisManager getRedisManager() {
        return this.redisManager;
    }

    public void setRedisManager(RedisManager redisManager) {
        this.redisManager = redisManager;
        this.redisManager.init();

    }
}
