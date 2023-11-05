
生成code
```java
    TimeBasedOneTimePasswordUtil.generateCurrentNumberHex(hexKey);
```

验证code
```java
    private String getSecret(MfaAuthentication authentication) throws Exception {
        if (authentication.getPrincipal() instanceof CustomUser) {
        CustomUser user = (CustomUser) authentication.getPrincipal();
        byte[] bytes = Hex.decode(user.getSecret());
        return new String(this.encryptor.decrypt(bytes));
        }
        // earlier factor failed
        return this.failedAuthenticationSecret;
    }
        
    String hexKey = getSecret(authentication);
    TimeBasedOneTimePasswordUtil.validateCurrentNumberHex(hexKey, Integer.parseInt(code), 10000);
```