# 🧪 POC-Spring-Under-Hood

This project is a Proof of Concept (POC) that recreates some of the core features of the **Spring Framework** and **Spring Boot**, in a simplified form.  
It aims to provide a **deep dive** into how Spring works under the hood.

---

## 🎯 Purpose

To demonstrate the internal mechanics of:

- Inversion of Control (IoC)
- Dependency Injection using `@Autowired`
- Component scanning (`@ComponentScan`)
- Bean registration via `@Component`
- AutoConfiguration similar to Spring Boot's `@EnableAutoConfiguration`
- Configuration loading from `.factories` files
- Manual context lifecycle and initialization

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── org/
│   │       └── myprojecticaro/
│   │           ├── annotations/
│   │           │   ├── Autowired.java
│   │           │   ├── Component.java
│   │           │   └── AutoConfiguration.java
│   │           ├── context/
│   │           │   └── ApplicationContext.java
│   │           ├── service/
│   │           │   ├── MessageService.java
│   │           │   └── GreetingService.java
│   │           └── Application.java
│   └── resources/
│       └── autoconfiguration.factories
```

---

## 🛠️ Implemented Features

| Feature                           | Description                                                            |
|----------------------------------|------------------------------------------------------------------------|
| IoC Container                    | Custom `ApplicationContext` implementation                            |
| Custom Annotations               | `@Component`, `@Autowired`, `@AutoConfiguration`                       |
| Dependency Injection             | Field-based injection using reflection and `@Autowired`               |
| Component Scanning               | Scans classes with `.class` extension for `@Component` annotation     |
| AutoConfiguration                | Based on `.factories` file and `@Component`-annotated classes         |
| Bean Registry                    | Beans stored in a `Map<Class<?>, Object>`                             |
| Manual Context Initialization    | Custom entry point via `Application.java`                             |

---

## 🔍 How It Works

### 1. `ApplicationContext`

When initialized with a base package:

- Scans `.class` files in that package
- Looks for classes annotated with `@Component`
- Instantiates and registers them using reflection
- Loads classes from `autoconfiguration.factories`
- Injects all fields annotated with `@Autowired`

---

### 2. AutoConfiguration

You can register components automatically via:

```
src/main/resources/autoconfiguration.factories
```

Example:
```properties
org.myprojecticaro.autoconfigure.EnableAutoConfiguration=\
org.myprojecticaro.service.MessageService
```

---

## ▶️ How to Run

1. Compile and run `Application.java`:

```bash
./gradlew run  # or run from your IDE
```

2. Expected output:

```
[SCAN] Registered: GreetingService
[AUTO-CONFIG] Registered: MessageService
[INJECT] Injected MessageService into GreetingService
Hello from MessageService!
```

---

## ✨ Examples

### MessageService.java

```java
@Component
@AutoConfiguration
public class MessageService {
    public void hello() {
        System.out.println("Hello from MessageService!");
    }
}
```

### GreetingService.java

```java
@Component
public class GreetingService {
    @Autowired
    private MessageService messageService;

    public void greet() {
        messageService.hello();
    }
}
```

---

## 🚧 Future Improvements

| Feature                   | Status    |
|---------------------------|-----------|
| `@Value` and config files | ⏳ Planned |
| `@PostConstruct` support  | ⏳ Planned |
| Embedded HTTP server      | ❌ Not yet |
| Bean scopes (singleton)   | ❌ Not yet |
| Event system              | ❌ Not yet |

---

## 👨‍💻 Author

**Ícaro Caetano**  
Learning the internals of Spring by building it from scratch.  
Contact: [LinkedIn](https://www.linkedin.com/in/icarocaetano)

---

## 📜 License

This project is free to use for educational and learning purposes.