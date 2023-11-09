package com.example.securityjwtcustomizer.service;


import com.example.securityjwtcustomizer.entity.CustomUser;
import com.example.securityjwtcustomizer.repository.CustomUserRepository;
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

    public CustomUser findByUsername(String username) {
        return userRepository.findByName(username);
    }
}
