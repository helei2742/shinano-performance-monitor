package org.helei.Shinano.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class ShinanoMethodConfig {

    @Value("${shinano.methodMetric.threadPoolCore}")
    private Integer threadPoolCore;

    @Value("${shinano.methodMetric.threadPoolMax}")
    private Integer threadPoolMax;

    @Value("${shinano.methodMetric.waitTime}")
    private Integer waitTime;
}
