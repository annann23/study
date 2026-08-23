package com.example.testapi.domain;


import com.example.testapi.security.OwnableResource;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name="board")
public class BoardEntity implements OwnableResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_type", nullable = false)
    private BoardTypeEntity boardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    private UserEntity operator;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean isPrivate;

    public enum BoardStatus { // 유저 요청으로 게시판 생성시 구분을 위해 정의
        PENDING, APPROVED, REJECTED
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardStatus status = BoardStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by")
    private UserEntity requestedBy;

    private String reason;

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
    public Long getOperatorId() { return operator == null ? null : operator.getId(); }
    @Override
    public Long getOwnerId() { return getOperatorId(); }
    public BoardStatus getStatus() {return status;}
    public UserEntity getRequestedBy() {return requestedBy; }
    public String getReason() { return reason; }

    public Long getId() { return id; }
    public boolean isPrivate() { return isPrivate; }

    public void setName(String name) { this.name = name; }
    public void setBoardType(BoardTypeEntity boardType) { this.boardType = boardType; }

    public void setRequestedBy(UserEntity requestedBy) { this.requestedBy = requestedBy; }
    public void setReason(String reason) { this.reason = reason; }
    public void setStatus(BoardStatus status) { this.status = status; }
    public void setOperator(UserEntity operator) {this.operator = operator; }
}
