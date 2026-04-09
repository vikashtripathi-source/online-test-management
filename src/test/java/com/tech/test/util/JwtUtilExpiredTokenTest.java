package com.tech.test.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtUtilExpiredTokenTest {

    @InjectMocks private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKeyForTestingPurposesOnly");
        ReflectionTestUtils.setField(
                jwtUtil, "expiration", 1000L); // 1 second expiration for testing
    }

    @Test
    void testExtractUsername_WithExpiredToken_ShouldReturnNull() {
        // Generate a token that will expire immediately
        String expiredToken = jwtUtil.generateToken("test@example.com", 1L);

        // Wait for token to expire
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Should return null instead of throwing ExpiredJwtException
        String username = jwtUtil.extractUsername(expiredToken);
        assertNull(username, "Expired token should return null username");
    }

    @Test
    void testValidateToken_WithExpiredToken_ShouldReturnFalse() {
        // Generate a token that will expire immediately
        String expiredToken = jwtUtil.generateToken("test@example.com", 1L);

        // Wait for token to expire
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Should return false for expired token
        Boolean isValid = jwtUtil.validateToken(expiredToken);
        assertFalse(isValid, "Expired token should be invalid");
    }

    @Test
    void testExtractUserId_WithExpiredToken_ShouldStillWork() {
        // Generate a token that will expire immediately
        Long expectedUserId = 123L;
        String expiredToken = jwtUtil.generateToken("test@example.com", expectedUserId);

        // Wait for token to expire
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Should still extract userId even from expired token
        Long userId = jwtUtil.extractUserId(expiredToken);
        assertEquals(expectedUserId, userId, "Should extract userId even from expired token");
    }
}
