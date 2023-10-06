package com.leno.example.restClients.executionManager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Original {

    private String filePath;
    private String fileName;

    private Collector collector;
}
