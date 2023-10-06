package com.leno.example.spring.unit.servicesTest.generalServices;


import com.leno.example.config.context.ApplicationContextProvider;
import com.leno.example.generalServices.redisService.RedisService;
import com.leno.example.generalServices.redisService.impl.RedisServiceHSetImpl;
import com.leno.example.utilz.redis.RedisClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
@SpringBootTest(classes = {RedisServiceHSetImpl.class, RedisService.class, ApplicationContextProvider.class, RedisClient.class})
public class RedisServiceTest {



    @Autowired
    RedisServiceHSetImpl redisServiceHSet;



    @Test
    @DisplayName("Basic test ")
    public void basicTest(){
        redisServiceHSet.addToSet("collectorFiles1","file","2");
    }
}
