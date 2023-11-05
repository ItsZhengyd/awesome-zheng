package com.example.mfa.service;

import java.security.GeneralSecurityException;

import com.j256.twofactorauth.TimeBasedOneTimePasswordUtil;

import org.springframework.stereotype.Service;

@Service
public class MfaService {

    public boolean check(String hexKey, String code) {
        try {
            return TimeBasedOneTimePasswordUtil.validateCurrentNumberHex(hexKey, Integer.parseInt(code), 10000);
        }
        catch (GeneralSecurityException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

}
