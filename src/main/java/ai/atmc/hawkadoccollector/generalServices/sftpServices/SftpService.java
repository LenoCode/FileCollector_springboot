package ai.atmc.hawkadoccollector.generalServices.sftpServices;

import ai.atmc.hawkadoccollector.utilz.sftp.exceptions.ServerConnectionLostException;
import net.schmizz.sshj.sftp.FileAttributes;

import java.io.IOException;

public interface SftpService {

    /**
     * Upload file to temp folder.
     * Passing just raw bytes and fileName.
     * Rest of the path (like directory location) should be added in class that will implement this interface
     * @param bytes
     * @param collectionName
     * @param fileName
     * @return boolean
     */
    boolean uploadToTemp(byte[] bytes,String collectionName,String fileName);

    /**
     * Make directory in which collection will be collected
     * @param name
     * @return
     */
    boolean makeCollectionTempDir(String name);

    /**
     * remove directory. This process will usually be
     * called when all the files were collected and execution was done
     * @param name
     * @return
     */
    boolean removeCollectionDir(String name);

    /**
     * Check if file exist in temp folder under specific directories
     * @param parentDir
     * @param file
     * @return
     */
    boolean fileExistInTempCollection(String parentDir,String file) throws ServerConnectionLostException;

    /**
     * Get uplaoded file size in temp dir
     * @return
     */
    long getUploadedFileSize(String parentDir,String file)throws ServerConnectionLostException;


    /**
     *
     * @param fileName
     * @return
     */
    String getFullPathWithFilename(String collectionName,String fileName);

}
