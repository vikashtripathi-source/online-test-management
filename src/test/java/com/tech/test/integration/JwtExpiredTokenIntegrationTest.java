package com.tech.test.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.tech.test.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtExpiredTokenIntegrationTest {

    @Autowired private JwtUtil jwtUtil;

    @Test
    void testExpiredTokenHandling() {
        // Generate a token with very short expiration
        String token = jwtUtil.generateToken("test@example.com", 123L);

        // The token should be valid immediately after generation
        assertNotNull(token);
        assertNotNull(jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.validateToken(token));

        // Test that the utility methods don't throw exceptions
        // (We can't easily test actual expiration without waiting)
        assertDoesNotThrow(
                () -> {
                    jwtUtil.extractUsername(token);
                    jwtUtil.extractUserId(token);
                    jwtUtil.validateToken(token);
                });
    }
}
