package com.example.testapi.service;

import com.example.testapi.domain.UserEntity;
import com.example.testapi.domain.UserLevelEntity;
import com.example.testapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserLevelService userLevelService;

    public UserService(UserRepository userRepository, UserLevelService userLevelService) {
        this.userRepository = userRepository;
        this.userLevelService = userLevelService;
    }

    // c
    public UserEntity register(String loginId, String password, Long userLevelId) {
        if (loginId == null || loginId.isBlank()) {
            throw new IllegalArgumentException("loginId는 비어있을 수 없습니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password는 비어있을 수 없습니다.");
        }
        if (userRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException("이미 사용중인 loginId입니다.");
        }

        UserLevelEntity userLevel = userLevelService.findById(userLevelId);

        return userRepository.save(new UserEntity(loginId, password, userLevel));
    }

    // r
    public UserEntity findById(Long id) {
        return getActiveUserOrThrow(id);
    }

    public UserEntity findByLoginId(String loginId) {
        return userRepository.findByLoginIdAndDeletedAtIsNull(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    // u
    public UserEntity updatePassword(Long id, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("password는 비어있을 수 없습니다.");
        }

        UserEntity user = getActiveUserOrThrow(id);
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    public UserEntity updateLevel(Long id, Long userLevelId) {
        UserEntity user = getActiveUserOrThrow(id);
        UserLevelEntity userLevel = userLevelService.findById(userLevelId);

        user.setUserLevel(userLevel);
        return userRepository.save(user);
    }

    // d
    public void delete(Long id) {
        UserEntity user = getActiveUserOrThrow(id);
        user.setDeletedAt(ZonedDateTime.now());
        userRepository.save(user);
    }

    private UserEntity getActiveUserOrThrow(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (user.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 사용자입니다.");
        }
        return user;
    }
}
