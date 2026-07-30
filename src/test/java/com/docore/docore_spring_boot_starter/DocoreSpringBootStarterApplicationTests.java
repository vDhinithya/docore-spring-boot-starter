package com.docore.docore_spring_boot_starter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = DocoreSpringBootStarterApplicationTests.TestApplication.class)
class DocoreSpringBootStarterApplicationTests {

    @SpringBootApplication
    static class TestApplication {
    }

    @Test
    void contextLoads() {
    }

}
