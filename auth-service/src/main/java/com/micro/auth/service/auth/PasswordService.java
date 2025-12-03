package com.micro.auth.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HexFormat;

@Service
@Slf4j
public class PasswordService {

    private static final int SALT_LENGTH = 32; // 256 bits
    private static final int HASH_ITERATIONS = 1024;

    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return HexFormat.of().formatHex(salt);
    }

    public String hashPassword(String password, String salt) {
        Sha256Hash hash = new Sha256Hash(password, salt, HASH_ITERATIONS);
        return hash.toHex();
    }

    public boolean verifyPassword(String password, String salt, String storedHash) {
        String computedHash = hashPassword(password, salt);
        return computedHash.equals(storedHash);
    }
}

