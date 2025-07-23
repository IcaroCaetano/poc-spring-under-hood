package org.myprojecticaro.component;

import org.myprojecticaro.annotations.Autowired;
import org.myprojecticaro.annotations.Component;

@Component
public class AppLoggerService {

    @Autowired
    private SimpleFormatter formatter;

    public void log(String message) {
        System.out.println("[LOG] " + formatter.format(message));
    }
}