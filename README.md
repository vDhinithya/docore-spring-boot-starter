# DOCore Spring Boot Starter SDK

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-Supported-black?logo=apachekafka)
![Version](https://img.shields.io/badge/version-1.0.0--SNAPSHOT-blue.svg)

---

## 📖 Overview

**DOCore Spring Boot Starter SDK** is a client-side ingestion engine designed for distributed microservices architectures. It seamlessly intercepts standard Logback events from your Spring Boot microservices, formats them into structured JSON, and asynchronously streams them to an Apache Kafka broker for centralized log aggregation within DOCore.

By leveraging Spring Boot auto-configuration, developers can centralize telemetry and logging data across microservices without writing custom logging infrastructure or modifying application code.

---

## 🏗️ Architecture & Pipeline

The SDK programmatically constructs and attaches a high-throughput, non-blocking telemetry pipeline to your application's Logback logging tree at startup:

```
Root Logger ──► AsyncAppender (queueSize=512, neverBlock=true) ──► KafkaAppender ──► LogstashEncoder
```

### 🛡️ Fail-Safe Mechanism & Resiliency
* **Non-Blocking I/O**: Telemetry processing runs asynchronously off the main application execution threads.
* **Fail-Safe Operation**: `AsyncAppender` is configured with `neverBlock=true` and a queue capacity of `512`. If the Kafka broker becomes offline, unreachable, or experiences latency spikes, log events exceeding queue capacity are safely dropped rather than blocking application threads or bringing down microservices. Core application functionality remains completely resilient.

---

## ✨ Key Features

* **Zero-Friction Setup**: Drop-in Spring Boot Starter that auto-configures automatically upon inclusion in the classpath with zero custom logging logic required.
* **Resiliency & Non-blocking I/O**: Asynchronous Logback execution pipeline engineered to prevent thread blocking and application downtime.
* **Structured JSON Formatting**: Converts log events to Logstash-compliant JSON structures, automatically injecting `serviceName` and `environment` metadata fields.

---

## 🚀 Getting Started (Installation)

Add the following Maven dependency to your Spring Boot microservice's `pom.xml`:

```xml
<dependency>
    <groupId>com.docore</groupId>
    <artifactId>docore-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

---

## ⚙️ Configuration

Configure the SDK properties in your application's `application.yml` (or `application.properties`):

### Sample `application.yml`

```yaml
docore:
  enabled: true
  service-name: order-service
  environment: production
  kafka-servers: localhost:9092
  topic: docore.logs.raw
```

### Configuration Properties

Properties are mapped to [`DocoreProperties.java`](src/main/java/com/docore/starter/config/DocoreProperties.java) under the `docore` prefix:

| Property | Type | Default Value | Description |
| :--- | :--- | :--- | :--- |
| `docore.enabled` | `boolean` | `true` | Enables or disables the DOCore log aggregation SDK. |
| `docore.service-name` | `String` | *(None)* | Name of the microservice producing the logs (automatically injected as `serviceName` in JSON logs). |
| `docore.kafka-servers` | `String` | *(None)* | Comma-separated list of Kafka bootstrap servers (e.g., `localhost:9092`). |
| `docore.topic` | `String` | `docore.logs.raw` | Target Kafka topic where raw JSON log payloads are published. |
| `docore.environment` | `String` | *(None)* | Deployment environment (e.g., `dev`, `staging`, `prod`; injected as `environment` in JSON logs). |
