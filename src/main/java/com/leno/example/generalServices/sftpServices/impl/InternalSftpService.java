package com.leno.example.generalServices.sftpServices.impl;


import com.leno.example.generalServices.sftpServices.SftpService;
import com.leno.example.utilz.sftp.exceptions.ServerConnectionLostException;
import com.leno.example.utilz.sftp.impl.SftpClient;
import jakarta.annotation.PostConstruct;
import net.schmizz.sshj.sftp.FileAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class InternalSftpService implements SftpService {


    @Value(value = "${hawkdoc.sftp.internal.host}")
    private String sftpHost;

    @Value(value = "${hawkdoc.sftp.internal.port}")
    private int sftpPort;


    @Value(value = "${hawkdoc.sftp.internal.user}")
    private String user;

    @Value(value = "${hawkdoc.sftp.internal.password}")
    private String password;

    @Value(value = "${hawkdoc.sftp.internal.execution.tempPath}")
    private String tempPath;

    private SftpClient sftpClient;


    public InternalSftpService(){

    }

    public InternalSftpService(String sftpHost,String password,String user,String port,String tempPath){
        this.sftpHost = sftpHost;
        this.password = password;
        this.user = user;
        this.sftpPort = Integer.parseInt(port);
        this.tempPath = tempPath;
    }


    /**
     * Internal Service
     */
    @PostConstruct
    public void init(){
        this.sftpClient = new SftpClient(sftpHost,sftpPort,user,password);
        try {
            this.sftpClient.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Temp path is the location of folder that will have all the execution folders
     *
     *  Temp
     *      executionId_1
     *          document_1.txt
     *          document_2.txt
     * @param bytes
     * @param fileName
     * @return
     */
    @Override
    public boolean uploadToTemp(byte[] bytes, String collectionName,String fileName) {
        try {
            String fullPath = tempPath+ File.separator+collectionName+File.separator+fileName;
            if(!sftpClient.uploadChannelSftp(bytes,fullPath)){
                throw new ServerConnectionLostException();
            };

            return true;
        } catch (ServerConnectionLostException e) {
            return false;
        }
    }

    /**
     *
     * @TODO -> check should there be a code to check for the existence of collection, meaning if collection with name already exist should throw error
     * @param name
     * @return
     */
    @Override
    public boolean makeCollectionTempDir(String name) {
        try {
            sftpClient.mkdir(tempPath+File.separator+name);
        } catch (ServerConnectionLostException e) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param nameOfDir
     * @return
     */
    @Override
    public boolean removeCollectionDir(String nameOfDir) {
        try {
            sftpClient.removeDir(tempPath+File.separator+nameOfDir);
        } catch (ServerConnectionLostException e) {
            return false;
        }
        return true;
    }

    /**
     * Check for file existience
     * @param parentDir
     * @param file
     * @return
     */
    @Override
    public boolean fileExistInTempCollection(String parentDir, String file) throws ServerConnectionLostException {
        return sftpClient.checkIfFileExists(tempPath+File.separator+parentDir+File.separator+file);
    }

    @Override
    public long getUploadedFileSize(String parentDir, String file) throws ServerConnectionLostException {
        FileAttributes fileAttributes = sftpClient.getFileAttribute(getFullPathWithFilename(parentDir,file));

        if(fileAttributes != null){
            return fileAttributes.getSize();
        }
        throw new ServerConnectionLostException();
    }

    @Override
    public String getFullPathWithFilename(String collectionName,String fileName) {
        return tempPath+ File.separator+collectionName+File.separator+fileName;
    }


    /**
     *
     * @return
     */
    public boolean isTempCollectionDirExist(String nameOfDir) {
        try {
            return sftpClient.checkIfFileExists(tempPath+File.separator+nameOfDir);
        } catch (ServerConnectionLostException e) {
            return false;
        }
    }


    public SftpClient getSftpClient() {
        return sftpClient;
    }




}
