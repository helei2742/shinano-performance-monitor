package org.helei.Shinano.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommonUtil {

    public static Map<Double, Double> quantile(long[]data, int start, int end, double[] quantiles){
        long[] arr = new long[end-start];
        System.arraycopy(data,start,arr, 0, end-start);

        Arrays.sort(arr);
        HashMap<Double, Double> res = new HashMap<>();
        for (double quantile : quantiles) {
            res.put(quantile, quantile(arr, quantile));
        }
        return res;
    }

    public static double quantile(long[] data, double p) {
        int n = data.length;
        double index = p * (n - 1);
        int lower = (int) Math.floor(index);
        int upper = (int) Math.ceil(index);

        if (lower == upper) {
            return data[lower];
        }

        double weight = index - lower;
        return (double)data[lower] * (1 - weight) + (double)data[upper] * weight;
    }

}
