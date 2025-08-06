package org.myprojecticaro.component;

import org.myprojecticaro.annotations.Component;
import org.myprojecticaro.annotations.PostConstruct;
import org.myprojecticaro.annotations.PreDestroy;

@Component
public class InitializationService {

    @PostConstruct
    public void init() {
        System.out.println("[INIT] InitializationService is ready!");
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("[PRE-DESTROY] InitializationService is shutting down!");
    }
}