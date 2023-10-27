package com.example.securityjwtcustomizer.controller;

import com.example.securityjwtcustomizer.config.CurrentUser;
import com.example.securityjwtcustomizer.entity.CustomUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/user")
    public CustomUser user(@CurrentUser CustomUser currentUser) {
        return currentUser;
    }

}
