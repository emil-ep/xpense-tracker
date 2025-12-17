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

    //cache lookup by user. In the format <userId : cacheName : ListOf(cacheKeys)>
    private final Map<String, Map<String, List<String>>> cacheByUser = new ConcurrentHashMap<>();

    //cache lookup by cacheName. In the format <cacheName : cacheKeys>
    private final Map<String, List<String>> cacheByName = new ConcurrentHashMap<>();

    public void storeByCacheName(String cacheName, String key){
        List<String> cacheKeys = cacheByName.get(cacheName);
        if (cacheKeys == null){
            cacheKeys = new ArrayList<>();
        }
        cacheKeys.add(key);
    }

    public void clearCache(String cacheName){
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null){
            cache.clear();
        }
    }


    public void storeByUser(String userId, String key, String cacheName){
        if (cacheByUser.containsKey(userId)){
            Map<String, List<String>> cacheByCacheName = cacheByUser.get(userId);
            List<String> cacheKeys = cacheByCacheName.get(cacheName);
            if (!cacheKeys.contains(key)){
                cacheKeys.add(key);
            }
            cacheByCacheName.replace(cacheName, cacheKeys);
            cacheByUser.replace(userId, cacheByCacheName);
        }else{
            Map<String, List<String>> cacheByCacheName = new ConcurrentHashMap<>();
            List<String> keyList = new ArrayList<>();
            keyList.add(key);
            cacheByCacheName.put(cacheName, keyList);
            cacheByUser.put(userId, cacheByCacheName);
        }
        LOGGER.debug("Stored the cacheKey : {} for user : {}", key, userId);
    }

    public void clearCache(String cacheName, String userId){
        Cache cache = cacheManager.getCache(cacheName);
        Map<String, List<String>> cacheKeyByCacheName = cacheByUser.get(userId);
        if (cacheKeyByCacheName == null){
            return;
        }
        List<String> cacheKeys = cacheKeyByCacheName.get(cacheName);
        if (cache == null){
            return;
        }
        for (String key: cacheKeys){
            cache.evict(key);
            LOGGER.debug("Cleared cacheKey : {} for user: {}", key, userId);
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
