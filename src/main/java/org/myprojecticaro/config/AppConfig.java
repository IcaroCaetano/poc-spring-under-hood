package org.myprojecticaro.config;

import org.myprojecticaro.annotations.Bean;
import org.myprojecticaro.component.SimpleFormatter;


public class AppConfig {

    @Bean
    public SimpleFormatter simpleFormatter() {
        System.out.println("[BEAN] Registered: " + SimpleFormatter.class.getSimpleName());
        return new SimpleFormatter();
    }
}