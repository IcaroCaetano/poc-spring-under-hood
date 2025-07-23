package org.myprojecticaro.component;

import org.myprojecticaro.annotations.Component;

@Component
public class SimpleFormatter {
    public String format(String message) {
        return "[Formatted] " + message;
    }
}