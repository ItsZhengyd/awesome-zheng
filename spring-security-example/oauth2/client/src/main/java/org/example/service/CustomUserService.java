package org.example.service;


import org.example.entity.CustomUser;
import org.example.repository.CustomUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserService {

    private final CustomUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserService(CustomUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(CustomUser currentUser) {
        CustomUser newUser = new CustomUser();
        BeanUtils.copyProperties(currentUser, newUser);
        String encode = passwordEncoder.encode(currentUser.getPassword());
        newUser.setPassword(encode);
        userRepository.save(newUser);
    }

    @Cacheable(cacheNames = "customUserByName",key = "#name")
    public CustomUser findByName(String name) {
        return userRepository.findByName(name);
    }
}
