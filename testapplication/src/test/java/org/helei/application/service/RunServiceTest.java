package org.helei.application.service;


import org.helei.Shinano.service.MetricService;
import org.helei.application.Starter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@SpringBootTest(classes = Starter.class)
class RunServiceTest {

    @Autowired
    private RunService runService;

    @Autowired
    private MetricService metricService;

    @Test
    void run() throws InterruptedException {
        int threadCount = 10;
        CountDownLatch downLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(()->{
                for (int j = 0; j < 100; j++) {
                    runService.run();
                    try {
                        TimeUnit.MILLISECONDS.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                downLatch.countDown();
            }).start();
        }

        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Thread thread = new Thread(() -> {
                while(true) {
                    metricService.getPerformanceInfo().forEach(System.out::println);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            threadList.add(thread);
            thread.start();
        }


        downLatch.await();
        for (Thread thread : threadList) {
            thread.join();
        }

        System.out.println(metricService.getPerformanceInfo());
    }
}
