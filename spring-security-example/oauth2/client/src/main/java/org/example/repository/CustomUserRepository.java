package org.example.repository;

import org.example.entity.CustomUser;
import org.springframework.data.repository.CrudRepository;

public interface CustomUserRepository extends CrudRepository<CustomUser, Long> {

    // 1
    CustomUser findByName(String name);

}