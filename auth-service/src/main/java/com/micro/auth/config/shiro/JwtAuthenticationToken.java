package com.micro.auth.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * Custom AuthenticationToken для JWT токенів.
 * 
 * Використовується для аутентифікації через JWT токени в stateless архітектурі.
 * Містить userId та JWT токен для перевірки в Realm.
 */
public class JwtAuthenticationToken implements AuthenticationToken {

    private final Long userId;
    private final String jwtToken;

    public JwtAuthenticationToken(Long userId, String jwtToken) {
        this.userId = userId;
        this.jwtToken = jwtToken;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public Object getCredentials() {
        return jwtToken;
    }

    public Long getUserId() {
        return userId;
    }

    public String getJwtToken() {
        return jwtToken;
    }
}

