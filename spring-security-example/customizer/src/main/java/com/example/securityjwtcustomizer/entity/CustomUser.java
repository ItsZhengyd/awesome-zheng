package com.example.securityjwtcustomizer.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class CustomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;
    private String password;

    @JsonCreator
    public CustomUser(long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

}
