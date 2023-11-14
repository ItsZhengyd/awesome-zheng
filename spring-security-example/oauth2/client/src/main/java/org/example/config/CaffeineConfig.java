package org.example.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.example.entity.CustomUser;
import org.example.repository.CustomUserRepository;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableCaching
public class CaffeineConfig {

    private final CustomUserRepository userRepository;

    public CaffeineConfig(CustomUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 初始化缓存，将用户信息放进缓存中
     */
    @Bean
    public Cache<String, CustomUser> cacheCustomUser() {
        Cache<String, CustomUser> cache = Caffeine.newBuilder()
                .expireAfterWrite(36500, TimeUnit.DAYS)
                .maximumSize(10_0000)
                .build();
        this.userRepository.findAll().forEach(user -> cache.put(user.getName(), user));
        return cache;
    }

    /**
     * 初始化缓存，将登录次数信息放进缓存中，用于接口限流
     */
    @Bean
    public Cache<String, AtomicInteger> cacheLoginTimes() {
        // 本地缓存

        return Caffeine.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .maximumSize(100_0000)
                .build();
    }

//    @Bean
//    public CacheManager cacheManager() {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
//
//        // 默认缓存配置
//        cacheManager.setCaffeine(Caffeine.newBuilder()
//                .expireAfterWrite(5, TimeUnit.MINUTES)
//                .maximumSize(1000));
//
//
//        return cacheManager;
//    }
}