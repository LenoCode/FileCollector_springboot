package ai.atmc.hawkadoccollector.restClients.executionManager.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Properties;
@Getter
@Setter
public class Temp {
    private String filePath;
    private String fileName;
    private LocationFile storage;
}
