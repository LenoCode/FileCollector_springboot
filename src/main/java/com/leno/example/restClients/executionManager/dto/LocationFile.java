package com.leno.example.restClients.executionManager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LocationFile {

    private String ftpHost;
    private int port;
    private String username;
    private String password;
}
