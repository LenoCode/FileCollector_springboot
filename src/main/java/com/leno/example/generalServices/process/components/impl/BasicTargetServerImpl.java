package com.leno.example.generalServices.process.components.impl;

import com.leno.example.config.context.ApplicationContextProvider;
import com.leno.example.domain.dto.execution.ExecutionLocationAndFilters;
import com.leno.example.generalServices.process.components.TargetFile;
import com.leno.example.generalServices.process.components.TargetServer;
import com.leno.example.generalServices.redisService.impl.RedisServiceHSetImpl;
import com.leno.example.generalServices.sftpServices.impl.InternalSftpService;
import com.leno.example.utilz.sftp.exceptions.DownloadFileException;

import java.util.*;
import java.util.function.BiFunction;

public class BasicTargetServerImpl extends TargetServer {

    private final InternalSftpService destinationSftp;


    private final RedisServiceHSetImpl redisServiceHSet;



    /**
     * @param executionLocationAndFiltersDto
     */
    public BasicTargetServerImpl( ExecutionLocationAndFilters executionLocationAndFiltersDto) {
        super(executionLocationAndFiltersDto);
        this.destinationSftp = ApplicationContextProvider.getApplicationContext().getBean(InternalSftpService.class);
        this.redisServiceHSet = ApplicationContextProvider.getApplicationContext().getBean(RedisServiceHSetImpl.class);
    }

    /**
     *
     * @param nameOfTheCollection
     * @return
     */
    @Override
    public boolean collect(String nameOfTheCollection) throws Exception, DownloadFileException {
        TargetFile[] targetFiles = filter();

        for(TargetFile targetFile : targetFiles){
            byte[] bytes = downloadFile(targetFile);

            if (!destinationSftp.uploadToTemp(bytes,nameOfTheCollection,targetFile.getName())){
                return false;
            }
        }
        return true;
    }

    /**
     * After each of the collection call event function
     * @param nameOfTheCollection
     * @param onCollectFunction
     * @return
     * @throws Exception
     */
    @Override
    public boolean collect(String nameOfTheCollection, BiFunction<List<TargetFile>,Boolean, Boolean> onCollectFunction) throws Exception,DownloadFileException {

        TargetFile[] targetFiles = filter();

        if(targetFiles.length <= COLLECT_BATCH_SIZE){

            List<TargetFile> batch = new ArrayList<>();

            for(TargetFile targetFile : targetFiles){

                if(!checkIfAlreadyUploaded(nameOfTheCollection,targetFile)) {
                    byte[] bytes = downloadFileWithCheckOfFileUploadStatus(targetFile);

                    if (!destinationSftp.uploadToTemp(bytes, nameOfTheCollection, targetFile.getName())) {
                        return false;
                    } else {
                        targetFile.setTargetFtpHost(destinationSftp.getSftpClient().getHost());
                        targetFile.setTargetUsername(destinationSftp.getSftpClient().getUsername());
                        targetFile.setTargetPassword(destinationSftp.getSftpClient().getPassword());
                        targetFile.setTargetPort(destinationSftp.getSftpClient().getPort());
                        targetFile.setTargetFilename(targetFile.getName());
                        targetFile.setTargetFilePath(destinationSftp.getFullPathWithFilename(nameOfTheCollection, targetFile.getName()));

                        batch.add(targetFile);

                        addToRedis(nameOfTheCollection,targetFile);

                    }
                }

            }
            onCollectFunction.apply(batch,true);
        }else{

            int filesCollected = 0;

            while(filesCollected != targetFiles.length){

                List<TargetFile> batch = new ArrayList<>();

                for(int i = 0; i < COLLECT_BATCH_SIZE; ++i){

                    for(TargetFile targetFile : targetFiles){
                        byte[] bytes = downloadFile(targetFile);

                        if (!destinationSftp.uploadToTemp(bytes,nameOfTheCollection,targetFile.getName())){
                            return false;
                        }else{
                            targetFile.setTargetFtpHost(destinationSftp.getSftpClient().getHost());
                            targetFile.setTargetUsername(destinationSftp.getSftpClient().getUsername());
                            targetFile.setTargetPassword(destinationSftp.getSftpClient().getPassword());
                            targetFile.setTargetPort(destinationSftp.getSftpClient().getPort());
                            targetFile.setTargetFilename(targetFile.getName());
                            targetFile.setTargetFilePath(destinationSftp.getFullPathWithFilename(nameOfTheCollection, targetFile.getName()));
                            batch.add(targetFile);
                            ++filesCollected;
                        }
                    }
                }
                onCollectFunction.apply(batch,false);
            }
            onCollectFunction.apply(null,true);
    }
        return true;
    }

    /**
     * Adds file collected to redis so that we can track what files
     * have already been uploaded
     * @param executionId
     * @param targetFile
     */
    private void addToRedis(String executionId,TargetFile targetFile){
        redisServiceHSet.addToSet(executionId,targetFile.getAbsolutePath(),targetFile.getName());
    }

    /**
     *
     * @param executionId
     * @param targetFile
     * @return
     */
    private boolean checkIfAlreadyUploaded(String executionId,TargetFile targetFile){
        return redisServiceHSet.isExist(executionId,targetFile.getAbsolutePath());
    }

}
