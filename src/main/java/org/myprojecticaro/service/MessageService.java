package org.myprojecticaro.service;


import org.myprojecticaro.annotations.Component;

/**
 * {@code MessageService} is a simple component that prints a message.
 * <p>
 * This class simulates a basic service bean managed by a custom IoC container.
 * It's used to demonstrate how a component can be discovered, instantiated,
 * and used independently or injected into another bean.
 * </p>
 */
@Component
public class MessageService {

    /**
     * Prints a greeting message to the console.
     */
    public void hello() {
        System.out.println("Hello from MessageService!");
    }
}