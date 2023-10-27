package com.example.securityjwtcustomizer.repository;

import com.example.securityjwtcustomizer.entity.CustomUser;
import org.springframework.data.repository.CrudRepository;

public interface CustomUserRepository extends CrudRepository<CustomUser, Long> {

    // 1
    CustomUser findByName(String name);

}