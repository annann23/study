package com.example.testapi;

import com.example.testapi.domain.BoardEntity;
import com.example.testapi.domain.BoardTypeEntity;
import com.example.testapi.domain.UserLevelEntity;
import com.example.testapi.repository.BoardRepository;
import com.example.testapi.repository.BoardTypeRepository;
import com.example.testapi.repository.UserLevelRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserLevelRepository userLevelRepository;
    private final BoardTypeRepository boardTypeRepository;
    private final BoardRepository boardRepository;

    public DataInitializer(UserLevelRepository userLevelRepository, BoardTypeRepository boardTypeRepository, BoardRepository boardRepository) {
        this.userLevelRepository = userLevelRepository;
        this.boardTypeRepository = boardTypeRepository;
        this.boardRepository = boardRepository;
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
            boardRepository.save(new BoardEntity(defaultType, "자유게시판"));
        }
    }
}
