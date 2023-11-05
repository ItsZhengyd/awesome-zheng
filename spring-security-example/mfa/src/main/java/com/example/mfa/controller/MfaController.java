package com.example.mfa.controller;

import com.example.mfa.config.CurrentUser;
import com.example.mfa.config.MfaAuthentication;
import com.example.mfa.config.MfaAuthenticationHandler;
import com.example.mfa.entity.CustomUser;
import com.example.mfa.service.MfaService;
import com.j256.twofactorauth.TimeBasedOneTimePasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;

@Controller
public class MfaController {

    @GetMapping("/user")
    public CustomUser user(@CurrentUser CustomUser currentUser) {
        return currentUser;
    }

    private final MfaService mfaService;

    private final BytesEncryptor encryptor;

    private final PasswordEncoder encoder;

    private final AuthenticationSuccessHandler successHandler;

    private final AuthenticationFailureHandler failureHandler;

    private final String failedAuthenticationSecret;

    private final String failedAuthenticationSecurityAnswer;

    public MfaController(MfaService mfaService, BytesEncryptor encryptor, PasswordEncoder encoder,
                         AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler) {

        this.mfaService = mfaService;
        this.encryptor = encryptor;
        this.encoder = encoder;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;

        this.failedAuthenticationSecret = randomValue();
        this.failedAuthenticationSecurityAnswer = this.encoder.encode(randomValue());
    }

    @GetMapping("/second-factor")
    public String requestSecondFactor() {
        return "second-factor";
    }

    @PostMapping("/second-factor")
    public void processSecondFactor(@RequestParam("code") String code, MfaAuthentication authentication,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        MfaAuthenticationHandler handler = new MfaAuthenticationHandler("/third-factor");
        String secret = getSecret(authentication);
        code = TimeBasedOneTimePasswordUtil.generateCurrentNumberHex(secret)+"";
        if (this.mfaService.check(secret, code)) {
            handler.onAuthenticationSuccess(request, response, authentication.getFirst());
        }
        else {
            handler.onAuthenticationFailure(request, response, new BadCredentialsException("bad credentials"));
        }
    }

    @GetMapping("/third-factor")
    public String requestThirdFactor() {
        return "third-factor";
    }

    @PostMapping("/third-factor")
    public void processThirdFactor(@RequestParam("answer") String answer, MfaAuthentication authentication,
                                   HttpServletRequest request, HttpServletResponse response) throws Exception {
        String encodedAnswer = getAnswer(authentication);
        if (this.encoder.matches(answer, encodedAnswer)) {
            SecurityContextHolder.getContext().setAuthentication(authentication.getFirst());
            this.successHandler.onAuthenticationSuccess(request, response, authentication.getFirst());
        }
        else {
            this.failureHandler.onAuthenticationFailure(request, response,
                    new BadCredentialsException("bad credentials"));
        }
    }

    private String getSecret(MfaAuthentication authentication) throws Exception {
        if (authentication.getPrincipal() instanceof CustomUser) {
            CustomUser user = (CustomUser) authentication.getPrincipal();
            byte[] bytes = Hex.decode(user.getSecret());
            return new String(this.encryptor.decrypt(bytes));
        }
        // earlier factor failed
        return this.failedAuthenticationSecret;
    }

    private String getAnswer(MfaAuthentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUser) {
            CustomUser user = (CustomUser) authentication.getPrincipal();
            return user.getAnswer();
        }
        // earlier factor failed
        return this.failedAuthenticationSecurityAnswer;
    }

    private static String randomValue() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return new String(Hex.encode(bytes));
    }

}

