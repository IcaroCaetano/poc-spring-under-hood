package org.myprojecticaro.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field to be automatically injected by the IoC container.
 * <p>
 * When a field is annotated with {@code @Autowired}, the {@code ApplicationContext}
 * will attempt to find a matching bean by type and inject it using reflection.
 * </p>
 *
 * @see org.myprojecticaro.context.ApplicationContext#injectDependencies()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Autowired { }