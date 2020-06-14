package com.syg.ifmsession;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
public class RedisManager {
    private String host;
    private int port;
    private String password;
    private static JedisPool jedisPool = null;

    public void init() {
        if (jedisPool == null) {
            int timeout = 0;
            if (this.password != null && !"".equals(this.password)) {
                jedisPool = new JedisPool(new JedisPoolConfig(), this.host, this.port, timeout, this.password);
            } else {
                jedisPool = new JedisPool(new JedisPoolConfig(), this.host, this.port);
            }
        }

    }
    void flushTtl(byte[] key,int second){
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.expire(key,second);
        } finally {
            jedisPool.returnResource(jedis);
        }

    }

    public byte[] get(byte[] key) {
        Jedis jedis = jedisPool.getResource();

        byte[] value;
        try {
            value = jedis.get(key);
        } finally {
            jedisPool.returnResource(jedis);
        }

        return value;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
