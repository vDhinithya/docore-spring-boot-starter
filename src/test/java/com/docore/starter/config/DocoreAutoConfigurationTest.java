package com.docore.starter.config;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import com.github.danielwegener.logback.kafka.KafkaAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

class DocoreAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DocoreAutoConfiguration.class));

    @Test
    void testAutoConfigurationLoadsAndConfiguresLogbackTree() {
        contextRunner
                .withPropertyValues(
                        "docore.enabled=true",
                        "docore.service-name=test-service",
                        "docore.kafka-servers=localhost:9092",
                        "docore.topic=docore.logs.test",
                        "docore.environment=test"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(DocoreAutoConfiguration.class);
                    assertThat(context).hasSingleBean(DocoreProperties.class);

                    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                    Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);

                    Iterator<Appender<ch.qos.logback.classic.spi.ILoggingEvent>> appenderIterator = rootLogger.iteratorForAppenders();
                    boolean foundAsyncKafka = false;
                    while (appenderIterator.hasNext()) {
                        Appender<ch.qos.logback.classic.spi.ILoggingEvent> appender = appenderIterator.next();
                        if ("DOCoreAsyncKafkaAppender".equals(appender.getName()) && appender instanceof AsyncAppender asyncAppender) {
                            assertThat(asyncAppender.getQueueSize()).isEqualTo(512);

                            Iterator<Appender<ch.qos.logback.classic.spi.ILoggingEvent>> innerIterator = asyncAppender.iteratorForAppenders();
                            while (innerIterator.hasNext()) {
                                Appender<ch.qos.logback.classic.spi.ILoggingEvent> innerAppender = innerIterator.next();
                                if ("DOCoreKafkaAppender".equals(innerAppender.getName()) && innerAppender instanceof KafkaAppender) {
                                    foundAsyncKafka = true;
                                }
                            }
                        }
                    }

                    assertThat(foundAsyncKafka).isTrue();
                });
    }

    @Test
    void testAutoConfigurationDisabledWhenPropertyIsFalse() {
        contextRunner
                .withPropertyValues("docore.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(DocoreAutoConfiguration.class);
                });
    }

    @Test
    void testAutoConfigurationHandlesMissingKafkaServersSafely() {
        contextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(DocoreAutoConfiguration.class);
                    // Should complete without throwing exceptions when kafka-servers is omitted
                });
    }
}
