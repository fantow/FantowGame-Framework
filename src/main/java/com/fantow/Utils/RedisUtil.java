package com.fantow.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisUtil {

    private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    private static JedisPool jedisPool = null;

    public RedisUtil() {
    }

    public static void init(){
        try {
            jedisPool = new JedisPool("127.0.0.1", 6379);
            logger.info("Redis连接成功...");
        }catch (Exception ex){
            logger.info("Redis连接异常...");
            logger.error(ex.getMessage(),ex);
        }
    }

    public static Jedis getJedis(){
        if(jedisPool == null){
            logger.info("JedisPool 初始化失败...");
        }

        Jedis jedis = jedisPool.getResource();

        return jedis;
    }

}
