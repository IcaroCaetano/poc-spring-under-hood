package org.myprojecticaro.context;


import org.myprojecticaro.annotations.Autowired;
import org.myprojecticaro.annotations.Component;
import org.myprojecticaro.annotations.PostConstruct;
import org.myprojecticaro.annotations.Qualifier;
import org.myprojecticaro.events.EventPublisher;
import org.myprojecticaro.events.EventListener;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;


/**
 * {@code ApplicationContext} is a lightweight inversion-of-control container that mimics
 * the core behavior of the Spring Framework.
 * <p>
 * It supports:
 * <ul>
 *   <li>Component scanning via reflection</li>
 *   <li>Auto-configuration using a custom .factories file</li>
 *   <li>Field-based dependency injection via {@code @Autowired}</li>
 * </ul>
 * All discovered and instantiated beans are stored as singletons in a simple map.
 * </p>
 */
public class ApplicationContext {

    private final Map<Class<?>, Object> beans = new HashMap<>();

    /**
     * Initializes the application context:
     * <ol>
     *   <li>Scans the specified base package for {@code @Component}-annotated classes</li>
     *   <li>Loads additional beans via custom auto-configuration</li>
     *   <li>Injects dependencies annotated with {@code @Autowired}</li>
     * </ol>
     *
     * @param basePackage the package to scan for component classes
     * @throws RuntimeException if the initialization fails
     */
    public ApplicationContext(String basePackage) {
        try {
            scanPackage(basePackage);
            loadAutoConfigurations();
            injectDependencies();
            invokePostConstructMethods();

            EventPublisher publisher = (EventPublisher) beans.get(EventPublisher.class);
            if (publisher == null) {
                throw new RuntimeException("EventPublisher not found in context.");
            }

            beans.values().forEach(bean -> {
                if (bean instanceof EventListener<?> listener) {
                    publisher.registerListener(listener);
                    System.out.println("[EVENT] Registered listener: " + bean.getClass().getSimpleName());
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize context", e);
        }
    }

    /**
     * Scans the specified package for classes annotated with {@code @Component},
     * instantiates them via reflection, and registers them in the IoC container.
     *
     * @param basePackage the base package to scan for component classes
     * @throws Exception if any error occurs during class loading, instantiation, or scanning
     */
    private void scanPackage(String basePackage) throws Exception {
        String path = basePackage.replace(".", "/");
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);

        if (resource == null) {
            throw new RuntimeException("Package not found: " + basePackage);
        }

        File baseDir = new File(resource.toURI());
        scanDirectoryRecursive(baseDir, basePackage);
    }

    /**
     * Recursively scans the given directory for class files, loading classes annotated with
     * {@code @Component}, instantiating them, and registering them in the IoC container.
     *
     * @param directory the current directory to scan
     * @param packageName the package name corresponding to the directory
     * @throws Exception if any error occurs during class loading or instantiation
     */
    private void scanDirectoryRecursive(File directory, String packageName) throws Exception {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                scanDirectoryRecursive(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(org.myprojecticaro.annotations.Component.class)) {
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    beans.put(clazz, instance);
                    System.out.println("[SCAN] Registered: " + clazz.getSimpleName());
                }
            }
        }
    }

    /**
     * Loads bean classes from the {@code autoconfiguration.factories} file
     * and registers them in the container if they are annotated with {@code @Component}.
     *
     * @throws Exception if any class loading or instantiation fails
     */
    private void loadAutoConfigurations() throws Exception {
        URL resource = Thread.currentThread()
                .getContextClassLoader()
                .getResource("autoconfiguration.factories");

        if (resource == null) {
            System.out.println("[AUTO-CONFIG] No autoconfiguration.factories file found.");
            return;
        }

        Properties props = new Properties();
        try (InputStream input = resource.openStream()) {
            props.load(input);
        }

        String configClasses = props.getProperty("org.myprojecticaro.autoconfigure.EnableAutoConfiguration");
        if (configClasses == null) return;

        for (String className : configClasses.split(",")) {
            className = className.trim();
            if (className.isEmpty()) continue;

            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(Component.class)) {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                beans.put(clazz, instance);
                System.out.println("[AUTO-CONFIG] Registered: " + clazz.getSimpleName());
            }
        }
    }

    /**
     * Performs field-based dependency injection for all beans
     * by setting fields annotated with {@code @Autowired}.
     *
     * @throws IllegalAccessException if a field cannot be set
     */
    private void injectDependencies() throws IllegalAccessException {
        for (Object bean : beans.values()) {
            for (var field : bean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Class<?> dependencyType = field.getType();
                    Object dependency = null;

                    if (field.isAnnotationPresent(Qualifier.class)) {
                        String qualifierName = field.getAnnotation(Qualifier.class).value();
                        dependency = beans.entrySet().stream()
                                .filter(entry -> dependencyType.isAssignableFrom(entry.getKey()))
                                .filter(entry -> qualifierName.equals(getComponentName(entry.getKey())))
                                .map(Map.Entry::getValue)
                                .findFirst()
                                .orElse(null);
                    } else {
                        dependency = beans.entrySet().stream()
                                .filter(entry -> dependencyType.isAssignableFrom(entry.getKey()))
                                .map(Map.Entry::getValue)
                                .findFirst()
                                .orElse(null);
                    }

                    if (dependency != null) {
                        field.setAccessible(true);
                        field.set(bean, dependency);
                        System.out.println("[INJECT] Injected " + dependency.getClass().getSimpleName() +
                                " into " + bean.getClass().getSimpleName());
                    } else {
                        throw new RuntimeException("No bean found for type: " + dependencyType.getName());
                    }
                }
            }
        }
    }

    private void invokePostConstructMethods() throws Exception {
        for (Object bean : beans.values()) {
            for (var method : bean.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    method.setAccessible(true);
                    method.invoke(bean);
                    System.out.println("[POST-CONSTRUCT] Invoked " + method.getName() + " on " + bean.getClass().getSimpleName());
                }
            }
        }
    }

    public <T> T getBean(Class<T> clazz) {
        return clazz.cast(beans.get(clazz));
    }

    private String getComponentName(Class<?> clazz) {
        Component annotation = clazz.getAnnotation(Component.class);
        if (annotation != null) {
            return annotation.value().isEmpty()
                    ? clazz.getSimpleName().substring(0,1).toLowerCase() + clazz.getSimpleName().substring(1)
                    : annotation.value();
        }
        return null;
    }
}
