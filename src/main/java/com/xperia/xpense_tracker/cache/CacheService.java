package com.xperia.xpense_tracker.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Autowired
    private CaffeineCacheManager cacheManager;


    public void clearCache(String cacheName){
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null){
            cache.clear();
        }
    }

    public void clearCacheByKey(String cacheName, String key){
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null){
            cache.evict(key);
        }
    }
}
