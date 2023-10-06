package com.leno.example.spring.unit.servicesTest.moduleServices.collector;


import com.leno.example.domain.dao.documentCollector.DocumentDao;
import com.leno.example.domain.dto.execution.ExecutionDto;
import com.leno.example.domain.dto.execution.ExecutionLocationAndFilters;
import com.leno.example.domain.enums.CollectorTypeEnum;
import com.leno.example.domain.repository.documentCollector.DocumentRepositoryBase;
import com.leno.example.generalServices.sftpServices.impl.InternalSftpService;
import com.leno.example.modules.collector.services.collectorService.CollectorServiceInterface;
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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


/**
 *
 */
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
@SpringBootTest()
public class CollectorServiceTests {

    @Value(value = "${hawkdoc.sftp.internal.host}")
    private String sftpHost;

    @Value(value = "${hawkdoc.sftp.internal.port}")
    private String sftpPort;

    @Value(value = "${hawkdoc.sftp.internal.user}")
    private String user;

    @Value(value = "${hawkdoc.sftp.internal.password}")
    private String password;

    @Autowired
    private InternalSftpService internalSftpService;


    @Autowired
    private CollectorServiceInterface collectorService;

    @Autowired
    DocumentRepositoryBase hawkadocDocumentRepositoryBase;

    private ExecutionDto executionDto;

    private final int executionId = 150;


    private final String TARGET_DIRECTORY = "/data/test/targetServerTest/filesToCollect";

    @BeforeEach
    public void setup(){
        HashMap<String,Object> filters = new HashMap<>();
        filters.put("filename","^[\\w\\-. ]+$");
        filters.put("createdBy","Filip");
        filters.put("createdTime",30000);
        ExecutionLocationAndFilters executionLocationAndFiltersDto = new ExecutionLocationAndFilters(sftpHost,22,user,password, List.of(new String[]{TARGET_DIRECTORY}),filters);

        HashMap<String,Object> internalServerProperties = new HashMap<>();
        internalServerProperties.put("host",sftpHost);
        internalServerProperties.put("port",sftpPort);
        internalServerProperties.put("username",user);
        internalServerProperties.put("password",password);
        internalServerProperties.put("locations",new String[]{TARGET_DIRECTORY});
        internalServerProperties.put("filters",filters);

        executionDto = new ExecutionDto(executionId,36, -1,internalServerProperties,"trigger", CollectorTypeEnum.COLLECTOR_INTERNAL_SERVER.getType(),false);
    }



    @Test
    @DisplayName("Testing basic async function in collector service - SUPPOSE TO FAIL")
    public void testBasicAsyncFunctionInCollector() throws Exception {
        CompletableFuture<Boolean> future =  collectorService.runProcess(executionDto);
        assertFalse(future.isDone());


        while(!future.isDone()){}

        assertTrue(internalSftpService.fileExistInTempCollection(
                "execution_".concat(String.valueOf(executionId)),
                "code.py" )
        );
        Optional<DocumentDao> hawkadocDocumentDao = hawkadocDocumentRepositoryBase.findById(executionId);

        assertTrue(hawkadocDocumentDao.isPresent());

    }


    @AfterEach
    public void clean(){
        internalSftpService.removeCollectionDir("execution_".concat(String.valueOf(executionId)));
    }
}
