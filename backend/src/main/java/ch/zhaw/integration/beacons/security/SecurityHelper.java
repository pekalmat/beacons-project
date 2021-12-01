package ch.zhaw.integration.beacons.security;

import org.springframework.stereotype.Component;

@Component
public class SecurityHelper {

    private final SecurityConfig securityConfig;

    public SecurityHelper(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    public String getEncryptedPassword(String password) {
        return securityConfig.passwordEncoder().encode(password);
    }

}
