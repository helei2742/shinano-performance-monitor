package org.helei.Shinano.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.helei.Shinano.service.MethodMetricService;
import org.helei.Shinano.annotation.ApplicationMethodMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Aspect
@Component
public class MonitorAspect {

    @Autowired
    private MethodMetricService methodMetricService;

    @Pointcut("@annotation(org.helei.Shinano.annotation.ApplicationMethodMonitor)")
    private void applicationMethodMonitorPointcut() {
        // 切点表达式定义方法，方法修饰符可以是private或public
    }


    @Around("applicationMethodMonitorPointcut()")
    public Object aroundApplicationMethodMonitor(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.toString();
        //获取注解里的quantile分位
        Method method = signature.getMethod();
        ApplicationMethodMonitor methodMonitor = method.getAnnotation(ApplicationMethodMonitor.class);
        int sample = methodMonitor.sample();

        //执行方法
        Object proceed = joinPoint.proceed();

        long end = System.currentTimeMillis();
        methodMetricService.addCostTime(methodName, sample, end, end - startTime);
        return proceed;
    }
}
