package com.leno.example.spring.unit.servicesTest.generalServices;


import com.leno.example.generalServices.sftpServices.SftpService;
import com.leno.example.generalServices.sftpServices.impl.InternalSftpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
@SpringBootTest(classes = {InternalSftpService.class, SftpService.class})
public class InternalSftpServiceTest {


    @Autowired
    private InternalSftpService internalSftpService;




    @BeforeEach
    public void setup(){

    }

    /**
     *
     */
    @Test
    @DisplayName("Check if collection dir is made and removed properly")
    public void checkIfCollectionDirIsMadeAndRemovedProperly(){
        String temp = "internalSftpServiceTestCollectionTemp";
        boolean status = internalSftpService.makeCollectionTempDir(temp);
        assertTrue(status);
        assertTrue(internalSftpService.isTempCollectionDirExist(temp));

        status = internalSftpService.removeCollectionDir(temp);
        assertTrue(status);
        assertFalse(internalSftpService.isTempCollectionDirExist(temp));
    }

}
