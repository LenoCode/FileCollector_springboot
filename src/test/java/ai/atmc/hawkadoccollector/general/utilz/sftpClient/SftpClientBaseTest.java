package ai.atmc.hawkadoccollector.general.utilz.sftpClient;

import ai.atmc.hawkadoccollector.TestTools;
import ai.atmc.hawkadoccollector.utilz.sftp.exceptions.ServerConnectionLostException;
import ai.atmc.hawkadoccollector.utilz.sftp.impl.SftpClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
public class SftpClientBaseTest extends TestTools {

    @Value(value = "${hawkdoc.sftp.internal.host}")
    private String sftpHost;

    @Value(value = "${hawkdoc.sftp.internal.user}")
    private String user;

    @Value(value = "${hawkdoc.sftp.internal.password}")
    private String password;


    private SftpClient sftpClient;


    @BeforeEach
    public void setup(){
        this.sftpClient = new SftpClient(sftpHost,22,user,password);
        try {
            this.sftpClient.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check to see if initialization passes and checks if address is wrong
     * will it throw proper exception.
     *
     * If all credentials are right -> test assert True
     * If host is wrong -> test assert message "No route to host"
     * If user is wrong -> test assert message "Exhausted available authentication methods"
     */
    @ParameterizedTest
    @DisplayName("Check sftpClient initialization - internal server")
    @ValueSource(ints = {0,1,2})
    public void checkSftpClientInitializationInternalServer(Integer index){
        if(index == 0){
            try {
                boolean status = sftpClient.init();
                assertTrue(status);
            }catch (IOException e){
                assertNull(e);
            }

        }else if(index == 1){
            try {
                this.sftpClient = new SftpClient("10.100.111.203",22,user,password);

                boolean status = this.sftpClient.init();

                assertFalse(status);
            }catch (IOException e){
                assertEquals("No route to host",e.getMessage());
            }
        }else{
            try {
                this.sftpClient = new SftpClient(sftpHost,22,"wrongUser",password);

                boolean status = this.sftpClient.init();

                assertFalse(status);
            }catch (IOException e){
                assertEquals("Exhausted available authentication methods",e.getMessage());
            }
        }
    }


    /**
     * Uploads file to server and checks if everything went well
     */
    @Test
    @DisplayName("Check if file gets uploaded to sftp server")
    public void checkIfFileGetsUploadedToServer() throws ServerConnectionLostException {
        File content = new File(RESOURCE_DIR,"testFiles/textFiles/textFile1.txt");
        boolean status = sftpClient.upload(content,INTERNAL_SERVER_TEST_DATA);
        assertTrue(status);
    }

    /**
     * Uploads file to server using raw bytes and checks if everything went well
     */
    @Test
    @DisplayName("Check if file gets uploaded to sftp server using raw bytes method")
    public void checkIfFileGetsUploadedToServerUsingRawBytes() throws ServerConnectionLostException, IOException {
        File content = new File(RESOURCE_DIR,"testFiles/textFiles/textFile1.txt");
        FileInputStream fileInputStream = new FileInputStream(content);
        byte[] bytes = new byte[fileInputStream.available()];
        fileInputStream.read(bytes);

        boolean status = sftpClient.upload(bytes,INTERNAL_SERVER_TEST_DATA+File.separator+"rawBytes.txt");

        assertTrue(status);

        status = sftpClient.checkIfFileExists(INTERNAL_SERVER_TEST_DATA+File.separator+"rawBytes.txt");

        assertTrue(status);
    }

    /**
     * Uploads file to server and checks if everything went well
     */
    @Test
    @DisplayName("Check if directory gets created in sftp server and then deleted")
    public void checkIfDirGetsCreatedAndThenDeleted() throws InterruptedException, ServerConnectionLostException {
        boolean status = sftpClient.mkdir(INTERNAL_SERVER_TEST_DATA+File.separator+"testDir");
        assertTrue(status);
        status = sftpClient.removeDir(INTERNAL_SERVER_TEST_DATA+File.separator+"testDir");
        assertTrue(status);
    }


    /**
     * Uploads file to server and checks if everything went well
     */
    @Test
    @DisplayName("Check if downloading file without saving in temp is working")
    public void checkIfDownloadingFileWithoutSavingInTempWorks() throws InterruptedException, ServerConnectionLostException {
        File content = new File(RESOURCE_DIR,"testFiles/textFiles/textFile1.txt");
        boolean status = sftpClient.upload(content,INTERNAL_SERVER_TEST_DATA);
        assertTrue(status);

        byte[] bytesDownloaded = sftpClient.get(INTERNAL_SERVER_TEST_DATA+File.separator+"textFile1.txt");

        assertEquals(content.length(),bytesDownloaded.length);
    }

    @Test
    @DisplayName("Check if pdf gets uploaded to sftp server using raw bytes method")
    public void checkIfPdfGetsUploadedToServer() throws ServerConnectionLostException, IOException {
        File content = new File(RESOURCE_DIR,"testFiles/textFiles/book.pdf");
        FileInputStream fileInputStream = new FileInputStream(content);
        byte[] bytes = new byte[fileInputStream.available()];
        fileInputStream.read(bytes);

        boolean status = sftpClient.uploadChannelSftp(bytes,INTERNAL_SERVER_TEST_DATA+File.separator+"rawBytes.pdf");

        assertTrue(status);

        status = sftpClient.checkIfFileExists(INTERNAL_SERVER_TEST_DATA+File.separator+"rawBytes.pdf");

        assertTrue(status);
    }




    @AfterEach
    public void clear(){
        try {
            sftpClient.deleteAllFilesFromDir(INTERNAL_SERVER_TEST_DATA);
        } catch (ServerConnectionLostException e) {
            throw new RuntimeException(e);
        }
    }

}
