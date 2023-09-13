package org.example.service;

import org.example.dao.UserRepository;
import org.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {this.userRepository = userRepository;}

    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }
}