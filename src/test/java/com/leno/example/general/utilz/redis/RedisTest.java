package com.leno.example.general.utilz.redis;


import com.leno.example.utilz.redis.RedisClient;
import ai.atmc.kvstore.utils.KeyValueStoreException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
public class RedisTest {

    @Value(value = "${hawkdoc.redis.host}")
    private String host;

    @Value(value = "${hawkdoc.redis.port}")
    private int port;





    @Test
    public void basicRedisTest() throws KeyValueStoreException {
        RedisClient redisClient = new RedisClient(host,port);
        redisClient.getiKeyValueStore().hset("collectorFiles1","hashfile","1");
        assertEquals("1",redisClient.getiKeyValueStore().hget("collectorFiles1","hashfle"));


    }
}
