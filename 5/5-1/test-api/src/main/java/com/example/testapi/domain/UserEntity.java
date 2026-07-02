package com.example.testapi.domain;

import com.example.testapi.controller.request.UserLoginDto;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name="user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_level_id", nullable = false)
    private UserLevelEntity userLevel;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    private ZonedDateTime deletedAt;

    public UserEntity() {}

    public UserEntity(UserLoginDto loginDto, UserLevelEntity userLevel) {
        this.userLevel = userLevel;
        this.loginId = loginDto.loginId();
        this.password = loginDto.password();
    }

    public Long getId() { return id; }

    public UserLevelEntity getUserLevel() { return userLevel; }
    public Long getUserLevelId() {return userLevel.getId(); }
    public String getLoginId() { return loginId; }
    public String getPassword() { return password; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public ZonedDateTime getDeletedAt() { return deletedAt; }

    public void setUserLevel(UserLevelEntity userLevel) { this.userLevel = userLevel; }
    public void setLoginId(String loginId) { this.loginId = loginId; }
    public void setPassword(String password) { this.password = password; }
    public void setDeletedAt(ZonedDateTime deletedAt) { this.deletedAt = deletedAt; }
}
