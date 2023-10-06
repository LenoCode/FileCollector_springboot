package ai.atmc.hawkadoccollector.general.utilz.targetServer;


import ai.atmc.hawkadoccollector.config.context.ApplicationContextProvider;
import ai.atmc.hawkadoccollector.domain.dto.execution.ExecutionLocationAndFilters;
import ai.atmc.hawkadoccollector.generalServices.process.components.impl.BasicTargetServerImpl;
import ai.atmc.hawkadoccollector.generalServices.redisService.impl.RedisServiceHSetImpl;
import ai.atmc.hawkadoccollector.generalServices.sftpServices.SftpService;
import ai.atmc.hawkadoccollector.generalServices.sftpServices.impl.InternalSftpService;
import ai.atmc.hawkadoccollector.utilz.sftp.exceptions.ServerConnectionLostException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * For this test to run on internal sftp server there should be
 *
 * Directories
 *          /data/test/targetServerTest/filesToCollect
 *                                                    and inside there should be a file code.py
 */
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
@SpringBootTest(classes = {InternalSftpService.class, SftpService.class, ApplicationContextProvider.class, RedisServiceHSetImpl.class})
public class TargetServerTest {

    @Value(value = "${hawkdoc.sftp.internal.host}")
    private String sftpHost;

    @Value(value = "${hawkdoc.sftp.internal.user}")
    private String user;

    @Value(value = "${hawkdoc.sftp.internal.password}")
    private String password;

    @Autowired
    private InternalSftpService internalSftpService;


    private ExecutionLocationAndFilters executionLocationAndFiltersDto;
    private final String COLLECTION_NAME = "testTargetCollectionTemp";

    @BeforeEach
    public void setup(){
        HashMap<String,Object> filters = new HashMap<>();
        filters.put("filename","^[\\w\\-. ]+$");
        filters.put("createdBy","Filip");
        filters.put("createdTime",30000);
        executionLocationAndFiltersDto = new ExecutionLocationAndFilters(sftpHost,22,user,password, List.of(new String[]{"/data/test/targetServerTest/filesToCollect"}),filters);
        internalSftpService.makeCollectionTempDir(COLLECTION_NAME);
    }


    /**
     * Basic initialization
     */
    @Test
    public void initializeTargetTestServer(){
        BasicTargetServerImpl basicTargetServer = new BasicTargetServerImpl(executionLocationAndFiltersDto);
        assertEquals(basicTargetServer.getHostAddress(),sftpHost);
    }

    /**
     *
     * @throws ServerConnectionLostException
     */
    @Test
    public void checkIfDataIsCollectedProperly() throws Exception {
        BasicTargetServerImpl basicTargetServer = new BasicTargetServerImpl(executionLocationAndFiltersDto);
        basicTargetServer.init();

        basicTargetServer.collect(COLLECTION_NAME);

        assertTrue(internalSftpService.fileExistInTempCollection(COLLECTION_NAME,"code.py"));
    }


    @AfterEach
    public void clean(){
        internalSftpService.removeCollectionDir(COLLECTION_NAME);
    }
}
