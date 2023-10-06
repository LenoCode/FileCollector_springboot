package ai.atmc.hawkadoccollector.restClients.executionManager;


import ai.atmc.hawkadoccollector.restClients.executionManager.dto.IterationCollectorRestDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/v1/execution/")
public interface ExecutionIterationControllerRest {



    /**
     * Sending information to scheduler to make triggers and jobs (in short setup scheduler process with trigger)
     * @param executionFlowId
     * @param iterationCollectorRestDto
     * @return
     */
    @PostExchange("/execute/{flow}/iterations")
    String sendIterationsOfDocument(@PathVariable("execution_flow_id") Integer flow, @RequestBody IterationCollectorRestDto iterationCollectorRestDto);
}
