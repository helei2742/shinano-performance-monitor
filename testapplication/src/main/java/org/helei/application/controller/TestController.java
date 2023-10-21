package org.helei.application.controller;

import com.alibaba.fastjson.JSON;
import org.helei.Shinano.service.MetricService;
import org.helei.application.service.RunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private RunService runService;

    @Autowired
    private MetricService metricService;

    @RequestMapping("/test")
    public String test(){
        return runService.run();
    }

    @RequestMapping("/test2")
    public String test2() {
        return JSON.toJSONString(metricService.getPerformanceInfo());
    }
}
