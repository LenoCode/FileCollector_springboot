package com.leno.example.domain.dto.execution;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;


@AllArgsConstructor
@Getter
@Setter
public class ExecutionDto {

    private long executionId;

    private long executionFlowId;

    private long processId;

    private HashMap<String,Object> properties;
    private String collectorType;

    private String executionType;

    private boolean checkOnlyNew;


    @Override
    public String toString() {
        return "ExecutionDto{" +
                "executionId=" + executionId +
                ", executionFlowId=" + executionFlowId +
                ", collectorType='" + collectorType + '\'' +
                ", executionType='" + executionType + '\'' +
                ", properties=" + properties +
                '}';
    }
}
