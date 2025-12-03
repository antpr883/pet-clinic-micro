package com.micro.auth.util;

import org.apache.shiro.crypto.hash.Sha256Hash;

import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Утиліта для генерації hash пароля для seed даних.
 * 
 * Використання:
 * Запустити main метод, скопіювати згенеровані salt та hash в seed SQL файл.
 */
public class PasswordHashGenerator {

    private static final int SALT_LENGTH = 32; // 256 bits
    private static final int HASH_ITERATIONS = 1024;

    public static void main(String[] args) {
        String password = "admin123";
        
        // Генеруємо salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        String saltHex = HexFormat.of().formatHex(salt);
        
        // Генеруємо hash
        Sha256Hash hash = new Sha256Hash(password, saltHex, HASH_ITERATIONS);
        String hashHex = hash.toHex();
        
        System.out.println("==========================================");
        System.out.println("Password Hash Generator");
        System.out.println("==========================================");
        System.out.println("Password: " + password);
        System.out.println("Salt: " + saltHex);
        System.out.println("Hash: " + hashHex);
        System.out.println("==========================================");
        System.out.println();
        System.out.println("SQL для оновлення seed даних:");
        System.out.println("-- Salt: " + saltHex);
        System.out.println("-- Hash: " + hashHex);
        System.out.println();
        System.out.println("Оновіть файл: src/main/resources/db/changelog/changes/v1.0/009-seed-admin-user.sql");
    }
}

