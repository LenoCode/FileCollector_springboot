package ai.atmc.hawkadoccollector.restClients.executionManager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchIteration {
    private String iterationType;

    /**
     * Always one, this one is more for databases
     */
    private int numOfRecords;

    /**
     * FIle name
     */
    private String iterationName;


    private IterationMetaData iterationMetadata;
}
