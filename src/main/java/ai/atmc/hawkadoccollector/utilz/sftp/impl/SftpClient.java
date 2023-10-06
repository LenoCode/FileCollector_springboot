package ai.atmc.hawkadoccollector.utilz.sftp.impl;

import ai.atmc.hawkadoccollector.utilz.sftp.exceptions.ServerConnectionLostException;
import com.jcraft.jsch.*;
import lombok.NoArgsConstructor;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.*;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.schmizz.sshj.xfer.LocalSourceFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * This class represents connection to home server not clients
 * It is also server that will have TEMP DIR - for storing all collected document
 */
@NoArgsConstructor
public class SftpClient extends SftpClientAbstract {
    private final short RECONNECT_TRY = 5;

    private String sftpHost;

    private String user;

    private int port;

    private String password;

    private SSHClient sshClient;

    private ChannelSftp channelSftp;
    private SFTPClient sftpClient;


    public SftpClient(String sftpHost,int port,String user,String password){
        this.sftpHost = sftpHost;
        this.user = user;
        this.password = password;
        this.port = port;
    }

    /**
     * Init sftp object -> main object for communication with server
     * @throws IOException
     */
    @Override
    public boolean init() throws IOException{
        sshClient = setupSshj(sftpHost,port,user,password);
        sshClient.setTimeout(10000);
        sshClient.setConnectTimeout(100000);
        if(sshClient.isConnected()){
            sftpClient = sshClient.newSFTPClient();
            return true;
        }else{
            return false;
        }
    }

    private ChannelSftp setupJsch() throws JSchException {
        JSch jsch = new JSch();
        Session jschSession = jsch.getSession(user, sftpHost);

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");

        jschSession.setConfig(config);
        jschSession.setPassword(password);
        jschSession.connect();
        return (ChannelSftp) jschSession.openChannel("sftp");
    }



    /**
     *
     * @param source
     * @param dest
     * @return
     */
    @Override
    public boolean get(String source, String dest) {
        return false;
    }

