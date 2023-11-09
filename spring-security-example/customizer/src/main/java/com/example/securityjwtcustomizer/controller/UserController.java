package com.example.securityjwtcustomizer.controller;

import com.example.securityjwtcustomizer.config.CurrentUser;
import com.example.securityjwtcustomizer.entity.CustomUser;
import com.example.securityjwtcustomizer.service.CustomUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final CustomUserService customUserService;

    public UserController(CustomUserService customUserService) {
        this.customUserService = customUserService;
    }

    @GetMapping("/user")
    public CustomUser user(@CurrentUser CustomUser currentUser) {
        return currentUser;
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public String register(@RequestBody CustomUser currentUser){

        registerCheck();
        customUserService.register(currentUser);

        return "注册成功";
    }

    /**
     * 注册校验与限流
     */
    private void registerCheck() {


    }

}
