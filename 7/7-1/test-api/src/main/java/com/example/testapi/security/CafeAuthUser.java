package com.example.testapi.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CafeAuthUser implements UserDetails, Serializable {
    private final Long userId;
    private final String loginId;
    private final String password;
    private final Set<String> authorities ;

    public CafeAuthUser(Long userId, String loginId, String password, Set<String> authorities) {
        this.userId = userId;
        this.loginId = loginId;
        this.password = password;
        this.authorities = (authorities != null) ? authorities : Set.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() { return loginId; }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    public Long getUserId() {
        return userId;
    }

    public boolean hasAuthority(String name) {
        return authorities.contains(name);
    }
}