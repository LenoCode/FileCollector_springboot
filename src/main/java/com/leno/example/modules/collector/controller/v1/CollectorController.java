package com.leno.example.modules.collector.controller.v1;


import com.leno.example.config.baseClasses.controllerBase.ControllerBase;
import com.leno.example.domain.dto.execution.ExecutionDto;
import com.leno.example.domain.dto.execution.ExecutionRunResponseDto;
import com.leno.example.modules.collector.services.collectorService.CollectorServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path="/v1/collector")
public class CollectorController extends ControllerBase {


    @Autowired
    private CollectorServiceInterface collectorService;


    /**
     *
     * @param executionDto
     * @return
     */
    @PostMapping("run/new/execution")
    public ExecutionRunResponseDto runNewExecution(@RequestBody ExecutionDto executionDto) throws Exception {
        CompletableFuture<Boolean> future = collectorService.runProcess(executionDto);
        return new ExecutionRunResponseDto(future != null,future.isDone());
    }

    @GetMapping("stop/continues/execution/{id}")
    public ResponseEntity<String> stopContinuesExecution(@PathVariable Integer id) throws Exception {
        collectorService.removeContinuesProcess(id);
        return ResponseEntity.ok("Execution stopped");
    }
}
