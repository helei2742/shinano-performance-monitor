package org.helei.Shinano.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceInfo {
    private String methodName;
    private Long avgCostTime;
    private Long maxCostTime;
    private Long minCostTime;

    private Double throughput;
    private Map<Double, Double> quantile;
}
