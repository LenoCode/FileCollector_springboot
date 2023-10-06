package ai.atmc.hawkadoccollector.generalServices.process.impl;

import ai.atmc.hawkadoccollector.config.context.ApplicationContextProvider;
import ai.atmc.hawkadoccollector.domain.dto.execution.ExecutionDto;
import ai.atmc.hawkadoccollector.domain.dto.execution.ExecutionLocationAndFilters;
import ai.atmc.hawkadoccollector.generalServices.process.Process;
import ai.atmc.hawkadoccollector.generalServices.process.components.TargetFile;
import ai.atmc.hawkadoccollector.generalServices.process.components.TargetServer;
import ai.atmc.hawkadoccollector.generalServices.process.components.impl.BasicTargetServerImpl;
import ai.atmc.hawkadoccollector.generalServices.process.components.impl.ContinuesTargetServerImpl;
import ai.atmc.hawkadoccollector.generalServices.sftpServices.impl.InternalSftpService;
import ai.atmc.hawkadoccollector.utilz.sftp.exceptions.DownloadFileException;
import ai.atmc.hawkadoccollector.utilz.sftp.exceptions.ServerConnectionLostException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;

public class ContinuesSftpProcess implements Process {

    private final long executionId;

    private final String collectionName;

    private final ExecutionDto executionDto;

    private final ExecutionLocationAndFilters[] executionLocationAndFilters;
    private List<TargetServer> targetServerList;

    private InternalSftpService internalSftpService;

    private BiFunction<List<TargetFile>,Boolean, Boolean> onCollectTrigger;

    /**
     * @TODO -> In constructor there should be a object that contains all the information
     *          about where are documents located so they could be downloaded
     *
     * @TODO -> If read-only flag implement that goes to process id not execution id.
     */
    public ContinuesSftpProcess(ExecutionDto executionDto,ExecutionLocationAndFilters[] executionLocationAndFilters){
        this.executionId = executionDto.getExecutionId();
        this.executionDto = executionDto;
        this.executionLocationAndFilters = executionLocationAndFilters;
        this.targetServerList = new ArrayList<>();
        this.collectionName = "execution_".concat(String.valueOf(executionId));
        this.internalSftpService = new InternalSftpService(
                ApplicationContextProvider.getApplicationContext().getEnvironment().getProperty("hawkdoc.sftp.internal.host"),
                ApplicationContextProvider.getApplicationContext().getEnvironment().getProperty("hawkdoc.sftp.internal.password"),
                ApplicationContextProvider.getApplicationContext().getEnvironment().getProperty("hawkdoc.sftp.internal.user"),
                ApplicationContextProvider.getApplicationContext().getEnvironment().getProperty("hawkdoc.sftp.internal.port"),
                ApplicationContextProvider.getApplicationContext().getEnvironment().getProperty("hawkdoc.sftp.internal.execution.tempPath")
        );
        this.internalSftpService.init();
    }

    /**
     * Constructor used for TEST purpose. Changing the collection name
     * @param executionDto
     * @param executionLocationAndFilters
     * @param collectionName
     */
    public ContinuesSftpProcess(ExecutionDto executionDto,ExecutionLocationAndFilters[] executionLocationAndFilters,String collectionName){
        this.executionId = executionDto.getExecutionId();
        this.executionDto = executionDto;
        this.executionLocationAndFilters = executionLocationAndFilters;
        this.targetServerList = new ArrayList<>();
        this.collectionName = collectionName;
        this.internalSftpService = new InternalSftpService(
                ApplicationContextProvider.getApplicationContext().getEnvironment().getProperty("hawkdoc.sftp.internal.host"),
                ApplicationContextProvider.getApplicationContext().getEnvironment().getProperty("hawkdoc.sftp.internal.password"),
                ApplicationContextProvider.getApplicationContext().getEnvironment().getProperty("hawkdoc.sftp.internal.user"),
                ApplicationContextProvider.getApplicationContext().getEnvironment().getProperty("hawkdoc.sftp.internal.port"),
                ApplicationContextProvider.getApplicationContext().getEnvironment().getProperty("hawkdoc.sftp.internal.execution.tempPath")
        );
        this.internalSftpService.init();
    }

    /**
     * No args constructor, more for purpose of testing
     */
    public ContinuesSftpProcess(){
        this.executionId = -1;
        this.executionDto = null;
        this.collectionName = null;
        this.executionLocationAndFilters = new ExecutionLocationAndFilters[0];
    }


    @Override
    public void setup() throws ServerConnectionLostException {
        internalSftpService.makeCollectionTempDir(collectionName);

        for(ExecutionLocationAndFilters executionAndLocation: executionLocationAndFilters){
            ContinuesTargetServerImpl basicTargetServer = new ContinuesTargetServerImpl(executionAndLocation);
            basicTargetServer.init();
            targetServerList.add(basicTargetServer);
        }
    }

    @Override
    public void run() {
        for(TargetServer targetServer : targetServerList){
            try {
                if(onCollectTrigger == null){
                    targetServer.collect(collectionName);
                }else{
                    targetServer.collect(collectionName,onCollectTrigger);
                }
            } catch (Exception e) {
                if(e instanceof DownloadFileException){
                    System.out.println("Error while downloading file");
                }else{
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns all targetServer list
     * @return
     */
    public List<TargetServer> getTargetServerList() {
        return targetServerList;
    }

    /**
     * On Collect trigger function
     * @param onCollectTrigger
     */
    public void setOnCollectTrigger( BiFunction<List<TargetFile>,Boolean, Boolean> onCollectTrigger) {
        this.onCollectTrigger = onCollectTrigger;
    }

}
