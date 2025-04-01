package com.xperia.xpense_tracker.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
import static com.xperia.xpense_tracker.cache.CacheNames.METRICS_CACHE_NAME;

@Configuration
@EnableCaching
public class CaffeineCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        //If you want to add more caches, implement the cache in CacheNames.java and include in the cacheNames param for CaffeineCacheManager
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(METRICS_CACHE_NAME);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES) // Cache expires after 30 minutes
                .maximumSize(1000) // Limit cache size
                .recordStats() // Enable cache statistics
        );
        return cacheManager;
    }
}
