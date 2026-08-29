package com.leaseapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LeaseApplicationTests {

    @Test
    void contextLoads() {
        // Verifies the Spring context wires up correctly (entities, repositories,
        // services, controllers) against an in-memory H2 database.
    }
}
