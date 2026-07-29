# DOCore Integration Contract
This SDK acts as a producer for the main DOCore infrastructure. The agents do not have access to the DOCore server codebase, so they must strictly adhere to this contract.

## 1. Transport Layer
*   **Protocol:** Kafka
*   **Default Topic:** `docore.logs.raw` (Must be overridable via application.yml)

## 2. Payload Schema
All logs must be serialized as flat JSON by the `LogstashEncoder` before being pushed to Kafka. The main DOCore server expects the following fields:
*   `@timestamp`: Standard ISO-8601 timestamp.
*   `level`: The log level (INFO, ERROR, WARN, DEBUG).
*   `service`: The name of the microservice producing the log (injected dynamically from `docore.service-name`).
*   `thread`: The thread name executing the code.
*   `logger`: The Java class/logger name.
*   `message`: The actual log message.