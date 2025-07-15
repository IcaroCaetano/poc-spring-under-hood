package org.myprojecticaro.service;

import org.myprojecticaro.annotations.Component;

@Component("sms")
public class SmsMessageService implements MessageSender {
    @Override
    public void send(String message) {
        System.out.println("[QUALIFIER] Sending SMS: " + message);
    }
}