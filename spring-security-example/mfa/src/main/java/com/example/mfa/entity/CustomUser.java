package com.example.mfa.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class CustomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private final String secret;
    @JsonIgnore
    private final String answer;

    @JsonCreator
    public CustomUser(long id, String email, String password,String secret, String answer) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.secret = secret;
        this.answer = answer;
    }

    public CustomUser(CustomUser user) {
        this(user.id, user.email, user.password, user.secret, user.answer);
    }

}
