package ai.atmc.hawkadoccollector.generalServices.process.impl;

import ai.atmc.hawkadoccollector.config.context.ApplicationContextProvider;
import ai.atmc.hawkadoccollector.domain.dto.execution.ExecutionDto;
import ai.atmc.hawkadoccollector.domain.dto.execution.ExecutionLocationAndFilters;
import ai.atmc.hawkadoccollector.generalServices.process.Process;
import ai.atmc.hawkadoccollector.generalServices.process.components.TargetFile;
import ai.atmc.hawkadoccollector.generalServices.process.components.TargetServer;
import ai.atmc.hawkadoccollector.generalServices.process.components.impl.BasicTargetServerImpl;
import ai.atmc.hawkadoccollector.generalServices.sftpServices.impl.InternalSftpService;
import ai.atmc.hawkadoccollector.utilz.sftp.exceptions.DownloadFileException;
import ai.atmc.hawkadoccollector.utilz.sftp.exceptions.ServerConnectionLostException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * Simple process or one time process
 * This is process that will execute steps and when all steps are finished it will end
 */

public class SimpleSftpProcess implements Process {

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
    public SimpleSftpProcess(ExecutionDto executionDto,ExecutionLocationAndFilters[] executionLocationAndFilters){
        this.executionId = executionDto.getExecutionId();
        this.executionDto = executionDto;
        this.executionLocationAndFilters = executionLocationAndFilters;
        this.targetServerList = new ArrayList<>();

        if(executionDto.isCheckOnlyNew()){
            this.collectionName = "execution_".concat(String.valueOf(executionDto.getProcessId()));
        }else{
            this.collectionName = "execution_".concat(String.valueOf(executionId));

        }
        this.internalSftpService = ApplicationContextProvider.getApplicationContext().getBean(InternalSftpService.class);

    }

    /**
     * No args constructor, more for purpose of testing
     */
    public SimpleSftpProcess(){
        this.executionId = -1;
        this.executionDto = null;
        this.collectionName = null;
        this.executionLocationAndFilters = new ExecutionLocationAndFilters[0];
    }


    /**
     * Setup all the sftp clients
     * Creates a temp dir and creates target servers
     */
    @Override
    public void setup() throws ServerConnectionLostException {
        internalSftpService.makeCollectionTempDir(collectionName);

        for(ExecutionLocationAndFilters executionAndLocation: executionLocationAndFilters){
            BasicTargetServerImpl basicTargetServer = new BasicTargetServerImpl(executionAndLocation);
            basicTargetServer.init();
            targetServerList.add(basicTargetServer);
        }
    }


    /**
     * Simple run
     */
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
