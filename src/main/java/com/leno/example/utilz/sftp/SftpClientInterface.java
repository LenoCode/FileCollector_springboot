package com.leno.example.utilz.sftp;


import com.leno.example.utilz.sftp.exceptions.ServerConnectionLostException;
import net.schmizz.sshj.sftp.FileAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface SftpClientInterface {


    /**
     * Necessary steps for initializing client.
     * Like making connection to server
     * @return
     */
    boolean init() throws IOException;

    /**
     * Get file from Sftp service to broker machine
     * @param source
     * @param dest
     * @return
     */
    boolean get(String source,String dest) throws ServerConnectionLostException;

    /**
     * Get file from Sftp service and return bytes.
     * Usage would be if broker just wants byte of file so it can give it
     * to someone else.
     * This way it will save file in temp folder
     * @param source
     * @return
     */
    byte[] get(String source) throws ServerConnectionLostException;





    /**
     * Read content of file and return string
     * @param source
     * @return
     */
    String getContent(String source) throws ServerConnectionLostException;


    /**
     * Remove directory from server
     */
    boolean removeDir(String path) throws ServerConnectionLostException;

    /**
     * Remove all files from directory
     * @param path
     * @return
     * @throws ServerConnectionLostException
     */
    boolean removeAllFilesFromDir(String path) throws ServerConnectionLostException;

    /**
     * Remove specifc file
     * @param path
     * @return
     * @throws ServerConnectionLostException
     */
    boolean removeFile(String path) throws ServerConnectionLostException;

    /**
     * Creates directory on sftp server
     * @param path
     * @return
     */
    boolean mkdir(String path) throws ServerConnectionLostException;

    /**
     * Upload file to sftp server.
     * In path don't add the file name, just absolute path of parent folder
     * @param fileToUpload
     * @param path
     * @return
     */
    boolean upload(File fileToUpload, String path) throws ServerConnectionLostException;


    /**
     * Upload a file through raw bytes
     * @param bytes
     * @param path
     * @return
     * @throws ServerConnectionLostException
     */
    boolean upload(byte[] bytes,String path) throws ServerConnectionLostException;



    boolean uploadChannelSftp(byte[] bytes,String path);



    /**
     * List all files inside directory
     * @param directory
     * @return
     */
    List listFilesFromDir(String directory) throws ServerConnectionLostException;


    /**
     * Check if certain file exist on server
     *
     * @param path
     * @return
     */
    boolean checkIfFileExists(String path) throws ServerConnectionLostException;

    /**
     * Get FileAttributes
     */

    FileAttributes getFileAttribute(String path) throws ServerConnectionLostException;


    /**
     * Get host address of client
     */
    String getHost();

    /**
     * Get username with which client is connected
     * @return
     */
    String getUsername();

    /**
     * Checks whether a connection is alive
     * @return
     */
    boolean isConnectionAlive();

    /**
     *
     * @return
     */
    int getPort();

    /**
     *
     * @return
     */
    String getPassword();
}
