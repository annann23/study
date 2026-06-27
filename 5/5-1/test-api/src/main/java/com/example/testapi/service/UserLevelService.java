package com.example.testapi.service;

import com.example.testapi.domain.UserLevelEntity;
import com.example.testapi.repository.UserLevelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLevelService {

    private final UserLevelRepository userLevelRepository;

    public UserLevelService(UserLevelRepository userLevelRepository) {
        this.userLevelRepository = userLevelRepository;
    }

    // c
    public UserLevelEntity create(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다.");
        }
        if (userLevelRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 등급입니다.");
        }

        return userLevelRepository.save(new UserLevelEntity(name));
    }

    // r
    public UserLevelEntity findById(Long id) {
        return userLevelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 등급입니다."));
    }

    public List<UserLevelEntity> findAll() {
        return userLevelRepository.findAll();
    }

    // u
    public UserLevelEntity update(Long id, String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다.");
        }
        if (userLevelRepository.existsByName(newName)) {
            throw new IllegalArgumentException("이미 존재하는 등급입니다.");
        }

        UserLevelEntity userLevel = userLevelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 등급입니다."));

        userLevel.setName(newName);
        return userLevelRepository.save(userLevel);
    }

    // d
    public void delete(Long id) {
        if (!userLevelRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 등급입니다.");
        }
        userLevelRepository.deleteById(id);
    }
}
