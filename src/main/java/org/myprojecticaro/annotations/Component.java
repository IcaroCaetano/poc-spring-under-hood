package org.myprojecticaro.annotations;

import java.lang.annotation.*;

/**
 * Indicates that an annotated class is a "component" and should be registered
 * by the {@link org.myprojecticaro.context.ApplicationContext} as a managed bean.
 * <p>
 * This annotation is used during the component scan phase to discover classes
 * that should be automatically instantiated and managed in the IoC container.
 * </p>
 *
 * @see org.myprojecticaro.context.ApplicationContext
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component { String value() default ""; }
