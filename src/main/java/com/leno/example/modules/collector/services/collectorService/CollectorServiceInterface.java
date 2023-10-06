package com.leno.example.modules.collector.services.collectorService;


import com.leno.example.domain.dto.execution.ExecutionDto;
import com.leno.example.generalServices.process.Process;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

/**
 * Collector Service interface
 *
 * This is a user service - meaning it acts like roof for all the sub services for collecting.
 * This service contains a Process service for example
 */
public interface CollectorServiceInterface {

    /**
     *
     * @param executionDto
     * @param <A> only interfaces Process, meaning one function will run all types of processes
     */
    @Async
    <A extends Process> CompletableFuture<Boolean> runProcess(ExecutionDto executionDto) throws Exception;


    /**
     * Mostly for testing purpose
     * @param process
     */
    void manuallyAddProcess(long executionId,Process process);


    /**
     *
     */
    @Async
    <A extends Process> CompletableFuture<Boolean> runContinuesHandlerProcess() throws InterruptedException;



    /**
     * Check to see if continues process handler is alive
     */

    boolean isContinuesProcessHandlerAlive();

    /**
     * How many active process are running currentyl
     * @return
     */
    int numberOfActiveContinuesProcess();

    /**
     *
     * @return
     */
    boolean removeContinuesProcess(long executionId);
}
