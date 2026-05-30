package com.banco.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.banco.service", "com.banco.config"})
public class SpringRootConfig {
}
