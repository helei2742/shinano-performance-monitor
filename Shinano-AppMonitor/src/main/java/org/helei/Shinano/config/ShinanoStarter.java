package org.helei.Shinano.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(value = {"org.helei.Shinano.aspect",
        "org.helei.Shinano.service","org.helei.Shinano.config"})
@EnableAspectJAutoProxy
public class ShinanoStarter {


}
