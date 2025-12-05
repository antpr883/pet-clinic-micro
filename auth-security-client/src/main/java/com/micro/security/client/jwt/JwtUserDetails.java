package com.micro.security.client.jwt;

import io.jsonwebtoken.Claims;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * UserDetails для Spring Security на основі JWT claims.
 * 
 * Використовується в JwtAuthenticationFilter для встановлення Authentication в SecurityContext.
 * 
 * BEST PRACTICE: Створюється автоматично при валідації JWT токена.
 * Містить userId, username, roles та permissions з токена.
 */
@Getter
public class JwtUserDetails implements UserDetails {

    private final Long userId;
    private final String username;
    private final Set<String> roles;
    private final Set<String> permissions;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUserDetails(Claims claims, JwtTokenValidator validator) {
        this.userId = validator.getUserId(claims);
        this.username = validator.getUsername(claims);
        this.roles = validator.getRoles(claims);
        this.permissions = validator.getPermissions(claims);
        
        // Build authorities: roles as ROLE_* and permissions as authorities
        this.authorities = Stream.concat(
                roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)),
                permissions.stream().map(SimpleGrantedAuthority::new)
        ).collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // JWT doesn't contain password
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

