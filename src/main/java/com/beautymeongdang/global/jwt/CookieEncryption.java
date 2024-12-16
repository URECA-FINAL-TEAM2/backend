package com.beautymeongdang.global.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Component
public class CookieEncryption {
    private static final String ALGORITHM = "AES";

    private static String secretKey;

    @Value("${spring.jwt.secret}")
    public void setSecretKey(String secret) {
        CookieEncryption.secretKey = secret;
    }

    public static String encrypt(String value) {
        try {
            if (secretKey == null) {
                throw new IllegalStateException("Secret key not initialized");
            }

            SecretKeySpec secretKeySpec = generateKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(value.getBytes());
            String encryptedValue = Base64.getEncoder().encodeToString(encryptedBytes);

            // SHA-256 해시 생성
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(encryptedValue.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting value", e);
        }
    }

    public static String decrypt(String hashedValue, String originalValue) {
        try {
            if (secretKey == null) {
                throw new IllegalStateException("Secret key not initialized");
            }

            // 원본 값의 해시를 생성하여 비교
            String calculatedHash = encrypt(originalValue);
            if (!hashedValue.equals(calculatedHash)) {
                throw new RuntimeException("Cookie value has been tampered with");
            }

            SecretKeySpec secretKeySpec = generateKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(originalValue));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting value", e);
        }
    }

    private static SecretKeySpec generateKey() throws Exception {
        if (secretKey == null) {
            throw new IllegalStateException("Secret key not initialized");
        }

        byte[] key = secretKey.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        return new SecretKeySpec(key, ALGORITHM);
    }

    // 보안 검증을 위한 추가 메서드
    public static boolean validateHash(String hashedValue, String originalValue) {
        try {
            String calculatedHash = encrypt(originalValue);
            return hashedValue.equals(calculatedHash);
        } catch (Exception e) {
            return false;
        }
    }

    // 키가 초기화되었는지 확인하는 메서드
    public static boolean isInitialized() {
        return secretKey != null;
    }
}