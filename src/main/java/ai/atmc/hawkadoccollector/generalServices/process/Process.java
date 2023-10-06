package ai.atmc.hawkadoccollector.generalServices.process;


import ai.atmc.hawkadoccollector.generalServices.process.components.TargetFile;
import ai.atmc.hawkadoccollector.utilz.sftp.exceptions.ServerConnectionLostException;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Main interface for creating new process that wil collect files
 * for one execution task
 *
 *
 */
public interface Process {


    /**
     * Setup method that is called before start
     */
    void setup() throws ServerConnectionLostException;


    /**
     * Update process
     * This will mostly be used for Continues processes.
     * This update function will be constantly called by
     * main process handler - loop
     */
    void run();


    /**
     *Triggers, function that will be called for each file collected
     * @param onCollectTrigger
     */
    void setOnCollectTrigger( BiFunction<List<TargetFile>,Boolean, Boolean> onCollectTrigger);
}
