package ai.atmc.hawkadoccollector.restClients.executionManager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Collector {
    private String type;
    private LocationFile info;
}
