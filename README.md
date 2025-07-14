# 🧪 POC-Spring-Under-Hood

This project is a **Proof of Concept** that reimplements the core functionality of the **Spring Framework** and **Spring Boot**, focusing on internal mechanisms like IoC, dependency injection, component scanning, auto-configuration, and event publishing.

It provides a step-by-step way to understand **how Spring works behind the scenes** — by rebuilding it from scratch.

---

## 🎯 Purpose

To simulate and explore how the Spring ecosystem works by implementing:

- ✅ Inversion of Control (IoC)
- ✅ Dependency Injection using `@Autowired`
- ✅ Component Scanning via `@Component`
- ✅ AutoConfiguration via `.factories` file
- ✅ Event Publishing system (`EventPublisher` and `EventListener`)
- ✅ Reflection-based bean management
- ✅ Manual bean lifecycle control
- ✅ Lifecycle hooks via `@PostConstruct`

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── org/myprojecticaro/
│   │       ├── annotations/
│   │       │   ├── Autowired.java
│   │       │   ├── Component.java
│   │       │   └── AutoConfiguration.java
│   │       ├── context/
│   │       │   └── ApplicationContext.java
│   │       ├── events/
│   │       │   ├── EventPublisher.java
│   │       │   ├── EventListener.java
│   │       │   ├── UserRegisteredEvent.java
│   │       │   └── WelcomeEmailListener.java
│   │       ├── lifecycle/
│   │       │   └── InitializationService.java
│   │       ├── service/
│   │       │   ├── MessageService.java
│   │       │   ├── GreetingService.java
│   │       │   └── RegistrationService.java
│   │       └── Application.java
│   └── resources/
│       └── autoconfiguration.factories
```

---

## 🛠️ Implemented Features

| Feature                         | Description                                                           |
|--------------------------------|------------------------------------------------------------------------|
| IoC Container                  | Custom `ApplicationContext` that manages bean lifecycle                |
| Dependency Injection           | Injects fields annotated with `@Autowired` using reflection            |
| Component Scanning             | Detects and registers `@Component` classes                             |
| AutoConfiguration              | Registers beans via `.factories` config + `@Component` classes         |
| Event System                   | Publishes and listens to events using `EventPublisher`/`EventListener` |
| Annotation-based Metadata      | Implements `@Component`, `@Autowired`, and `@AutoConfiguration`        |
| Lifecycle Hook                   | `@PostConstruct` support for bean initialization logic               |

---

## 🔍 How It Works

### `ApplicationContext.java`

This is the core of the custom framework. It:

1. **Scans the base package** for `@Component` classes.
2. **Loads auto-configured classes** from `autoconfiguration.factories`.
3. **Instantiates beans using reflection**.
4. **Performs field-level injection** via `@Autowired`.
5. **Registers event listeners** and supports event publishing.
6. **Calls methods annotated with `@PostConstruct`**

---

### AutoConfiguration

You can auto-load beans using this file:

```properties
# src/main/resources/autoconfiguration.factories
org.myprojecticaro.autoconfigure.EnableAutoConfiguration=\
org.myprojecticaro.service.MessageService
```

---

### Event System

- **Publishers** fire events (e.g., `UserRegisteredEvent`)
- **Listeners** receive and process the events

Example:
```java
eventPublisher.publish(new UserRegisteredEvent("icaro.dev"));
```

---

## ▶️ How to Run

1. Run the main class:

```bash
./gradlew run
```

2. Example output:

```
[SCAN] Registered: EventPublisher
[SCAN] Registered: WelcomeEmailListener
[SCAN] Registered: GreetingService
[SCAN] Registered: MessageService
[SCAN] Registered: RegistrationService
[AUTO-CONFIG] Registered: MessageService
[INJECT] Injected MessageService into GreetingService
[INJECT] Injected EventPublisher into RegistrationService
[INJECT] Injected ...
[POST-CONSTRUCT] Called init on InitializationService
[INIT] InitializationService is ready!
[EVENT] Registered listener: WelcomeEmailListener
Hello from MessageService!
[REGISTER] User created: icaro.dev
[EVENT] Sending welcome email to: icaro.dev
```

---

## ✨ Example: Custom Services

### `MessageService.java`

```java
@Component
@AutoConfiguration
public class MessageService {
    public void hello() {
        System.out.println("Hello from MessageService!");
    }
}
```

### `RegistrationService.java`

```java
@Component
public class RegistrationService {
    @Autowired
    private EventPublisher eventPublisher;

    public void register(String username) {
        System.out.println("[REGISTER] User created: " + username);
        eventPublisher.publish(new UserRegisteredEvent(username));
    }
}
```

### `WelcomeEmailListener.java`

```java
@Component
public class WelcomeEmailListener implements EventListener<UserRegisteredEvent> {
    @Override
    public void onEvent(UserRegisteredEvent event) {
        System.out.println("[EVENT] Sending welcome email to: " + event.getUsername());
    }
}
```

---

## 🚧 Roadmap & Improvements

| Feature                   | Status    |
|---------------------------|-----------|
| Config properties / `@Value` | ⏳ Planned |
| Bean lifecycle callbacks  | ⏳ Planned |
| Prototype scope support   | ❌ Not yet |
| Embedded HTTP server      | ❌ Not yet |

---

## 👨‍💻 Author

**Ícaro Caetano**  
Exploring Spring’s internals through hands-on implementation.  
📫 [LinkedIn →](https://www.linkedin.com/in/icarocaetano)

---

## 📜 License

This project is open-source and intended for learning and educational purposes.