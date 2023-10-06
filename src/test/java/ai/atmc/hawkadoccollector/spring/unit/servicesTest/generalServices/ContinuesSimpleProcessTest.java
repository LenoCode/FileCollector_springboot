package ai.atmc.hawkadoccollector.spring.unit.servicesTest.generalServices;


import ai.atmc.hawkadoccollector.TestTools;
import ai.atmc.hawkadoccollector.config.context.ApplicationContextProvider;
import ai.atmc.hawkadoccollector.config.thread.ThreadExecutorConfiguration;
import ai.atmc.hawkadoccollector.domain.dto.execution.ExecutionDto;
import ai.atmc.hawkadoccollector.domain.dto.execution.ExecutionLocationAndFilters;
import ai.atmc.hawkadoccollector.domain.enums.CollectorTypeEnum;
import ai.atmc.hawkadoccollector.generalServices.process.components.TargetDirectory;
import ai.atmc.hawkadoccollector.generalServices.process.impl.ContinuesSftpProcess;
import ai.atmc.hawkadoccollector.generalServices.process.impl.SimpleSftpProcess;
import ai.atmc.hawkadoccollector.generalServices.redisService.impl.RedisServiceHSetImpl;
import ai.atmc.hawkadoccollector.generalServices.sftpServices.SftpService;
import ai.atmc.hawkadoccollector.generalServices.sftpServices.impl.InternalSftpService;
import ai.atmc.hawkadoccollector.modules.collector.services.collectorService.CollectorServiceInterface;
import ai.atmc.hawkadoccollector.modules.collector.services.collectorService.impl.CollectorServiceImpl;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
@SpringBootTest(classes = {ThreadExecutorConfiguration.class,InternalSftpService.class, SftpService.class, ApplicationContextProvider.class, ContinuesSftpProcess.class, SimpleSftpProcess.class, RedisServiceHSetImpl.class, CollectorServiceInterface.class,CollectorServiceImpl.class})
public class ContinuesSimpleProcessTest extends TestTools {

    @Value(value = "${hawkdoc.sftp.internal.host}")
    private String sftpHost;

    @Value(value = "${hawkdoc.sftp.internal.user}")
    private String user;

    @Value(value = "${hawkdoc.sftp.internal.password}")
    private String password;

    @Autowired
    private InternalSftpService internalSftpService;


    @Autowired
    private RedisServiceHSetImpl redisServiceHSet;

    @Autowired
    private CollectorServiceInterface collectorService;

    private ExecutionDto executionDto;

    private ExecutionLocationAndFilters executionLocationAndFilters;



    private final long executionId = 0;


    private final String TARGET_DIRECTORY = "/data/test/targetServerTest/continuesProcessTestFiles";
    private final String COLLECTION_NAME = "execution_Test_0";

    @BeforeEach
    public void setup() throws ServerConnectionLostException {
        HashMap<String,Object> filters = new HashMap<>();
        filters.put("filename","^[\\w\\-. ]+$");
        filters.put("createdBy","Filip");
        filters.put("createdTime",30000);
        executionLocationAndFilters = new ExecutionLocationAndFilters(sftpHost,22,user,password, List.of(new String[]{TARGET_DIRECTORY}),filters);

        executionDto = new ExecutionDto(executionId,36, -1,new HashMap<>(),"trigger",CollectorTypeEnum.COLLECTOR_INTERNAL_SERVER.getType(),false);

        internalSftpService.getSftpClient().mkdir(TARGET_DIRECTORY);

    }


    /**
     * Simple continues process.
     * Check if setup process is ok
     * Check if run process is ok
     * and if finished result is ok
     *
     * Temp dir in which files will be collect is named execution_120
     */
    @Test
    @DisplayName("Start basic process and check if all went well")
    public void startBasicProcessAndCheckIfAllWentWell() throws ServerConnectionLostException {
        ContinuesSftpProcess continuesSftpProcess = new ContinuesSftpProcess(executionDto, new ExecutionLocationAndFilters[]{executionLocationAndFilters});

        continuesSftpProcess.setup();
        TargetDirectory directory = continuesSftpProcess.getTargetServerList().get(0).getDirectories()[0];
        assertEquals(directory.getLocation(),TARGET_DIRECTORY);

        continuesSftpProcess.run();

        assertTrue(internalSftpService.fileExistInTempCollection(
                "execution_".concat(String.valueOf(executionId)),
                "code.py" )
        );
    }


    /**
     * Simple test that will run async continues process handler.
     * Then it will upload file periodically and test if file gets uploaded.
     *
     *
     * Also test will check if the file size is correct
     */
    @Test
    @DisplayName("Checking if continues process is collecting files uploaded periodically")
    public void continuesProcessFileCollectingTest() throws InterruptedException, ServerConnectionLostException, IOException {
        File content = new File(RESOURCE_DIR,"testFiles/filesForContinuesTesting");

        //Run async collector handler
        Future<Boolean> future = collectorService.runContinuesHandlerProcess();
        Thread.sleep(100);
        assertTrue(collectorService.isContinuesProcessHandlerAlive());
        assertTrue(!future.isDone());

        //Adding continues process
        ContinuesSftpProcess continuesSftpProcess = new ContinuesSftpProcess(executionDto, new ExecutionLocationAndFilters[]{executionLocationAndFilters},COLLECTION_NAME);
        continuesSftpProcess.setup();
        //Checking if collection temp dir was made on sftp server in folder temp
        assertTrue(internalSftpService.isTempCollectionDirExist(COLLECTION_NAME));

        //Adding to runner
        collectorService.manuallyAddProcess(executionId,continuesSftpProcess);
        Thread.sleep(10);
        assertEquals(1, collectorService.numberOfActiveContinuesProcess());



        for(File fileToUpload : content.listFiles()){

            FileInputStream stream = new FileInputStream(fileToUpload);
            byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            stream.close();

            String absPath = TARGET_DIRECTORY.concat(File.separator).concat(fileToUpload.getName());
            internalSftpService.getSftpClient().uploadChannelSftp(bytes,absPath);

            assertTrue(internalSftpService.getSftpClient().checkIfFileExists(absPath));

            int tryCount = 0;

            while(tryCount != 5 ){
                if(internalSftpService.fileExistInTempCollection(COLLECTION_NAME,fileToUpload.getName())){
                    break;
                }
                ++tryCount;
                Thread.sleep(2000);
            }
            assertTrue(tryCount < 5);
        }


        //Check if the file uploaded to temp/collector are the valid size, meaning that the function did properly wait for file to be downloaded
        for(File fileToUpload : content.listFiles()){
            try {
                long sizeOrg = fileToUpload.length();
                long uploadedSize = internalSftpService.getUploadedFileSize(COLLECTION_NAME, fileToUpload.getName());
                assertEquals(sizeOrg,uploadedSize);
            }catch (ServerConnectionLostException e){
                assertTrue(fail());
            }
        }
    }

    @AfterEach
    public void clean() throws ServerConnectionLostException {
        internalSftpService.removeCollectionDir(COLLECTION_NAME);
        redisServiceHSet.removeHSet(COLLECTION_NAME);
        internalSftpService.getSftpClient().removeAllFilesFromDir(TARGET_DIRECTORY);
        internalSftpService.getSftpClient().removeDir(TARGET_DIRECTORY);
    }
}
