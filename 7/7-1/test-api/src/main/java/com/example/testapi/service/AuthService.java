package com.example.testapi.service;
import com.example.testapi.dtos.auth.UserLoginRequest;
import com.example.testapi.domain.UserEntity;
import com.example.testapi.domain.UserLevelEntity;
import com.example.testapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserLevelService userLevelService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, UserLevelService userLevelService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userLevelService = userLevelService;
        this.passwordEncoder = passwordEncoder;
    }

    // c
    public UserEntity register(UserLoginRequest loginRequest, String nickname) {
        if (loginRequest.loginId() == null || loginRequest.loginId().isBlank()) {
            throw new IllegalArgumentException("loginId는 비어있을 수 없습니다.");
        }
        if (loginRequest.password() == null || loginRequest.password().isBlank()) {
            throw new IllegalArgumentException("password는 비어있을 수 없습니다.");
        }
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 비어있을 수 없습니다.");
        }
        if (userRepository.existsByLoginId(loginRequest.loginId())) {
            throw new IllegalArgumentException("이미 사용중인 loginId입니다.");
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }

        Long defaultUserLevel = 1L;

        UserLevelEntity userLevel = userLevelService.findById(defaultUserLevel);

        UserEntity user = new UserEntity(loginRequest, nickname, userLevel);
        user.setPassword(passwordEncoder.encode(loginRequest.password()));

        return userRepository.save(user);
    }

    public UserEntity login(UserLoginRequest loginRequest) {
        if (loginRequest.loginId() == null || loginRequest.loginId().isBlank()) {
            throw new IllegalArgumentException("loginId는 비어있을 수 없습니다.");
        }
        if (loginRequest.password() == null || loginRequest.password().isBlank()) {
            throw new IllegalArgumentException("password는 비어있을 수 없습니다.");
        }

        UserEntity user = userRepository.findByLoginIdAndDeletedAtIsNull(loginRequest.loginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디나 패스워드가 틀렸습니다."));

        if(!user.getPassword().equals(loginRequest.password())) {throw new IllegalArgumentException("아이디나 패스워드가 틀렸습니다.");}
        return user;
    }
}
