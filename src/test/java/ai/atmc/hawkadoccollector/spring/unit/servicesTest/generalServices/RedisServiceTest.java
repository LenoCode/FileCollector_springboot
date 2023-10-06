package ai.atmc.hawkadoccollector.spring.unit.servicesTest.generalServices;


import ai.atmc.hawkadoccollector.config.context.ApplicationContextProvider;
import ai.atmc.hawkadoccollector.generalServices.process.impl.SimpleSftpProcess;
import ai.atmc.hawkadoccollector.generalServices.redisService.RedisService;
import ai.atmc.hawkadoccollector.generalServices.redisService.impl.RedisServiceHSetImpl;
import ai.atmc.hawkadoccollector.generalServices.sftpServices.SftpService;
import ai.atmc.hawkadoccollector.generalServices.sftpServices.impl.InternalSftpService;
import ai.atmc.hawkadoccollector.utilz.redis.RedisClient;
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
