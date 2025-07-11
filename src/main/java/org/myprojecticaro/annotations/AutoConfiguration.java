package org.myprojecticaro.annotations;

import java.lang.annotation.*;

/**
 * Indicates that a class is eligible for auto-configuration.
 * <p>
 * When declared in the {@code autoconfiguration.factories} file under the key
 * {@code org.myprojecticaro.autoconfigure.EnableAutoConfiguration}, classes
 * annotated with {@code @AutoConfiguration} and {@code @Component} will be
 * automatically instantiated and registered by the container.
 * </p>
 *
 * @see org.myprojecticaro.context.ApplicationContext#loadAutoConfigurations()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoConfiguration { }