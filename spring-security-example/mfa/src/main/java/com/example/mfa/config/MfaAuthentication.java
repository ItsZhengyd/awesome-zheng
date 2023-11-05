package com.example.mfa.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;

import java.util.Collections;

/**
 * 多因素认证
 */
public class MfaAuthentication extends AbstractAuthenticationToken {

    private final Authentication first;

    public MfaAuthentication(Authentication first) {
        super(Collections.emptyList());
        this.first = first;
    }

    @Override
    public Object getPrincipal() {
        return this.first.getPrincipal();
    }

    @Override
    public Object getCredentials() {
        return this.first.getCredentials();
    }

    @Override
    public void eraseCredentials() {
        if (this.first instanceof CredentialsContainer) {
            ((CredentialsContainer) this.first).eraseCredentials();
        }
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    public Authentication getFirst() {
        return this.first;
    }

}
