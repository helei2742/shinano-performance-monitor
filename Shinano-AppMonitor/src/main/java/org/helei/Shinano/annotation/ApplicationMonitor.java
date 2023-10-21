package org.helei.Shinano.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Inherited
@Component
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ApplicationMonitor {
}
