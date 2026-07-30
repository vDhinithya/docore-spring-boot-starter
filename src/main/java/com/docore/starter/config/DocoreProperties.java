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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getKafkaServers() {
        return kafkaServers;
    }

    public void setKafkaServers(String kafkaServers) {
        this.kafkaServers = kafkaServers;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
