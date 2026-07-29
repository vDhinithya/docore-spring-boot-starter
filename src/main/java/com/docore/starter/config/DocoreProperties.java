package com.docore.starter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the DOCore Spring Boot Starter SDK.
 * Binds properties prefixed with 'docore'.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "docore")
public class DocoreProperties {

    /**
     * Whether DOCore log aggregation is enabled.
     */
    private boolean enabled = true;

    /**
     * Name of the microservice producing logs.
     */
    private String serviceName;

    /**
     * Comma-separated list of Kafka bootstrap servers.
     */
    private String kafkaServers;

    /**
     * Target Kafka topic for raw logs.
     */
    private String topic = "docore.logs.raw";

    /**
     * Environment name (e.g. dev, staging, prod).
     */
    private String environment;
}
