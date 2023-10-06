package com.leno.example.restClients.executionManager.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class IterationCollectorRestDto {
    private List<BatchIteration> iterations;
    private boolean lastBatch;

    @Override
    public String toString() {
        return "IterationCollectorRestDto{" +
                "iterations=" + iterations +
                ", lastBatch=" + lastBatch +
                '}';
    }
}
