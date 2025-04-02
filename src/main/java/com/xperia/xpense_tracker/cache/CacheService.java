package com.xperia.xpense_tracker.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {

    @Autowired
    private CaffeineCacheManager cacheManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);

    private final Map<String, List<String>> cacheByUser = new ConcurrentHashMap<>();

    public void storeByUser(String userId, String key, String cacheName){
        if (cacheByUser.containsKey(userId)){
            List<String> cacheKeys = cacheByUser.get(userId);
            cacheKeys.add(key);
            cacheByUser.replace(userId, cacheKeys);
        }else{
            List<String> keyList = new ArrayList<>();
            keyList.add(key);
            cacheByUser.put(userId, keyList);
        }
        LOGGER.info("Stored the cacheKey : {} for user : {}", key, userId);
    }

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

//    public void clearCacheByUser(String cacheName, String userKey){
//        Cache cache = cacheManager.getCache(cacheName);
//        if (cache != null){
//            Objects.requireNonNull(cache.getNativeCache())
//                    .asMap()
//                    .keySet()
//                    .stream()
//                    .filter(key -> key.toString().contains(userKey)) // Check if key contains user info
//                    .forEach(cache::evict);
//        }
//    }
}
