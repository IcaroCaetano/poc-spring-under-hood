package org.myprojecticaro.context;


import org.myprojecticaro.annotations.Autowired;
import org.myprojecticaro.annotations.Component;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class ApplicationContext {
    private final Map<Class<?>, Object> beans = new HashMap<>();

    public ApplicationContext(String basePackage) {
        try {
            scanPackage(basePackage);
            loadAutoConfigurations();
            injectDependencies();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize context", e);
        }
    }

    private void scanPackage(String basePackage) throws Exception {
        String path = basePackage.replace(".", "/");
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);

        if (resource == null) {
            throw new RuntimeException("Package not found: " + basePackage);
        }

        File directory = new File(resource.toURI());
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().endsWith(".class")) {
                String className = basePackage + "." + file.getName().replace(".class", "");
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Component.class)) {
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    beans.put(clazz, instance);
                    System.out.println("[SCAN] Registered: " + clazz.getSimpleName());
                }
            }
        }
    }


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

    private void injectDependencies() throws IllegalAccessException {
        for (Object bean : beans.values()) {
            for (var field : bean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Class<?> dependencyType = field.getType();
                    Object dependency = beans.get(dependencyType);
                    if (dependency != null) {
                        field.setAccessible(true);
                        field.set(bean, dependency);
                        System.out.println("[INJECT] Injected " + dependencyType.getSimpleName() + " into " + bean.getClass().getSimpleName());
                    } else {
                        throw new RuntimeException("No bean found for type: " + dependencyType.getName());
                    }
                }
            }
        }
    }

    public <T> T getBean(Class<T> clazz) {
        return clazz.cast(beans.get(clazz));
    }
}