    /**
     *
     * @param source
     * @return
     */
    @Override
    public byte[] get(String source) {
        try {
            RemoteFile remoteFile = sftpClient.open(source, EnumSet.of(OpenMode.READ));

            final long size = remoteFile.length();

            if(size == 0)return null;

            byte[] bytes = new byte[(int) size];

            int offSet = 0;
            while(offSet != size){
               int bytesRead = remoteFile.read(offSet,bytes,offSet, ((int) size) - offSet);
               if(bytesRead == -1){
                   break;
               }else{
                   offSet += bytesRead;
               }
            }
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getContent(String source) {
        return null;
    }


    /**
     * Remove directory and all content
     *
     * @param path
     * @return
     */
    @Override
    public boolean removeDir(String path) throws ServerConnectionLostException {
        try {
            if(removeAllFilesFromDir(path)){
                sftpClient.rmdir(path);
            }
            return !checkIfFileExists(path);
        }catch (SFTPException e){
            if(hasTimeoutExceptionOccured((SFTPException) e)){
                throw new ServerConnectionLostException();
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     *
     * @param path
     * @return
     * @throws ServerConnectionLostException
     */
    @Override
    public boolean removeAllFilesFromDir(String path) throws ServerConnectionLostException {
        try {
            List<RemoteResourceInfo> files = listFilesFromDir(path);

            for(RemoteResourceInfo remoteResourceInfo : files){
                if (! removeFile(remoteResourceInfo.getPath())){
                    throw new SFTPException("File not removed");
                };
            }
            return true;
        }catch (SFTPException e){
            if(hasTimeoutExceptionOccured((SFTPException) e)){
                throw new ServerConnectionLostException();
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param path
     * @return
     * @throws ServerConnectionLostException
     */
    @Override
    public boolean removeFile(String path) throws ServerConnectionLostException {
        try {
           sftpClient.rm(path);
           return true;
        }catch (SFTPException e){
            if(hasTimeoutExceptionOccured((SFTPException) e)){
                throw new ServerConnectionLostException();
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     *
     * @param path
     * @return
     */
    @Override
    public boolean mkdir(String path) throws ServerConnectionLostException {
        try {
            sftpClient.mkdir(path);
            return checkIfFileExists(path);
        }catch (SFTPException e){
            if(hasTimeoutExceptionOccured((SFTPException) e)){
                throw new ServerConnectionLostException();
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Uploading file to server.
     *
     * @param fileToUpload
     * @param path - it should only contain till parent text, meaning we dont need to add file name
     * @return
     */
    @Override
    public boolean upload(File fileToUpload, String path) throws ServerConnectionLostException {
        try {
            sftpClient.put(new FileSystemFile(fileToUpload.getAbsolutePath()),path);
            return checkIfFileExists(path);
        }catch (SFTPException e){
            if(hasTimeoutExceptionOccured((SFTPException) e)){
                throw new ServerConnectionLostException();
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Upload file using raw bytes
     *
     *
     * @TODO error handling
     * @param bytes
     * @param path
     * @return
     * @throws ServerConnectionLostException
     */
    @Override
    public boolean upload(byte[] bytes, String path) throws ServerConnectionLostException {
        try {
            RemoteFile remoteFile = sftpClient.open(path,EnumSet.of(OpenMode.CREAT,OpenMode.WRITE));

            remoteFile.write(0,bytes,0,bytes.length);
            remoteFile.close();
            return true;
        }catch (SFTPException e){
            e.printStackTrace();
            if(hasTimeoutExceptionOccured((SFTPException) e)){
                throw new ServerConnectionLostException();
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * Upload using channel sft
     * @param bytes
     * @param path
     * @return
     */
    @Override
    public boolean uploadChannelSftp(byte[] bytes, String path) {
        try {
            ChannelSftp channelJschSftp = setupJsch();
            channelJschSftp.connect();

            InputStream stream = new ByteArrayInputStream(bytes);
            channelJschSftp.put(stream,path);
            return true;
        } catch (JSchException e) {
            return false;
        } catch (SftpException e) {
            return false;
        }
    }

    /**
     * List all files
     * @param directory
     * @return
     */
    @Override
    public List<RemoteResourceInfo> listFilesFromDir(String directory) throws ServerConnectionLostException {
        try {
            return sftpClient.ls(directory);
        }catch (SFTPException e){
            if(hasTimeoutExceptionOccured((SFTPException) e)){
                throw new ServerConnectionLostException();
            }
            return new ArrayList();
        } catch (IOException e) {


            if(e.getMessage().contains("Stream closed")){
                reconnect();
                if(!isConnectionAlive()){
                    throw new ServerConnectionLostException();
                }else{

                    try {
                        return sftpClient.ls(directory);
                    } catch (IOException ex) {
                        throw new ServerConnectionLostException();
                    }
                }
            }
            return new ArrayList();
        }
    }
    /**
     * Checks if file exists on server.
     *
     * @param path
     * @return
     */
    @Override
    public boolean checkIfFileExists(String path) throws ServerConnectionLostException {
        try {
            return sftpClient.statExistence(path) != null;
        }catch (SFTPException e){
            if(hasTimeoutExceptionOccured((SFTPException) e)){
                throw new ServerConnectionLostException();
            }

            reconnect();

            if(!isConnectionAlive()){
                throw new ServerConnectionLostException();
            }else{

                try {
                    return sftpClient.statExistence(path) != null;
                } catch (IOException ex) {
                    throw new ServerConnectionLostException();

                }
            }


        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public FileAttributes getFileAttribute(String path) throws ServerConnectionLostException {
        try {
            return sftpClient.statExistence(path);
        }catch (SFTPException e){
            if(hasTimeoutExceptionOccured((SFTPException) e)){
                throw new ServerConnectionLostException();
            }
            return null;

        } catch (IOException e) {
            return null;
        }
    }


    /**
     * Delete all files inside directory
     * @param path
     */
    public void deleteAllFilesFromDir(String path) throws ServerConnectionLostException {
        try {
            List<RemoteResourceInfo> files = sftpClient.ls(path);

            files.stream().forEach(remoteResourceInfo -> {
                try {
                    if(remoteResourceInfo.isDirectory()){
                        sftpClient.rmdir(remoteResourceInfo.getPath());
                    }
                    else if(remoteResourceInfo.isRegularFile()){
                        sftpClient.rm(remoteResourceInfo.getPath());
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }

            });

        }catch (SFTPException e){
            if(hasTimeoutExceptionOccured((SFTPException) e)){
                throw new ServerConnectionLostException();
            }
        }catch (Exception e){
            return;
        }
    }


    /**
     * If connection breaks, this function will try to reconnect
     */
    public void reconnect() throws ServerConnectionLostException {
        try {
            for(int i = 0; i < RECONNECT_TRY;++i){
                sshClient = setupSshj(sftpHost,22,user,password);
                if(sshClient.isConnected()) {
                    sftpClient = sshClient.newSFTPClient();
                    return;
                }
            }
            throw new ServerConnectionLostException();
        }catch (IOException e){
            throw new ServerConnectionLostException();
        }
    }


    /**
     *
     * @return
     */
    @Override
    public String getHost() {
        return sftpHost;
    }




    /**
     *
     * @return
     */
    @Override
    public String getUsername() {
        return user;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isConnectionAlive() {
        return sshClient.isConnected();
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getPassword() {
        return password;
    }

}
