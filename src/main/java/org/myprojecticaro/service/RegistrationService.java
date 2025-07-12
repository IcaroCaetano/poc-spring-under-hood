package org.myprojecticaro.service;

import org.myprojecticaro.annotations.Autowired;
import org.myprojecticaro.annotations.Component;
import org.myprojecticaro.events.EventPublisher;
import org.myprojecticaro.events.UserRegisteredEvent;

@Component
public class RegistrationService {

    @Autowired
    private EventPublisher eventPublisher;

    public void register(String username) {
        System.out.println("[REGISTER] User created: " + username);
        eventPublisher.publish(new UserRegisteredEvent(username));
    }
}
