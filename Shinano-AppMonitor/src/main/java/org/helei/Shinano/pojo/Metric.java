package org.helei.Shinano.pojo;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.helei.Shinano.util.CommonUtil;

import java.util.Map;

@Data
@NoArgsConstructor
public class Metric {

    /**
     * 样本数
     */
    private Integer sampleCount;

    /**
     * quantile长度的list，记录时间戳与耗时
     */
    private long[] costTimes;
    private long[] timestamp;

    /**
     * costTimes 数组的位置
     */
    private volatile long writePointer = -1;
    private volatile long readStartPointer = -1;
    private volatile long readEndPointer = -1;

    /**
     * 吞吐量
     */
    private Map<ThroughputType, Double> throughput;

    public Metric(Integer sampleCount) throws IllegalAccessException {
        if(sampleCount == null || sampleCount <= 0)
            throw new IllegalAccessException("sampleCount count must bigger than 0");

        this.sampleCount = sampleCount;
        costTimes = new long[sampleCount];
        timestamp = new long[sampleCount];
    }

    /**
     * 添加消耗时间
     * @param timestamp
     * @param cost
     * @return
     */
    public Metric addCostTime(long idx, long timestamp, long cost) {
        int arrIdx = (int) (idx % sampleCount);
        this.costTimes[arrIdx] = cost;
        this.timestamp[arrIdx] = timestamp;
        return this;
    }

    public PerformanceInfo getPerformanceInfo(){
        PerformanceInfo performanceInfo = new PerformanceInfo();
        if(readEndPointer < readStartPointer) return performanceInfo;

        long total = 0;
        long max = -1;
        long min = Long.MAX_VALUE;
        int count = 0;
        for (long i = readStartPointer; i < readEndPointer; i++) {
            int idx = (int) (i%sampleCount);
            long cost = costTimes[idx];

            max = Math.max(max, cost);
            min = Math.min(min, cost);
            total += cost;
            count++;
        }

        performanceInfo.setMaxCostTime(max);
        performanceInfo.setMinCostTime(min);
        performanceInfo.setAvgCostTime(total/count);
        performanceInfo.setThroughput(count /(double) total);

        performanceInfo.setQuantile(
                CommonUtil.quantile(costTimes,
                        0,
                        writePointer<sampleCount?(int)writePointer+1:sampleCount,
                        new double[]{0.25, 0.5, 0.75, 0.9})
        );
        return performanceInfo;
    }


    public synchronized long tryWrite() {
        //没写到读的位置，或已经读完
        if(writePointer >= readStartPointer || readEndPointer == readStartPointer) {
            long index = writePointer;
            writePointer++;
            return index;
        }
        return -1;
    }

    /**
     * 尝试读，返回-1表示不能读因为有其它在读，返回0表示不能读因为没有数据，返回1表示可读
     * 调用返回1之后必须调用readEnd，否则状态位更改会导致其它读写返回失败
     * @return
     */
    public synchronized long tryRead() {
        if(writePointer == -1) return 0;
        //没有读，且至少写完了一个
        if(readEndPointer == readStartPointer && writePointer >= 0) {
            readEndPointer = writePointer;
            readStartPointer = Math.max(0, readEndPointer - sampleCount);
            return 1;
        }
        return -1;
    }

    public synchronized void readEnd() {
        readStartPointer = readEndPointer;
    }

    /**
     * 吞吐量类型
     */
    enum ThroughputType {
        MINUTE
    }

    @Override
    public String toString() {
        return "Metric{" +
                "writePointer=" + writePointer +
                ", readStartPointer=" + readStartPointer +
                ", readEndPointer=" + readEndPointer +
                '}';
    }
}
