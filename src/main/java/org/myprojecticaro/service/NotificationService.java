package org.myprojecticaro.service;

import org.myprojecticaro.annotations.Autowired;
import org.myprojecticaro.annotations.Component;
import org.myprojecticaro.annotations.Qualifier;

@Component
public class NotificationService {

    @Autowired
    @Qualifier("sms")
    private MessageSender messageSender;

    public void notifyUser(String text) {
        messageSender.send(text);
    }
}