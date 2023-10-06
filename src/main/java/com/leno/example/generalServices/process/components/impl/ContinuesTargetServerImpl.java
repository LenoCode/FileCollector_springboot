package com.leno.example.generalServices.process.components.impl;

import com.leno.example.config.context.ApplicationContextProvider;
import com.leno.example.domain.dto.execution.ExecutionLocationAndFilters;
import com.leno.example.generalServices.process.components.TargetFile;
import com.leno.example.generalServices.process.components.TargetServer;
import com.leno.example.generalServices.redisService.impl.RedisServiceHSetImpl;
import com.leno.example.generalServices.sftpServices.impl.InternalSftpService;
import com.leno.example.utilz.sftp.exceptions.DownloadFileException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class ContinuesTargetServerImpl extends TargetServer {

    private final InternalSftpService destinationSftp;

    private final RedisServiceHSetImpl redisServiceHSet;

    public ContinuesTargetServerImpl(ExecutionLocationAndFilters executionLocationAndFiltersDto) {
        super(executionLocationAndFiltersDto);
        this.destinationSftp = ApplicationContextProvider.getApplicationContext().getBean(InternalSftpService.class);
        this.redisServiceHSet = ApplicationContextProvider.getApplicationContext().getBean(RedisServiceHSetImpl.class);
    }

    @Override
    public boolean collect(String nameOfCollection) throws Exception, DownloadFileException {
        TargetFile[] targetFiles = filter();

        for(TargetFile targetFile : targetFiles){
            byte[] bytes = downloadFileWithCheckOfFileUploadStatus(targetFile);

            if(!checkIfAlreadyUploaded(nameOfCollection,targetFile)){
                if (!destinationSftp.uploadToTemp(bytes,nameOfCollection,targetFile.getName())){
                    return false;
                }else{
                    addToRedis(nameOfCollection,targetFile);
                }
            }

        }
        return true;
    }

    @Override
    public boolean collect(String nameOfCollection, BiFunction<List<TargetFile>, Boolean, Boolean> onCollectFunction) throws Exception, DownloadFileException {
        TargetFile[] targetFiles = filter();

        List<TargetFile> batch = new ArrayList<>();

        for(TargetFile targetFile : targetFiles){

            if(!checkIfAlreadyUploaded(nameOfCollection,targetFile)){
                byte[] bytes = downloadFileWithCheckOfFileUploadStatus(targetFile);

                if (!destinationSftp.uploadToTemp(bytes,nameOfCollection,targetFile.getName())){
                    return false;
                }else{
                    targetFile.setTargetFtpHost(destinationSftp.getSftpClient().getHost());
                    targetFile.setTargetUsername(destinationSftp.getSftpClient().getUsername());
                    targetFile.setTargetPassword(destinationSftp.getSftpClient().getPassword());
                    targetFile.setTargetPort(destinationSftp.getSftpClient().getPort());
                    targetFile.setTargetFilename(targetFile.getName());
                    targetFile.setTargetFilePath(destinationSftp.getFullPathWithFilename(nameOfCollection, targetFile.getName()));

                    batch.add(targetFile);

                    addToRedis(nameOfCollection,targetFile);
                }
            }
        }
        if(batch.size() != 0){
            onCollectFunction.apply(batch,true);
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
