package com.leno.example.utilz.sftp.impl;

import com.leno.example.utilz.sftp.SftpClientInterface;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class SftpClientAbstract implements SftpClientInterface {


    /**
     * Create main object for sftp communication
     * @return
     * @throws IOException
     */
    protected SSHClient setupSshj(String sftpHost,int port,String user,String password) throws IOException {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.connect(sftpHost,port);
        client.authPassword(user,password);
        return client;
    }

    /**
     * Checks whether a timeout exception occurred.
     * Usually that would mean that connection has been lost and it should be reconnected
     * @param sftpException
     * @return
     */
    protected boolean hasTimeoutExceptionOccured(SFTPException sftpException){
        return sftpException.getCause() instanceof TimeoutException;
    }
}
