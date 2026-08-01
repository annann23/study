package com.example.testapi;

import com.example.testapi.domain.BoardEntity;
import com.example.testapi.domain.BoardTypeEntity;
import com.example.testapi.domain.RoleEntity;
import com.example.testapi.domain.UserEntity;
import com.example.testapi.domain.UserLevelEntity;
import com.example.testapi.dtos.auth.UserLoginRequest;
import com.example.testapi.repository.BoardRepository;
import com.example.testapi.repository.BoardTypeRepository;
import com.example.testapi.repository.RoleRepository;
import com.example.testapi.repository.UserLevelRepository;
import com.example.testapi.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final String ADMIN_LOGIN_ID = "admin";
    private static final String ADMIN_PASSWORD = "1234";

    private final UserLevelRepository userLevelRepository;
    private final BoardTypeRepository boardTypeRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserLevelRepository userLevelRepository, BoardTypeRepository boardTypeRepository, BoardRepository boardRepository,
                           UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userLevelRepository = userLevelRepository;
        this.boardTypeRepository = boardTypeRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userLevelRepository.count() == 0) {
            userLevelRepository.saveAll(List.of(
                    new UserLevelEntity("일반"),
                    new UserLevelEntity("관리자")
            ));
        }

        if (boardTypeRepository.count() == 0) {
            boardTypeRepository.saveAll(List.of(
                    new BoardTypeEntity("일반"),
                    new BoardTypeEntity("포럼"),
                    new BoardTypeEntity("갤러리")
            ));
        }

        if (boardRepository.count() == 0) {
            BoardTypeEntity defaultType = boardTypeRepository.findAll().get(0);
            boardRepository.save(new BoardEntity(defaultType, "자유게시판", false));
        }

        if (!userRepository.existsByLoginId(ADMIN_LOGIN_ID)) {
            RoleEntity adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new IllegalStateException("ADMIN 역할이 존재하지 않습니다"));
            UserLevelEntity userLevel = userLevelRepository.findAll().get(0);

            UserEntity admin = new UserEntity(new UserLoginRequest(ADMIN_LOGIN_ID, ADMIN_PASSWORD), "관리자", userLevel);
            admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            admin.addRole(adminRole);
            userRepository.save(admin);
        }
    }
}
