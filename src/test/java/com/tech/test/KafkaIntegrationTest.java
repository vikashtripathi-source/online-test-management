package com.tech.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class KafkaIntegrationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring context loads successfully
        // including Kafka configuration and beans
    }
}
