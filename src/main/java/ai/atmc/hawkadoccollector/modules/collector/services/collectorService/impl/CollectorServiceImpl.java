package ai.atmc.hawkadoccollector.modules.collector.services.collectorService.impl;

import ai.atmc.hawkadoccollector.config.context.ApplicationContextProvider;
import ai.atmc.hawkadoccollector.domain.dto.execution.ExecutionDto;
import ai.atmc.hawkadoccollector.domain.dto.execution.ExecutionLocationAndFilters;
import ai.atmc.hawkadoccollector.domain.enums.CollectorTypeEnum;
import ai.atmc.hawkadoccollector.generalServices.process.Process;
import ai.atmc.hawkadoccollector.generalServices.process.components.TargetFile;
import ai.atmc.hawkadoccollector.generalServices.process.impl.ContinuesSftpProcess;
import ai.atmc.hawkadoccollector.generalServices.process.impl.SimpleSftpProcess;
import ai.atmc.hawkadoccollector.generalServices.redisService.impl.RedisServiceHSetImpl;
import ai.atmc.hawkadoccollector.modules.collector.services.collectorService.CollectorServiceInterface;
import ai.atmc.hawkadoccollector.restClients.executionManager.BaseExecutionManagerRestClient;
import ai.atmc.hawkadoccollector.restClients.executionManager.dto.*;
import ai.atmc.hawkadoccollector.utilz.sftp.exceptions.ServerConnectionLostException;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


@Service
public class CollectorServiceImpl implements CollectorServiceInterface {

    @Value(value = "${hawkdoc.sftp.internal.host}")
    private String sftpHost;

    @Value(value = "${hawkdoc.sftp.internal.port}")
    private Integer sftpPort;

    @Value(value = "${hawkdoc.sftp.internal.user}")
    private String user;

    @Value(value = "${hawkdoc.sftp.internal.password}")
    private String password;

    @Autowired
    private RedisServiceHSetImpl redisService;


    private volatile boolean CONTINUES_PROCESS_ALIVE = false;
    
    private ReentrantLock iteratorLocker = new ReentrantLock();

    private final ConcurrentHashMap<Long,Process> activeProcesses = new ConcurrentHashMap<>();




    /*--------------------------------------------------CONTINUES PROCESS CODE-----------------------------------------------------------------------------------------*/


