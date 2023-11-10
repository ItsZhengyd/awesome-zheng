package com.example.securityjwtcustomizer.controller;

import com.example.securityjwtcustomizer.config.CurrentUser;
import com.example.securityjwtcustomizer.entity.CustomUser;
import com.example.securityjwtcustomizer.service.CustomUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
public class UserController {

    private final CustomUserService customUserService;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    public UserController(CustomUserService customUserService, AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
        this.customUserService = customUserService;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    @GetMapping("/user")
    public CustomUser user(@CurrentUser CustomUser currentUser) {
        return currentUser;
    }

    @GetMapping("/user/{name}")
    public CustomUser login(@PathVariable String name) {
        return customUserService.findByName(name);
    }

    /**
     * 注册并登录，同时返回一个jwt，用于访问授权资源
     */
    @PostMapping("/register")
    public String register(CustomUser currentUser) {

        registerCheck();
        // 注册
        customUserService.register(currentUser);
        // 登录
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(currentUser.getName(), currentUser.getPassword());
        this.authenticationManager.authenticate(authentication);
        // 生成一个jwt，用于访问授权资源
        Instant now = Instant.now();
        long expiry = 36000L;
        // @formatter:off
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        // @formatter:on
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

    }

    /**
     * 注册校验与限流
     */
    private void registerCheck() {


    }

}
