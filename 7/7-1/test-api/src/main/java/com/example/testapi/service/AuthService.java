package com.example.testapi.service;
import com.example.testapi.dtos.auth.UserLoginRequest;
import com.example.testapi.domain.RoleEntity;
import com.example.testapi.domain.UserEntity;
import com.example.testapi.domain.UserLevelEntity;
import com.example.testapi.repository.RoleRepository;
import com.example.testapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final String DEFAULT_ROLE = "USER";

    private final UserRepository userRepository;
    private final UserLevelService userLevelService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AuthService(UserRepository userRepository, UserLevelService userLevelService, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userLevelService = userLevelService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
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

        RoleEntity defaultRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new IllegalStateException(DEFAULT_ROLE + "역할이 존재하지 않습니다."));
        user.addRole(defaultRole);

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
