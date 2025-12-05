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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
        description = """
            Create a new user with optional roles assignment.
            
            **Required fields:**
            - `username` - must be 3-100 characters, unique
            - `password` - must be at least 8 characters
            - `email` - must be valid email format
            
            **Optional fields:**
            - `roleNames` - set of role names to assign (e.g., ["ADMIN", "VET"])
            
            **Available roles:** ADMIN, VET, RECEPTIONIST, OWNER, ORCHESTRATOR
            """,
        security = @SecurityRequirement(name = "bearerAuth"),
        requestBody = @RequestBody(
            description = "User creation data",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserCreateDto.class),
                examples = {
                    @ExampleObject(
                        name = "Create user with roles",
                        value = """
                            {
                              "username": "newuser",
                              "password": "password123",
                              "email": "newuser@example.com",
                              "roleNames": ["VET", "RECEPTIONIST"]
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Create user without roles",
                        value = """
                            {
                              "username": "simpleuser",
                              "password": "password123",
                              "email": "simpleuser@example.com"
                            }
                            """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User created successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "success": true,
                          "data": {
                            "id": 1,
                            "username": "newuser",
                            "email": "newuser@example.com",
                            "active": true,
                            "roles": [
                              {
                                "id": 2,
                                "name": "VET",
                                "active": true
                              }
                            ],
                            "createdAt": "2025-01-05T10:00:00",
                            "updatedAt": "2025-01-05T10:00:00"
                          }
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "Validation error or username/email already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    public ResponseEntity<AppResponse<UserFullDto>> createUser(@Valid @RequestBody UserCreateDto createDto) {
        log.debug("Admin creating user: {}", createDto.getUsername());
        UserFullDto user = userService.create(createDto);
        return ResponseEntity.ok(AppResponse.successful(user));
    }

    @GetMapping("/users")
    @RequiresRoles("ADMIN")
    @Operation(
        summary = "Get all users", 
        description = """
            Get paginated list of users.
            
            **Available sort fields:** id, username, email, active, createdAt, updatedAt
            
            **Two ways to use:**
            
            1. **Query parameters (recommended):**
               ```
               GET /api/v1/auth/users?page=0&size=1&sort=id
               GET /api/v1/auth/users?page=0&size=20&sort=username,asc
               GET /api/v1/auth/users?page=0&size=10&sort=createdAt,desc
               ```
            
            2. **JSON body:**
               ```json
               {
                 "page": 0,
                 "size": 1,
                 "sort": "id"
               }
               ```
               ```json
               {
                 "page": 0,
                 "size": 20,
                 "sort": "username,asc"
               }
               ```
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @Parameter(
        name = "page",
        description = "Page number (0-indexed)",
        example = "0",
        schema = @Schema(type = "integer", defaultValue = "0")
    )
    @Parameter(
        name = "size",
        description = "Page size",
        example = "20",
        schema = @Schema(type = "integer", defaultValue = "20")
    )
    @Parameter(
        name = "sort",
        description = "Sort field and direction. Available: id, username, email, active, createdAt, updatedAt. Format: field or field,direction (asc/desc)",
        examples = {
            @ExampleObject(name = "Sort by ID", value = "id"),
            @ExampleObject(name = "Sort by username ascending", value = "username,asc"),
            @ExampleObject(name = "Sort by createdAt descending", value = "createdAt,desc")
        },
        schema = @Schema(type = "string", defaultValue = "id")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "content": [
                            {
                              "id": 1,
                              "username": "admin",
                              "email": "admin@example.com",
                              "active": true,
                              "roles": [
                                {
                                  "id": 1,
                                  "name": "ADMIN",
                                  "active": true
                                }
                              ],
                              "createdAt": "2025-01-05T10:00:00",
                              "updatedAt": "2025-01-05T10:00:00"
                            }
                          ],
                          "pagination": {
                            "total": 10,
                            "limit": 20,
                            "page": 0,
                            "pages": 1,
                            "first": true,
                            "last": true,
                            "hasNext": false,
                            "hasPrevious": false
                          }
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
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
        description = """
            Create a new role with optional permissions assignment.
            
            **Required fields:**
            - `name` - role name, must be 2-50 characters, unique (e.g., "ADMIN", "VET", "RECEPTIONIST")
            
            **Optional fields:**
            - `permissionNames` - set of permission names to assign
            
            **Example permissions:** 
            - CLINIC_CASE_READ, CLINIC_CASE_WRITE
            - PET_READ, PET_WRITE
            - PERSON_READ, PERSON_WRITE
            - CLINIC_DIAGNOSTICS_WRITE, CLINIC_HOSPITALIZATION_WRITE
            """,
        security = @SecurityRequirement(name = "bearerAuth"),
        requestBody = @RequestBody(
            description = "Role creation data",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RoleCreateDto.class),
                examples = {
                    @ExampleObject(
                        name = "Create role with permissions",
                        value = """
                            {
                              "name": "SENIOR_VET",
                              "permissionNames": [
                                "PET_READ",
                                "PET_WRITE",
                                "CLINIC_CASE_READ",
                                "CLINIC_CASE_WRITE",
                                "CLINIC_DIAGNOSTICS_WRITE"
                              ]
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Create role without permissions",
                        value = """
                            {
                              "name": "JUNIOR_VET"
                            }
                            """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Role created successfully"
        ),
        @ApiResponse(responseCode = "400", description = "Validation error or role name already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    public ResponseEntity<AppResponse<RoleEntity>> createRole(@Valid @RequestBody RoleCreateDto createDto) {
        log.debug("Admin creating role: {}", createDto.getName());
        RoleEntity role = roleService.create(createDto);
        return ResponseEntity.ok(AppResponse.successful(role));
    }

    @PostMapping("/permissions")
    @RequiresRoles("ADMIN")
    @Operation(
        summary = "Create permission", 
        description = """
            Create a new permission.
            
            **Required fields:**
            - `name` - permission name, must be 3-100 characters, unique
            
            **Permission naming convention:**
            - Format: `RESOURCE_ACTION` (e.g., `PET_READ`, `CLINIC_CASE_WRITE`)
            - Examples:
              - `PET_READ`, `PET_WRITE`
              - `PERSON_READ`, `PERSON_WRITE`
              - `CLINIC_CASE_READ`, `CLINIC_CASE_WRITE`
              - `CLINIC_DIAGNOSTICS_WRITE`
              - `CLINIC_HOSPITALIZATION_WRITE`
            """,
        security = @SecurityRequirement(name = "bearerAuth"),
        requestBody = @RequestBody(
            description = "Permission creation data",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PermissionCreateDto.class),
                examples = {
                    @ExampleObject(
                        name = "Create permission",
                        value = """
                            {
                              "name": "PET_DELETE"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Create clinic permission",
                        value = """
                            {
                              "name": "CLINIC_APPOINTMENT_WRITE"
                            }
                            """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Permission created successfully"
        ),
        @ApiResponse(responseCode = "400", description = "Validation error or permission name already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    public ResponseEntity<AppResponse<PermissionEntity>> createPermission(
            @Valid @RequestBody PermissionCreateDto createDto) {
        log.debug("Admin creating permission: {}", createDto.getName());
        PermissionEntity permission = permissionService.create(createDto);
        return ResponseEntity.ok(AppResponse.successful(permission));
    }
}

