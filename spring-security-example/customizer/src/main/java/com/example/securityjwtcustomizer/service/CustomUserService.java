package com.example.securityjwtcustomizer.service;


import com.example.securityjwtcustomizer.entity.CustomUser;
import com.example.securityjwtcustomizer.repository.CustomUserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CustomUserService {

    private final CustomUserRepository userRepository;

    public CustomUserService(CustomUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(CustomUser currentUser) {
        userRepository.save(currentUser);
    }

    @Cacheable(cacheNames = "customUserByName",key = "#name")
    public CustomUser findByName(String name) {
        return userRepository.findByName(name);
    }
}
