package org.myprojecticaro.service;

import org.myprojecticaro.annotations.Autowired;
import org.myprojecticaro.annotations.Component;

/**
 * {@code GreetingService} depends on {@link MessageService}
 * to perform its operations.
 * <p>
 * This class demonstrates constructor-free dependency injection using a
 * custom {@code @Autowired} annotation and reflection-based field injection.
 * </p>
 */
@Component
public class GreetingService {
    @Autowired
    private MessageService messageService;

    /**
     * Calls the {@link MessageService#hello()} method
     * to output a greeting.
     */
    public void greet() {
        messageService.hello();
    }
}