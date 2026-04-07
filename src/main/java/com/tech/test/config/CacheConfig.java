package com.tech.test.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // Cache configuration will be auto-configured by Spring

    // You can customize cache providers (Redis, EhCache, etc.) if needed
}
