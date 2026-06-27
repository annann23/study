package com.example.testapi.service;

import com.example.testapi.domain.BoardTypeEntity;
import com.example.testapi.repository.BoardTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardTypeService {

    private final BoardTypeRepository boardTypeRepository;

    public BoardTypeService(BoardTypeRepository boardTypeRepository) {
        this.boardTypeRepository = boardTypeRepository;
    }

    // c
    public BoardTypeEntity create(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다.");
        }
        if (boardTypeRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 게시판 종류입니다.");
        }

        return boardTypeRepository.save(new BoardTypeEntity(name));
    }

    // r
    public BoardTypeEntity findById(Long id) {
        return boardTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판 종류입니다."));
    }

    public List<BoardTypeEntity> findAll() {
        return boardTypeRepository.findAll();
    }

    // u
    public BoardTypeEntity update(Long id, String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다.");
        }
        if (boardTypeRepository.existsByName(newName)) {
            throw new IllegalArgumentException("이미 존재하는 게시판 종류입니다.");
        }

        BoardTypeEntity boardType = boardTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판 종류입니다."));

        boardType.setName(newName);
        return boardTypeRepository.save(boardType);
    }

    // d
    public void delete(Long id) {
        if (!boardTypeRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 게시판 종류입니다.");
        }
        boardTypeRepository.deleteById(id);
    }
}
