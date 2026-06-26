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

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    public BoardEntity() {}

    public BoardEntity(BoardTypeEntity boardType, String name) {
        this.boardType = boardType;
        this.name = name;
    }

    public BoardTypeEntity getBoardType() { return boardType; }
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
