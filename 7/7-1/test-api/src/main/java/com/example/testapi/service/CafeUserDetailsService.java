package com.example.testapi.service;

import com.example.testapi.domain.PermissionEntity;
import com.example.testapi.domain.UserEntity;
import com.example.testapi.repository.UserRepository;
import com.example.testapi.security.CafeAuthUser;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CafeUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CafeUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(@NonNull String loginId) throws UsernameNotFoundException {
        UserEntity user = userRepository
                .findByLoginIdAndDeletedAtIsNull(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다"));
        Set<String> authorities = user.getRoles().stream().flatMap(role -> role.getPermissions().stream()).map(PermissionEntity::getName).collect(Collectors.toSet());

        return new CafeAuthUser(user.getId(), user.getLoginId(), user.getPassword(), authorities);
    }

}