    /**
     *
     */
    @Async
    public CompletableFuture<Boolean>  runContinuesHandlerProcess() throws InterruptedException {
        CompletableFuture<Boolean> asyncFunctionResult = new CompletableFuture<>();
        CONTINUES_PROCESS_ALIVE = true;

        while(CONTINUES_PROCESS_ALIVE){
            iteratorLocker.lock();
            try {
                for(Process process : activeProcesses.values()){
                    process.run();
                }
            }finally {
                iteratorLocker.unlock();
            }

        }
        System.out.println("Continues "+CONTINUES_PROCESS_ALIVE);
        asyncFunctionResult.complete(true);
        return asyncFunctionResult;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isContinuesProcessHandlerAlive() {
        return CONTINUES_PROCESS_ALIVE;
    }

    /**
     *
     * @return
     */
    @Override
    public int numberOfActiveContinuesProcess() {
        return activeProcesses.size();
    }

    /**
     *
     * @param executionId
     * @return
     */
    @Override
    public boolean removeContinuesProcess(long executionId) {
        iteratorLocker.lock();
        try {
            if(activeProcesses.containsKey(executionId)){
                activeProcesses.remove(executionId);
                redisService.removeHSet("execution_"+executionId);
                return true;
            }
            return false;
        }finally {
            iteratorLocker.unlock();
            System.out.println("Remove iterator unlock ");
        }

    }


    /**
     *
     * @param executionId
     * @param process
     */

    @Override
    public void manuallyAddProcess(long executionId,Process process) {
        iteratorLocker.lock();
        try {
            activeProcesses.put(executionId,process);
        }finally {
            iteratorLocker.unlock();
            System.out.println("Adding iterator unlock");
        }
    }

    /**
     *
     * @param executionDto
     * @return
     */
    private Process createContinuesProcess(ExecutionDto executionDto){
        String type = executionDto.getCollectorType();

        /**
         * Check string if it contains 'collector_internal_server' or 'collector_sftp_ftp'
         */
        if(type.equals(CollectorTypeEnum.COLLECTOR_INTERNAL_SERVER.getType()) || type.equals(CollectorTypeEnum.COLLECTOR_LOCAL_UPLOAD.getType()) ){
            ExecutionLocationAndFilters executionLocationAndFilters = mapFromJsonDtoToExecutionLocationAndFilters(executionDto,true);
            return createContinuesProcessFunction(executionDto, new ExecutionLocationAndFilters[]{executionLocationAndFilters});
        }
        else if (type.equals(CollectorTypeEnum.COLLECTOR_SFTP_FTP.getType())){
            ExecutionLocationAndFilters executionLocationAndFilters = mapFromJsonDtoToExecutionLocationAndFilters(executionDto,false);
            return createContinuesProcessFunction(executionDto, new ExecutionLocationAndFilters[]{executionLocationAndFilters});
        }
        else{
            return null;
        }
    }


    /*-------------------------------------------------------------------------------------------------------------------------------------------*/


    /**
     * @param executionDto
     * @param <A>
     */
    @Async
    @Override
    public <A extends Process> CompletableFuture<Boolean> runProcess(ExecutionDto executionDto) throws Exception {
        if(checkIfExecutionDtoIsValid(executionDto)){

            if(isSimpleProcess(executionDto)){
                System.out.println("createSimpleProcess");
                return createSimpleProcess(executionDto);
            }else{
                Process process = createContinuesProcess(executionDto);

                iteratorLocker.lock();
                try {
                    if(process != null){
                        activeProcesses.put(executionDto.getExecutionId(),process);
                    }
                }finally {
                    System.out.println("Unlocking iterator");
                    iteratorLocker.unlock();
                }
                return CompletableFuture.completedFuture(true);
            }
        }else{
            throw new Exception("Execution dto is not valid");
        }
    }



    /**
     *
     * @param executionDto
     * @return
     */
    private CompletableFuture<Boolean> createSimpleProcess(ExecutionDto executionDto){
        String type = executionDto.getCollectorType();

        /**
         * Check string if it contains 'collector_internal_server' or 'collector_sftp_ftp'
         */
        if(type.equals(CollectorTypeEnum.COLLECTOR_INTERNAL_SERVER.getType()) || type.equals(CollectorTypeEnum.COLLECTOR_LOCAL_UPLOAD.getType()) ){
            ExecutionLocationAndFilters executionLocationAndFilters = mapFromJsonDtoToExecutionLocationAndFilters(executionDto,true);
            return createSimpleProcessFunction(executionDto, new ExecutionLocationAndFilters[]{executionLocationAndFilters});
        }
        else if (type.equals(CollectorTypeEnum.COLLECTOR_SFTP_FTP.getType())){
            ExecutionLocationAndFilters executionLocationAndFilters = mapFromJsonDtoToExecutionLocationAndFilters(executionDto,false);
            return createSimpleProcessFunction(executionDto, new ExecutionLocationAndFilters[]{executionLocationAndFilters});
        }
        else{
            return null;
        }
    }




    /**
     *
     * @param executionDto
     * @return
     */
    protected ExecutionLocationAndFilters mapFromJsonDtoToExecutionLocationAndFilters(ExecutionDto executionDto,boolean internal){
        HashMap<String,Object> properties = executionDto.getProperties();
        ExecutionLocationAndFilters executionLocationAndFilters = new ExecutionLocationAndFilters();

        try {
            if(!internal){
                executionLocationAndFilters.setHost((String) checkForKeyInProperties("host",properties));
                executionLocationAndFilters.setPort((Integer) checkForKeyInProperties("port",properties));
                executionLocationAndFilters.setUsername((String) checkForKeyInProperties("username",properties));
                executionLocationAndFilters.setPassword((String) checkForKeyInProperties("password",properties));
                executionLocationAndFilters.setLocations((List<String>) checkForKeyInProperties("locations",properties));
                executionLocationAndFilters.setFilters((HashMap<String, Object>) checkForKeyInProperties("filters",properties));

            }else{
                executionLocationAndFilters.setHost(sftpHost);
                executionLocationAndFilters.setPort(sftpPort);
                executionLocationAndFilters.setUsername(user);
                executionLocationAndFilters.setPassword(password);
                executionLocationAndFilters.setLocations((List<String>) checkForKeyInProperties("locations",properties));
                executionLocationAndFilters.setFilters((HashMap<String, Object>) checkForKeyInProperties("filters",properties));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return executionLocationAndFilters;
    }

    /**
     *
     * @param key
     * @param properties
     * @return
     */
    protected static Object checkForKeyInProperties(String key,HashMap<String,Object> properties) throws ValidationException{
        if(properties.containsKey(key)){
            return properties.get(key);
        }else{
            throw new ValidationException("Properties for collector are missing "+key);
        }
    }

    /**
     * On collect Trigger
     * Will collect files and send in batches
     * @param executionDto
     * @return
     */
    protected static CompletableFuture<Boolean> createSimpleProcessFunction(ExecutionDto executionDto,
                                                                            ExecutionLocationAndFilters[] executionLocationAndFilters){
        CompletableFuture<Boolean> asyncFunctionResult = new CompletableFuture<>();

        SimpleSftpProcess simpleSftpProcess = new SimpleSftpProcess(executionDto,executionLocationAndFilters);
        try {
            simpleSftpProcess.setOnCollectTrigger((targetFiles, aBoolean) -> {
               return notifyExecutionManager(executionDto.getExecutionFlowId(),targetFiles,aBoolean);
            });
            simpleSftpProcess.setup();
            simpleSftpProcess.run();

            asyncFunctionResult.complete(true);
        } catch (ServerConnectionLostException e) {
            e.printStackTrace();
            asyncFunctionResult.complete(false);
        }
        return asyncFunctionResult;
    }

    /**
     * On collect Trigger
     * Will collect files and send in batches
     * This is for continues process. We are not running the process here, we are just setting it up
     * @param executionDto
     * @return
     */
    protected static Process createContinuesProcessFunction(ExecutionDto executionDto,
                                                            ExecutionLocationAndFilters[] executionLocationAndFilters){

        ContinuesSftpProcess continuesSftpProcess = new ContinuesSftpProcess(executionDto,executionLocationAndFilters);
        try {
            continuesSftpProcess.setOnCollectTrigger((targetFiles, aBoolean) -> {
                return notifyExecutionManager(executionDto.getExecutionFlowId(),targetFiles,aBoolean);
            });
            continuesSftpProcess.setup();
        } catch (ServerConnectionLostException e) {
            return null;
        }
        return continuesSftpProcess;
    }



    /**
     * Check if execution dto contains proper data
     * @param executionDto
     * @return
     */
    private boolean checkIfExecutionDtoIsValid(ExecutionDto executionDto){
        if(executionDto == null)return false;
        return true;
    }

    /**
     * is simple process, meaning not continuous
     * @TODO Need implemenation logic
     * @return
     */
    private boolean isSimpleProcess(ExecutionDto executionDto){
        if(executionDto.getExecutionType().equals("trigger")){
            return true;
        }
        return false;
    }


    /**
     *
     * @param executionFlowId
     * @param batch
     * @param status
     * @return
     */
    protected static boolean notifyExecutionManager(long executionFlowId,List<TargetFile> batch,boolean status ){
        BaseExecutionManagerRestClient baseExecutionManagerRestClient =   ApplicationContextProvider.getApplicationContext().getBean(BaseExecutionManagerRestClient.class);
        List<BatchIteration> iterations = new ArrayList<>();

        for(TargetFile targetFile: batch){
            BatchIteration batchIteration = new BatchIteration();
            batchIteration.setIterationType("Document");
            batchIteration.setNumOfRecords(1);
            batchIteration.setIterationName(targetFile.getName());


            IterationMetaData iterationMetaData = new IterationMetaData();


            Original original = new Original();
            original.setFilePath(targetFile.getAbsolutePath());
            original.setFileName(targetFile.getName());
            original.setCollector(
                    new Collector(
                            "sftp",
                            new LocationFile(targetFile.getFtp_host(),targetFile.getPort(),targetFile.getUsername(),targetFile.getPassword())
                    ));

            Temp temp = new Temp();
            temp.setFileName(targetFile.getTargetFilename());
            temp.setFilePath(targetFile.getTargetFilePath());
            temp.setStorage(new LocationFile(targetFile.getTargetFtpHost(),targetFile.getTargetPort(),targetFile.getTargetUsername(),targetFile.getTargetPassword()));

            iterationMetaData.setOriginal(original);
            iterationMetaData.setTemp(temp);

            batchIteration.setIterationMetadata(iterationMetaData);

            iterations.add(batchIteration);
        }
        try {
            IterationCollectorRestDto iterationCollectorRestDto = new IterationCollectorRestDto(iterations,status);
            iterationCollectorRestDto.setLastBatch(status);
            baseExecutionManagerRestClient.sendIterationsOfDocument((int)executionFlowId,iterationCollectorRestDto);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

}
