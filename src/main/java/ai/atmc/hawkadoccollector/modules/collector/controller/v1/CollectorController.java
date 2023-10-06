package ai.atmc.hawkadoccollector.modules.collector.controller.v1;


import ai.atmc.hawkadoccollector.config.baseClasses.controllerBase.ControllerBase;
import ai.atmc.hawkadoccollector.domain.dto.execution.ExecutionDto;
import ai.atmc.hawkadoccollector.domain.dto.execution.ExecutionRunResponseDto;
import ai.atmc.hawkadoccollector.modules.collector.services.collectorService.CollectorServiceInterface;
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
