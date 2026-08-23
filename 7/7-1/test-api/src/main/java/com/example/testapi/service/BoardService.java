package com.example.testapi.service;

import com.example.testapi.domain.BoardEntity;
import com.example.testapi.domain.BoardTypeEntity;
import com.example.testapi.domain.UserEntity;
import com.example.testapi.repository.BoardRepository;
import com.example.testapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardTypeService boardTypeService;

    public BoardService(BoardRepository boardRepository, UserRepository userRepository, BoardTypeService boardTypeService) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.boardTypeService = boardTypeService;
    }

    // c
    public BoardEntity create(String name, Long boardTypeId, boolean isPrivate) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다.");
        }
        if (boardRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 게시판입니다.");
        }

        BoardTypeEntity boardType = boardTypeService.findById(boardTypeId);
        BoardEntity board = new BoardEntity(boardType, name, isPrivate);
        board.setStatus(BoardEntity.BoardStatus.APPROVED);
        return boardRepository.save(board);
    }

    public BoardEntity request(String name, Long boardTypeId, Long userId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다.");
        }
        if (boardRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 게시판입니다.");
        }

        UserEntity requestedUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        BoardTypeEntity boardType = boardTypeService.findById(boardTypeId);
        BoardEntity board =  new BoardEntity(boardType, name, false);
        board.setRequestedBy(requestedUser);
        board.setStatus(BoardEntity.BoardStatus.PENDING);

        return boardRepository.save(board);
    }


    // r
    public BoardEntity findById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));
    }

    public List<BoardEntity> findAll() {
        return boardRepository.findAllByStatus(BoardEntity.BoardStatus.APPROVED);
    }

    public List<BoardEntity> findAllPending() {
        return boardRepository.findAllByStatus(BoardEntity.BoardStatus.PENDING);
    }

    public List<BoardEntity> findAllByBoardType(Long boardTypeId) {
        boardTypeService.findById(boardTypeId);
        return boardRepository.findAllByBoardTypeId(boardTypeId);
    }

    // u
    public BoardEntity updateName(Long id, String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다.");
        }
        if (boardRepository.existsByName(newName)) {
            throw new IllegalArgumentException("이미 존재하는 게시판입니다.");
        }

        BoardEntity board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));

        board.setName(newName);
        return boardRepository.save(board);
    }

    public BoardEntity updateBoardType(Long id, Long boardTypeId) {
        BoardEntity board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));

        BoardTypeEntity boardType = boardTypeService.findById(boardTypeId);
        board.setBoardType(boardType);
        return boardRepository.save(board);
    }

    public BoardEntity approve(Long id) {
        BoardEntity board = boardRepository.findById(id)
                .orElseThrow(() -> new
                        IllegalArgumentException("존재하지 않는 게시판입니다."));

        if (board.getStatus() !=
                BoardEntity.BoardStatus.PENDING) {
            throw new IllegalArgumentException("승인 대기 상태가 아닙니다.");
        }

        board.setOperator(board.getRequestedBy());
        board.setStatus(BoardEntity.BoardStatus.APPROVED);

        return boardRepository.save(board);
    }

    public BoardEntity reject(Long id) {
        BoardEntity board = boardRepository.findById(id)
                .orElseThrow(() -> new
                        IllegalArgumentException("존재하지 않는 게시판입니다."));

        if (board.getStatus() !=
                BoardEntity.BoardStatus.PENDING) {
            throw new IllegalArgumentException("승인 대기 상태가 아닙니다.");
        }

        board.setStatus(BoardEntity.BoardStatus.REJECTED);

        return boardRepository.save(board);
    }

    // d
    public void delete(Long id) {
        if (!boardRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 게시판입니다.");
        }
        boardRepository.deleteById(id);
    }
}
