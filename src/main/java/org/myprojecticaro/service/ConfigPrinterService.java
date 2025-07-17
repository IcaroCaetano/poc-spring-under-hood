package org.myprojecticaro.service;

import org.myprojecticaro.annotations.Component;
import org.myprojecticaro.annotations.PostConstruct;
import org.myprojecticaro.annotations.Value;

@Component
public class ConfigPrinterService {

    @Value("app.name")
    private String appName;

    @Value("app.author")
    private String author;

    @PostConstruct
    public void init() {
        System.out.println("[CONFIG] App Name: " + appName);
        System.out.println("[CONFIG] Author: " + author);
    }
}