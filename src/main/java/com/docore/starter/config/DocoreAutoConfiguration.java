  package com.docore.starter.config;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.danielwegener.logback.kafka.KafkaAppender;
import com.github.danielwegener.logback.kafka.delivery.AsynchronousDeliveryStrategy;
import com.github.danielwegener.logback.kafka.keying.NoKeyKeyingStrategy;
import jakarta.annotation.PostConstruct;
import net.logstash.logback.encoder.LogstashEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * Auto-configuration class for DOCore log aggregation starter SDK.
 * Programmatically constructs the Logback tree:
 * Root Logger -> AsyncAppender (queue size 512) -> KafkaAppender -> LogstashEncoder.
 */
@AutoConfiguration
@EnableConfigurationProperties(DocoreProperties.class)
@ConditionalOnProperty(name = "docore.enabled", havingValue = "true", matchIfMissing = true)
public class DocoreAutoConfiguration {

    private final DocoreProperties properties;

    public DocoreAutoConfiguration(DocoreProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        // Safe check for Kafka bootstrap servers configuration
        if (!StringUtils.hasText(properties.getKafkaServers())) {
            LoggerFactory.getLogger(DocoreAutoConfiguration.class)
                    .warn("DOCore Kafka bootstrap servers ('docore.kafka-servers') are not configured. Log aggregation will not be attached.");
            return;
        }

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        // 1. LogstashEncoder configuration with custom fields (serviceName & environment)
        LogstashEncoder encoder = new LogstashEncoder();
        encoder.setContext(context);

        String serviceName = StringUtils.hasText(properties.getServiceName()) ? properties.getServiceName() : "unknown-service";
        String environment = StringUtils.hasText(properties.getEnvironment()) ? properties.getEnvironment() : "unknown-env";
        String customFields = String.format("{\"serviceName\":\"%s\",\"environment\":\"%s\"}", serviceName, environment);
        encoder.setCustomFields(customFields);
        encoder.start();

        // 2. KafkaAppender configuration
        KafkaAppender<ILoggingEvent> kafkaAppender = new KafkaAppender<>();
        kafkaAppender.setContext(context);
        kafkaAppender.setName("DOCoreKafkaAppender");
        kafkaAppender.setEncoder(encoder);
        kafkaAppender.setTopic(StringUtils.hasText(properties.getTopic()) ? properties.getTopic() : "docore.logs.raw");
        kafkaAppender.setKeyingStrategy(new NoKeyKeyingStrategy());
        kafkaAppender.setDeliveryStrategy(new AsynchronousDeliveryStrategy());
        kafkaAppender.addProducerConfig("bootstrap.servers=" + properties.getKafkaServers());
        kafkaAppender.start();

        // 3. AsyncAppender configuration (Queue size 512, non-blocking)
        AsyncAppender asyncAppender = new AsyncAppender();
        asyncAppender.setContext(context);
        asyncAppender.setName("DOCoreAsyncKafkaAppender");
        asyncAppender.setQueueSize(512);
        asyncAppender.setNeverBlock(true);
        asyncAppender.addAppender(kafkaAppender);
        asyncAppender.start();

        // 4. Attach to Root Logger
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(asyncAppender);
    }
}
