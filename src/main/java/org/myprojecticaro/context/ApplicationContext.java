package org.myprojecticaro.context;


import org.myprojecticaro.annotations.*;
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
    private final Properties properties = new Properties();

    private final Map<Class<?>, Object> singletonBeans = new HashMap<>();
    private final Set<Class<?>> prototypeBeans = new HashSet<>();

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
            beans.putAll(singletonBeans);
            loadProperties();
            injectDependencies();
            invokePostConstructMethods();

            EventPublisher publisher = (EventPublisher) singletonBeans.get(EventPublisher.class);
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
                if (clazz.isAnnotationPresent(Component.class)) {
                    Scope scope = clazz.getAnnotation(Scope.class);
                    String scopeValue = scope != null ? scope.value() : "singleton";

                    if ("prototype".equalsIgnoreCase(scopeValue)) {
                        prototypeBeans.add(clazz);
                        System.out.println("[SCAN] Registered prototype: " + clazz.getSimpleName());
                    } else {
                        Object instance = clazz.getDeclaredConstructor().newInstance();
                        singletonBeans.put(clazz, instance);
                        System.out.println("[SCAN] Registered singleton: " + clazz.getSimpleName());
                    }
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
        for (Object bean : singletonBeans.values()) {
            for (var field : bean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Value.class)) {
                    String key = field.getAnnotation(Value.class).value();
                    String value = properties.getProperty(key);
                    System.out.println("[DEBUG] Property key: " + key + " → value: " + value);
                    if (value != null) {
                        field.setAccessible(true);
                        Object casted = castValue(field.getType(), value);
                        field.set(bean, casted);
                        System.out.println("[VALUE] Injected property " + key + "=" + value);
                    } else {
                        throw new RuntimeException("Missing property: " + key);
                    }
                }

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
        for (Object bean : singletonBeans.values()) {
            for (var method : bean.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    method.setAccessible(true);
                    method.invoke(bean);
                    System.out.println("[POST-CONSTRUCT] Invoked " + method.getName() + " on " + bean.getClass().getSimpleName());
                }
            }
        }
    }

    private void loadProperties() {
        try (InputStream input = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
                System.out.println("[PROPERTIES] Loaded application.properties");
            }
        } catch (Exception e) {
            System.out.println("[PROPERTIES] Could not load application.properties");
        }
    }

    public <T> T getBean(Class<T> clazz) {
        if (prototypeBeans.contains(clazz)) {
            try {
                T instance = clazz.getDeclaredConstructor().newInstance();
                injectInto(instance);
                postConstruct(instance);
                return instance;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create prototype bean: " + clazz, e);
            }
        }
        return clazz.cast(singletonBeans.get(clazz));
    }

    public void registerBean(Class<?> type, Object instance) {
        beans.put(type, instance);
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

    private Object castValue(Class<?> type, String value) {
        if (type == String.class) return value;
        if (type == int.class || type == Integer.class) return Integer.parseInt(value);
        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(value);
        if (type == double.class || type == Double.class) return Double.parseDouble(value);
        throw new IllegalArgumentException("Unsupported type for @Value: " + type.getName());
    }

    private void injectInto(Object instance) throws IllegalAccessException {
        for (var field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> dependencyType = field.getType();
                Object dependency = singletonBeans.get(dependencyType);

                if (dependency != null) {
                    field.setAccessible(true);
                    field.set(instance, dependency);
                    System.out.println("[INJECT] Injected " + dependencyType.getSimpleName() + " into " + instance.getClass().getSimpleName());
                } else {
                    throw new RuntimeException("No bean found for type: " + dependencyType.getName());
                }
            }
        }
    }

    private void postConstruct(Object instance) {
        for (var method : instance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                try {
                    method.setAccessible(true);
                    method.invoke(instance);
                    System.out.println("[POST-CONSTRUCT] Invoked " + method.getName() + " on " + instance.getClass().getSimpleName());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to invoke @PostConstruct on " + instance.getClass(), e);
                }
            }
        }
    }
}
