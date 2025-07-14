package org.myprojecticaro.lifecycle;

import org.myprojecticaro.annotations.Component;
import org.myprojecticaro.annotations.PostConstruct;

@Component
public class InitializationService {

    @PostConstruct
    public void init() {
        System.out.println("[INIT] InitializationService is ready!");
    }
}