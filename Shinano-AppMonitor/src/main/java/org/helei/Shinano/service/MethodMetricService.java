package org.helei.Shinano.service;

import org.helei.Shinano.config.ShinanoMethodConfig;
import org.helei.Shinano.pojo.PerformanceInfo;
import org.helei.Shinano.pojo.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class MethodMetricService {
    private static final Logger logger = LoggerFactory.getLogger(MethodMetricService.class);

    private static final Map<String, Metric> methodMetrics = new ConcurrentHashMap<>();

    private ThreadPoolExecutor executor;

    public MethodMetricService(@Autowired ShinanoMethodConfig config) {
        executor = new ThreadPoolExecutor(config.getThreadPoolCore(),
                config.getThreadPoolMax(),
                config.getWaitTime(),
                TimeUnit.MINUTES,
                new LinkedBlockingDeque<>());
    }

    /**
     * 添加耗时
     * @param methodName 方法名
     * @param sample   样本数
     * @param timeStamp  时间戳
     * @param costTime   消耗时间
     * @throws IllegalAccessException
     */
    public void addCostTime(String methodName, int sample, long timeStamp, long costTime) throws IllegalAccessException {
        methodMetrics.putIfAbsent(methodName, new Metric(sample));

        Metric metric = methodMetrics.get(methodName);

        executor.execute(()->{
            long writeIdx = -1;
            while ((writeIdx = metric.tryWrite()) == -1) {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    logger.error("error when add method cost time,method name[{}], cost time[{}]",
                            methodName, costTime, e);
                }
            }
            metric.addCostTime(writeIdx, timeStamp, costTime);
        });


        logger.debug("method[{}] execute cost time added metric, cost time[{}]",
                methodName, costTime);
    }


    public Future<List<PerformanceInfo>> getMethodPerformanceInfo() {

        Future<List<PerformanceInfo>> future = executor.submit(() -> {
            List<PerformanceInfo> res = new ArrayList<>();
            methodMetrics.forEach((k, metric) -> {
                long readIdx = -1;
                while ((readIdx = metric.tryRead()) == -1) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        logger.error("error when get method performance info,method name[{}]", k, e);
                    }
                }
                if(readIdx > 0){
                    try {
                        PerformanceInfo info = metric.getPerformanceInfo();
                        info.setMethodName(k);
                        res.add(info);
                    }finally {
                        metric.readEnd();
                    }
                }
                System.out.println(metric);
            });
            return res;
        });

        logger.info("getMethodPerformanceInfo task submit");
        return future;
    }
}
