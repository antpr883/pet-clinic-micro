package com.micro.auth.data.constants;

/**
 * Константи для Entity Graph атрибутів UserEntity.
 * Використовуються для оптимізації завантаження зв'язаних сутностей.
 */
public final class UserEntityGraphConstants {
    
    private UserEntityGraphConstants() {
        // Utility class
    }
    
    /**
     * Завантажити ролі користувача.
     */
    public static final String ROLES = "roles";
    
    /**
     * Завантажити ролі та пермішени ролей.
     */
    public static final String ROLES_PERMISSIONS = "roles.permissions";
    
    /**
     * Завантажити refresh токени користувача.
     */
    public static final String REFRESH_TOKENS = "refreshTokens";
}

