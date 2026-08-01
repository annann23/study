package com.example.testapi.domain;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name="board")
public class BoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_type", nullable = false)
    private BoardTypeEntity boardType;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean isPrivate;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    public BoardEntity() {}

    public BoardEntity(BoardTypeEntity boardType, String name, boolean isPrivate) {
        this.boardType = boardType;
        this.name = name;
        this.isPrivate = isPrivate;
    }

    public BoardTypeEntity getBoardType() { return boardType; }
    public Long getBoardTypeId() { return boardType.getId(); }
    public String getName() { return name; }

    public Long getId() { return id; }
    public boolean isPrivate() { return isPrivate; }

    public void setName(String name) { this.name = name; }
    public void setBoardType(BoardTypeEntity boardType) { this.boardType = boardType; }
}
