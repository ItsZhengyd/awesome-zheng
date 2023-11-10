package com.example.securityjwtcustomizer.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        return Redisson.create();
    }

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        RedissonSpringCacheManager cacheManager = new RedissonSpringCacheManager(redissonClient);
        Map<String, CacheConfig> cacheConfigMap = new HashMap<>();

        // 缓存cacheCustomUser的策略
        CacheConfig cacheCustomUserConfig = new CacheConfig();
        cacheCustomUserConfig.setMaxIdleTime(-1);
        cacheCustomUserConfig.setTTL(-1);
        cacheCustomUserConfig.setMaxSize(10_000);

        // 缓存cacheCustomUser的策略
        CacheConfig cacheLoginTimesConfig = new CacheConfig();
        cacheLoginTimesConfig.setMaxIdleTime(-1);
        cacheLoginTimesConfig.setTTL(-1);
        cacheLoginTimesConfig.setMaxSize(10_000);


        cacheConfigMap.put("cacheCustomUser",cacheCustomUserConfig);
        cacheConfigMap.put("cacheLoginTimes",cacheLoginTimesConfig);

        cacheManager.setConfig(cacheConfigMap);
        return cacheManager;
    }

}
