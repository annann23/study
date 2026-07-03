package com.example.testapi.service;
import com.example.testapi.controller.request.UserLoginDto;
import com.example.testapi.domain.UserEntity;
import com.example.testapi.domain.UserLevelEntity;
import com.example.testapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserLevelService userLevelService;

    public AuthService(UserRepository userRepository, UserLevelService userLevelService) {
        this.userRepository = userRepository;
        this.userLevelService = userLevelService;
    }

    // c
    public UserEntity register(UserLoginDto loginDto, String nickname) {
        if (loginDto.loginId() == null || loginDto.loginId().isBlank()) {
            throw new IllegalArgumentException("loginId는 비어있을 수 없습니다.");
        }
        if (loginDto.password() == null || loginDto.password().isBlank()) {
            throw new IllegalArgumentException("password는 비어있을 수 없습니다.");
        }
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 비어있을 수 없습니다.");
        }
        if (userRepository.existsByLoginId(loginDto.loginId())) {
            throw new IllegalArgumentException("이미 사용중인 loginId입니다.");
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }

        Long defaultUserLevel = 1L;

        UserLevelEntity userLevel = userLevelService.findById(defaultUserLevel);

        return userRepository.save(new UserEntity(loginDto, userLevel));
    }

    public UserEntity login(UserLoginDto loginDto) {
        if (loginDto.loginId() == null || loginDto.loginId().isBlank()) {
            throw new IllegalArgumentException("loginId는 비어있을 수 없습니다.");
        }
        if (loginDto.password() == null || loginDto.password().isBlank()) {
            throw new IllegalArgumentException("password는 비어있을 수 없습니다.");
        }

        UserEntity user = userRepository.findByLoginIdAndDeletedAtIsNull(loginDto.loginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디나 패스워드가 틀렸습니다."));

        if(!user.getPassword().equals(loginDto.password())) {throw new IllegalArgumentException("아이디나 패스워드가 틀렸습니다.");}
        return user;
    }
}
