package com.example.mfa.repository;

import com.example.mfa.entity.CustomUser;
import org.springframework.data.repository.CrudRepository;

public interface CustomUserRepository extends CrudRepository<CustomUser, Long> {

    // 1
    CustomUser findByName(String name);

}