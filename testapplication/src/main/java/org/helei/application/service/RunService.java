package org.helei.application.service;

import org.helei.Shinano.annotation.ApplicationMethodMonitor;
import org.helei.Shinano.service.MetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class RunService {

    @Autowired
    private MetricService metricService;
    private static AtomicInteger count = new AtomicInteger(0);

    @ApplicationMethodMonitor(sample = 50)
    public String run(){
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return String.valueOf(count.incrementAndGet());
    }
}
