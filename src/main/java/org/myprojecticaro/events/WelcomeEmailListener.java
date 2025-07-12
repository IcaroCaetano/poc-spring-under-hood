package org.myprojecticaro.events;

import org.myprojecticaro.annotations.Component;

@Component
public class WelcomeEmailListener implements EventListener<UserRegisteredEvent> {
    @Override
    public void onEvent(UserRegisteredEvent event) {
        System.out.println("[EVENT] Sending welcome email to: " + event.getUsername());
    }
}
