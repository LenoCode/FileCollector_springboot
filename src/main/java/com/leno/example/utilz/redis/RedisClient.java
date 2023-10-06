package com.leno.example.utilz.redis;

import ai.atmc.kvstore.impl.RedisKeyValueStore;
import ai.atmc.kvstore.interfaces.IKeyValueStore;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


/**
 *
 */
public class RedisClient {

    private final IKeyValueStore iKeyValueStore;


    public RedisClient(){
        this.iKeyValueStore = null;
    }

    public RedisClient(String host,int port){
        JedisPool jedisPool = new JedisPool(buildPoolConfig(),host,port);
        iKeyValueStore = new RedisKeyValueStore(jedisPool);
    }


    private static JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        //TODO check which config will suit us best
        /**poolConfig.setMaxTotal(128);
         poolConfig.setMaxIdle(128);
         poolConfig.setMinIdle(16);
         poolConfig.setTestOnBorrow(true);
         poolConfig.setTestOnReturn(true);
         poolConfig.setTestWhileIdle(true);
         poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
         poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
         poolConfig.setNumTestsPerEvictionRun(3);
         poolConfig.setBlockWhenExhausted(true);**/
        return poolConfig;
    }

    public IKeyValueStore getiKeyValueStore() {
        return iKeyValueStore;
    }
}
