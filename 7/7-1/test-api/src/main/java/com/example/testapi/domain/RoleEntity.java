package com.example.testapi.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="roles")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private final Set<PermissionEntity> permissions = new HashSet<>();

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    public RoleEntity(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() {return name; }
    public Set<PermissionEntity> getPermissions() { return permissions; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }

    public void setName(String name) { this.name = name; }
    public void addPermission(PermissionEntity permission) {
        this.permissions.add(permission);
    }

    public boolean hasPermission(String permissionName) {
        return permissions.stream()
                .anyMatch(p -> p.getName().equals(permissionName));
    }

}
