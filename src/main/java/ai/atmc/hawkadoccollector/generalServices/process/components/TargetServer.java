package ai.atmc.hawkadoccollector.generalServices.process.components;


import ai.atmc.hawkadoccollector.domain.dto.execution.ExecutionDto;
import ai.atmc.hawkadoccollector.domain.dto.execution.ExecutionLocationAndFilters;
import ai.atmc.hawkadoccollector.utilz.redis.RedisClient;
import ai.atmc.hawkadoccollector.utilz.sftp.exceptions.DownloadFileException;
import ai.atmc.hawkadoccollector.utilz.sftp.exceptions.ServerConnectionLostException;
import ai.atmc.hawkadoccollector.utilz.sftp.impl.SftpClient;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.RemoteResourceInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * One sftp location on which we will search for files in specific directories
 *
 * Basically this class have SFTPClient and properties like location and how to filter files
 */
public abstract class TargetServer implements TargetServerInterface {
    private final SftpClient sftpClient;

    protected final int COLLECT_BATCH_SIZE = 200;

    private HashMap<String,Object> filter;

    private TargetDirectory[] directories;


    public TargetServer(ExecutionLocationAndFilters executionLocationAndFiltersDto){

        initDirectories(executionLocationAndFiltersDto.getLocations());

        this.sftpClient = new SftpClient(
                executionLocationAndFiltersDto.getHost(),
                executionLocationAndFiltersDto.getPort(),
                executionLocationAndFiltersDto.getUsername(),
                executionLocationAndFiltersDto.getPassword());

        this.filter = executionLocationAndFiltersDto.getFilters();

    }

    /**
     * Initialization
     *
     *  -initializing sftp client -> if client throws exception, everything should stop working
     */
    public void init() throws ServerConnectionLostException {
        try {
            sftpClient.init();
        } catch (IOException e) {
            throw new ServerConnectionLostException();
        }
    }


    /**
     *  Matching files and collecting those that are matching the filter.
     *
     *
     *   Going through all directories
     *      in directories fetching all files inside
     *          iterate through files
     *              checking if file is regular - meaning is it file and not directory
     *                  checking if file name matches a regex
     *                      adding to targetFileArray
     *
     * @return
     */
    protected TargetFile[] filter(){
        try {
            List<TargetFile> targetFileArray = new ArrayList<>();
            Pattern fileNamePattern = compilePatternFileName();

            for (TargetDirectory directory : directories){
                List<RemoteResourceInfo> files =  sftpClient.listFilesFromDir(directory.getLocation());

                for(RemoteResourceInfo remoteResourceInfo : files){

                    if(remoteResourceInfo.isRegularFile()){
                        String name = remoteResourceInfo.getName();
                        Matcher matcher = fileNamePattern.matcher(name);

                        if(matcher.matches()){
                            targetFileArray.add(new TargetFile(sftpClient.getHost(),
                                    sftpClient.getPort(),
                                    sftpClient.getUsername(),
                                    sftpClient.getPassword(),
                                    remoteResourceInfo.getPath(),
                                    directory,
                                    name,
                                    remoteResourceInfo.getAttributes().getSize()));
                        }

                    }
                }
            }
            return targetFileArray.toArray(new TargetFile[targetFileArray.size()]);
        }catch (Exception e){
            return new TargetFile[0];
        }
    }

    /**
     * Download a target file from sftp server. To bring it to server where collector service
     * is running.
     * @return
     */
    protected byte[] downloadFile(TargetFile targetFile) throws DownloadFileException, ServerConnectionLostException {
        byte[] bytes = sftpClient.get(targetFile.getAbsolutePath());

        if(bytes == null){

            System.out.println("Reconnecting");
            sftpClient.reconnect();
            if(!sftpClient.isConnectionAlive()){
                throw new ServerConnectionLostException();
            }else{
                throw new DownloadFileException();
            }
        }else{
            return bytes;
        }
    }


    /**
     * This function is basically a normal download but with twis that will first check if the size of the file
     * is changing. Meaning that file that this function want to download is might still be uploading.
     * That why we will observe if the size of the file is changing. This
     * function is not bulletproof since is based on timeout.
     *
     * We will constantly check for the file size, if the file size is changing then that means file is still uploading.
     * But what can happen is that file might stop uploading because internet is slow and we can then proceed with download of file
     * even though file is not uploaded completely
     * @param targetFile
     * @return
     */
    protected byte[] downloadFileWithCheckOfFileUploadStatus(TargetFile targetFile) throws ServerConnectionLostException, DownloadFileException {
        try {
            waitForFileToBeUploaded(targetFile.getAbsolutePath());
            return downloadFile(targetFile);
        } catch (ServerConnectionLostException e) {
            throw new RuntimeException(e);
        } catch (DownloadFileException e) {
            //Second time to try download, if it fails, then throw exceptions
            return downloadFile(targetFile);

        }

    }


    /**
     * Get server address
     * @return
     */
    public String getHostAddress(){
        return sftpClient.getHost();
    }



    /**
     * Get directories
     * @return
     */
    public TargetDirectory[] getDirectories() {
        return directories;
    }

    /**
     * @TODO Check if filter has fileName if not it has to throw exception
     * @return Pattern object with regex for matching file name
     */
    private Pattern compilePatternFileName(){
        if(filter.size() == 0) {
            return Pattern.compile(".*");
        }
        return Pattern.compile((String) filter.get("filename"));
    }


    /**
     * Map raw string location into Directory object wrapper
     * @param directories
     */
    private void initDirectories(List<String> directories){
        this.directories = new TargetDirectory[directories.size()];
        for(int i = 0; i < directories.size(); i++){
            this.directories[i] = new TargetDirectory(directories.get(i));
        }
    }

    /**
     * Wait for file to be uploaded
     */
    private boolean waitForFileToBeUploaded(String absPath) throws ServerConnectionLostException {
        long size = 0;
        boolean fileUploadedApprox = false;

        while(!fileUploadedApprox){
            long newSize = sftpClient.getFileAttribute(absPath).getSize();
            if(newSize > 0 && size != newSize){
                size = newSize;
            }else{
                fileUploadedApprox = true;
            }
        }
        return true;
    }

}
