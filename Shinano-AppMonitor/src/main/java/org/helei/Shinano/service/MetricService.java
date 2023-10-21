package org.helei.Shinano.service;


import org.helei.Shinano.pojo.PerformanceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class MetricService {
    @Autowired
    public MethodMetricService methodMetricService;

    public List<PerformanceInfo> getPerformanceInfo() {
        try {
            return methodMetricService.getMethodPerformanceInfo().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
