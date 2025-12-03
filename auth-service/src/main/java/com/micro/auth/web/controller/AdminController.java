package com.micro.auth.web.controller;

import com.micro.auth.data.dto.auth.PermissionCreateDto;
import com.micro.auth.data.dto.auth.RoleCreateDto;
import com.micro.auth.data.dto.auth.UserCreateDto;
import com.micro.auth.data.dto.auth.UserFullDto;
import com.micro.auth.data.entities.PermissionEntity;
import com.micro.auth.data.entities.RoleEntity;
import com.micro.auth.service.permission.PermissionService;
import com.micro.auth.service.role.RoleService;
import com.micro.auth.service.user.UserService;
import com.micro.auth.web.response.AppResponse;
import com.micro.auth.web.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Admin endpoints for managing users, roles, and permissions")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;

    @PostMapping("/users")
    @RequiresRoles("ADMIN")
    @Operation(
        summary = "Create user", 
        description = "Create a new user with roles",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<AppResponse<UserFullDto>> createUser(@Valid @RequestBody UserCreateDto createDto) {
        log.debug("Admin creating user: {}", createDto.getUsername());
        UserFullDto user = userService.create(createDto);
        return ResponseEntity.ok(AppResponse.successful(user));
    }

    @GetMapping("/users")
    @RequiresRoles("ADMIN")
    @Operation(
        summary = "Get all users", 
        description = "Get paginated list of users",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<PaginationResponse<UserFullDto>> getAllUsers(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        log.debug("Admin getting all users");
        Page<UserFullDto> page = userService.findAll(pageable);
        return ResponseEntity.ok(PaginationResponse.of(page));
    }

    @PostMapping("/roles")
    @RequiresRoles("ADMIN")
    @Operation(
        summary = "Create role", 
        description = "Create a new role with permissions",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<AppResponse<RoleEntity>> createRole(@Valid @RequestBody RoleCreateDto createDto) {
        log.debug("Admin creating role: {}", createDto.getName());
        RoleEntity role = roleService.create(createDto);
        return ResponseEntity.ok(AppResponse.successful(role));
    }

    @PostMapping("/permissions")
    @RequiresRoles("ADMIN")
    @Operation(
        summary = "Create permission", 
        description = "Create a new permission",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<AppResponse<PermissionEntity>> createPermission(
            @Valid @RequestBody PermissionCreateDto createDto) {
        log.debug("Admin creating permission: {}", createDto.getName());
        PermissionEntity permission = permissionService.create(createDto);
        return ResponseEntity.ok(AppResponse.successful(permission));
    }
}

