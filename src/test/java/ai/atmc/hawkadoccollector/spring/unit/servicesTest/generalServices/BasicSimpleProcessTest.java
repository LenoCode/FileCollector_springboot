package ai.atmc.hawkadoccollector.spring.unit.servicesTest.generalServices;


import ai.atmc.hawkadoccollector.config.context.ApplicationContextProvider;
import ai.atmc.hawkadoccollector.config.thread.ThreadExecutorConfiguration;
import ai.atmc.hawkadoccollector.domain.dto.execution.ExecutionDto;
import ai.atmc.hawkadoccollector.domain.dto.execution.ExecutionLocationAndFilters;
import ai.atmc.hawkadoccollector.domain.enums.CollectorTypeEnum;
import ai.atmc.hawkadoccollector.generalServices.process.components.TargetDirectory;
import ai.atmc.hawkadoccollector.generalServices.process.impl.SimpleSftpProcess;
import ai.atmc.hawkadoccollector.generalServices.redisService.impl.RedisServiceHSetImpl;
import ai.atmc.hawkadoccollector.generalServices.sftpServices.SftpService;
import ai.atmc.hawkadoccollector.generalServices.sftpServices.impl.InternalSftpService;
import ai.atmc.hawkadoccollector.utilz.sftp.exceptions.ServerConnectionLostException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;


/**
 * For this test to run on internal sftp server there should be
 *
 * Directories
 *          /data/test/targetServerTest/filesToCollect
 *                                                    and inside there should be a file code.py
 */
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
@SpringBootTest(classes = {ThreadExecutorConfiguration.class,InternalSftpService.class, SftpService.class, ApplicationContextProvider.class, SimpleSftpProcess.class})
public class BasicSimpleProcessTest {

    @Value(value = "${hawkdoc.sftp.internal.host}")
    private String sftpHost;

    @Value(value = "${hawkdoc.sftp.internal.user}")
    private String user;

    @Value(value = "${hawkdoc.sftp.internal.password}")
    private String password;

    @Autowired
    private InternalSftpService internalSftpService;

    private ExecutionDto executionDto;

    private ExecutionLocationAndFilters executionLocationAndFilters;

    private final long executionId = 120;


    private final String TARGET_DIRECTORY = "/data/test/targetServerTest/filesToCollect";

    @BeforeEach
    public void setup(){
        HashMap<String,Object> filters = new HashMap<>();
        filters.put("filename","^[\\w\\-. ]+$");
        filters.put("createdBy","Filip");
        filters.put("createdTime",30000);
        executionLocationAndFilters = new ExecutionLocationAndFilters(sftpHost,22,user,password, List.of(new String[]{TARGET_DIRECTORY}),filters);

        executionDto = new ExecutionDto(executionId,36, -1,new HashMap<>(),"trigger",CollectorTypeEnum.COLLECTOR_INTERNAL_SERVER.getType(),false);
    }


    /**
     * Simple test process.
     * Check if setup process is ok
     * Check if run process is ok
     * and if finished result is ok
     *
     * Temp dir in which files will be collect is named execution_120
     */
    @Test
    @DisplayName("Start basic process and check if all went well")
    public void startBasicProcessAndCheckIfAllWentWell() throws ServerConnectionLostException {
        SimpleSftpProcess simpleProcess = new SimpleSftpProcess(executionDto, new ExecutionLocationAndFilters[]{executionLocationAndFilters});
        simpleProcess.setup();
        TargetDirectory directory = simpleProcess.getTargetServerList().get(0).getDirectories()[0];
        assertEquals(directory.getLocation(),TARGET_DIRECTORY);

        simpleProcess.run();

        assertTrue(internalSftpService.fileExistInTempCollection(
                "execution_".concat(String.valueOf(executionId)),
                "code.py" )
        );

    }


    @AfterEach
    public void clean(){
        internalSftpService.removeCollectionDir("execution_".concat(String.valueOf(executionId)));
    }
}
