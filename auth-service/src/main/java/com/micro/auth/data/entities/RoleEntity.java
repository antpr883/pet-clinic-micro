package com.micro.auth.data.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles", schema = "auth_schema")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"permissions", "users"})
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class RoleEntity extends BaseActiveEntity {

    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name; // "ADMIN", "VET", "RECEPTIONIST", "OWNER", "ORCHESTRATOR"

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permissions",
        schema = "auth_schema",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<PermissionEntity> permissions = new HashSet<>();

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserEntity> users = new HashSet<>();
}

