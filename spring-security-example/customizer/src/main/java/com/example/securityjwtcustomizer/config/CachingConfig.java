package com.example.securityjwtcustomizer.config;

import com.example.securityjwtcustomizer.entity.CustomUser;
import com.example.securityjwtcustomizer.repository.CustomUserRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableCaching
public class CachingConfig {

    private final CustomUserRepository userRepository;

    public CachingConfig(CustomUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public Cache<String, CustomUser> cacheCustomUser() {
        Cache<String, CustomUser> cache = Caffeine.newBuilder()
                .expireAfterWrite(36500, TimeUnit.DAYS)
                .maximumSize(10_0000)
                .build();
        this.userRepository.findAll().forEach(user -> cache.put(user.getName(), user));
        return cache;
    }

    @Bean
    public Cache<String, AtomicInteger> cacheLoginTimes() {
        return Caffeine.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .maximumSize(10_0000)
                .build();
    }

}
