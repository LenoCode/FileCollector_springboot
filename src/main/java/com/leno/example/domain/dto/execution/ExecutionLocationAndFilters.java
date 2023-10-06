package com.leno.example.domain.dto.execution;

import com.leno.example.domain.dto.DtoModel;
import lombok.*;

import java.util.HashMap;
import java.util.List;


/**
 * Dto that contains all the information on a single sftp server.
 * Meaning all the location on which to search the files and how to filter them
 *
 *
 *  example
 *
 *  {
 *    "host": "10.100.111.200",
 *    "port": 3020,
 *    "username": "hawkadoc",
 *    "password": "Fritaja123",
 *    "locations": ["C:\Documents\Hawk-a-doc", "C:\Documents\test"],
 *    "filters": {
 *
 * 	   "fileName": "[a-z]{24}",
 * 	   "createdBy": ["Katarina"],
 * 	   "createdTime" : 300000, -- last seconds
 * 	   "modifiedTime": ""
 *    }
 */
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class ExecutionLocationAndFilters implements DtoModel {

    @NonNull
    private String host;
    @NonNull

    private int port;
    @NonNull

    private String username;
    @NonNull

    private String password;
    @NonNull

    private List<String> locations;

    @NonNull
    private HashMap<String,Object> filters;



    @Override
    public String toString() {
        return "ExecutionLocationAndFilters{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", locations=" + locations +
                ", filters=" + filters +
                '}';
    }
}
