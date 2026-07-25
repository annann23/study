package com.example.testapi.domain;

import com.example.testapi.dtos.request.UserLoginRequest;
import com.example.testapi.security.OwnableResource;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
public class UserEntity implements OwnableResource {
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

    @Column(nullable = false, unique = true)
    private String nickname;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    private ZonedDateTime deletedAt;

    public UserEntity() {}

    public UserEntity(UserLoginRequest loginRequest, String nickname, UserLevelEntity userLevel) {
        this.userLevel = userLevel;
        this.loginId = loginRequest.loginId();
        this.password = loginRequest.password();
        this.nickname = nickname;
    }

    public Long getId() { return id; }

    public UserLevelEntity getUserLevel() { return userLevel; }
    public Long getUserLevelId() {return userLevel.getId(); }
    public String getLoginId() { return loginId; }
    public String getPassword() { return password; }
    public String getNickname() {return nickname; }
    public Set<RoleEntity> getRoles() { return roles; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public ZonedDateTime getDeletedAt() { return deletedAt; }

    @Override
    public Long getOwnerId() { return this.id; }

    public void setUserLevel(UserLevelEntity userLevel) { this.userLevel = userLevel; }
    public void setLoginId(String loginId) { this.loginId = loginId; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setPassword(String password) { this.password = password; }
    public void setDeletedAt(ZonedDateTime deletedAt) { this.deletedAt = deletedAt; }
    public void addRole(RoleEntity role) { this.roles.add(role); }

    public boolean hasPermission(String permissionName) {
        return roles.stream()
                .anyMatch(role -> role.hasPermission(permissionName));
    }
}
