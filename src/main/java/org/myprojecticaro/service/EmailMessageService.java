package org.myprojecticaro.service;

import org.myprojecticaro.annotations.Component;

@Component("email")
public class EmailMessageService implements MessageSender {
    @Override
    public void send(String message) {
        System.out.println("Sending EMAIL: " + message);
    }
}