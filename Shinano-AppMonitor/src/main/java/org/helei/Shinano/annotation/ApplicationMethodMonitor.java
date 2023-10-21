package org.helei.Shinano.annotation;

import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ApplicationMethodMonitor {
    int sample() default 1000;
}
