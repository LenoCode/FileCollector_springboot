package ai.atmc.hawkadoccollector.general.utilz.collector;


import ai.atmc.hawkadoccollector.config.context.ApplicationContextProvider;
import ai.atmc.hawkadoccollector.config.thread.ThreadExecutorConfiguration;
import ai.atmc.hawkadoccollector.generalServices.process.Process;
import ai.atmc.hawkadoccollector.generalServices.process.components.TargetFile;
import ai.atmc.hawkadoccollector.generalServices.redisService.impl.RedisServiceHSetImpl;
import ai.atmc.hawkadoccollector.generalServices.sftpServices.SftpService;
import ai.atmc.hawkadoccollector.generalServices.sftpServices.impl.InternalSftpService;
import ai.atmc.hawkadoccollector.modules.collector.services.collectorService.CollectorServiceInterface;
import ai.atmc.hawkadoccollector.modules.collector.services.collectorService.impl.CollectorServiceImpl;
import ai.atmc.hawkadoccollector.spring.unit.servicesTest.moduleServices.collector.CollectorServiceTests;
import ai.atmc.hawkadoccollector.utilz.sftp.exceptions.ServerConnectionLostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
@SpringBootTest(classes = {ThreadExecutorConfiguration.class,InternalSftpService.class, SftpService.class, ApplicationContextProvider.class, CollectorServiceInterface.class, CollectorServiceImpl.class, RedisServiceHSetImpl.class})
public class CollectorTests {




    @Autowired
    private CollectorServiceInterface collectorServiceInterface;


    /**
     * Check if thread initialization works correctly
     */
    @Test
    public void testContinuesProcessInitialization() throws InterruptedException {
        Future<Boolean> future = collectorServiceInterface.runContinuesHandlerProcess();
        assertTrue(collectorServiceInterface.isContinuesProcessHandlerAlive());
        assertTrue(!future.isDone());

    }



    /**
     * Check if thread initialization works correctly
     */
    @Test
    @DisplayName("Checking process adding removing iterating")
    public void testProcessAddingRemovingIteratingContinues() throws InterruptedException {
        Future<Boolean> future = collectorServiceInterface.runContinuesHandlerProcess();
        assertTrue(collectorServiceInterface.isContinuesProcessHandlerAlive());
        assertTrue(!future.isDone());

        final int[] counter = {0};

        collectorServiceInterface.manuallyAddProcess(10,new Process() {
            @Override
            public void setup() throws ServerConnectionLostException {
            }

            @Override
            public void run() {
                counter[0]++;
            }

            @Override
            public void setOnCollectTrigger(BiFunction<List<TargetFile>, Boolean, Boolean> onCollectTrigger) {

            }
        });

        Thread.sleep(10);

        assertNotEquals(counter[0],0);

        boolean status = collectorServiceInterface.removeContinuesProcess(10);

        assertTrue(status);
        assertEquals(collectorServiceInterface.numberOfActiveContinuesProcess(),0);

    }

    /**
     * Check if thread initialization works correctly
     *
     *
     * Add 100 process
     * Check if they are all running -> all of them should change counter value, other words increase it
     *
     * One by one remove from the list and check if the number of processes is decreasing
     *
     * Then check if all the counters stays the same, meaning no process is running and increasing counters
     */
    @Test
    @DisplayName("Checking process adding removing iterating- EXTENSIVE")
    public void testProcessAddingRemovingIteratingContinuesExtensive() throws InterruptedException {
        Future<Boolean> future = collectorServiceInterface.runContinuesHandlerProcess();
        Thread.sleep(10);
        assertTrue(collectorServiceInterface.isContinuesProcessHandlerAlive());

        final int[][] ids = new int[100][1];


        for(int i = 0; i < 100; ++i){
            final int index = i;
            ids[i] = new int[]{0};

            collectorServiceInterface.manuallyAddProcess(i, new Process() {
                @Override
                public void setup() throws ServerConnectionLostException {

                }

                @Override
                public void run() {
                    ids[index][0]++;
                }

                @Override
                public void setOnCollectTrigger(BiFunction<List<TargetFile>, Boolean, Boolean> onCollectTrigger) {

                }
            });
        }
        Thread.sleep(50);

        for(int i = 0; i < 100; ++i){
            assertNotEquals(ids[i][0],0);
        }
        assertEquals(collectorServiceInterface.numberOfActiveContinuesProcess(),100);

        for(int i = 0; i < 100; ++i){
            collectorServiceInterface.removeContinuesProcess(i);
            assertEquals(collectorServiceInterface.numberOfActiveContinuesProcess(),100 - (i + 1));
        }


        int[] currentValuesOfProcessAfterStopping = new int[100];

        for(int i = 0; i < 100; ++i){
            currentValuesOfProcessAfterStopping[i] = ids[i][0];
        }
        Thread.sleep(100);

        for(int i = 0; i < 100; ++i){
            assertEquals(currentValuesOfProcessAfterStopping[i],ids[i][0]);
        }
    }




}
