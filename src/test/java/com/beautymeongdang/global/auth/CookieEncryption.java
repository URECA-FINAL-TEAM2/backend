package com.beautymeongdang.global.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
public class CookieEncryption {
    private final SecretKeySpec secretKey;
    private final byte[] key;
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;

    public CookieEncryption(@Value("${cookie.encryption.key:default-test-key-12345}") String encryptionKey) {
        this.key = initializeKey(encryptionKey);
        this.secretKey = new SecretKeySpec(key, "AES");
    }

    private byte[] initializeKey(String encryptionKey) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = sha.digest(encryptionKey.getBytes(StandardCharsets.UTF_8));
            return Arrays.copyOf(key, 16);
        } catch (Exception e) {
            throw new CookieEncryptionException("키 초기화 중 오류가 발생했습니다", e);
        }
    }

    public String encrypt(String strToEncrypt) {
        if (strToEncrypt == null) {
            throw new IllegalArgumentException("암호화할 문자열이 null일 수 없습니다");
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = generateIV();
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));

            // IV와 암호화된 데이터를 결합
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new CookieEncryptionException("암호화 중 오류가 발생했습니다", e);
        }
    }

    public String decrypt(String strToDecrypt) {
        if (strToDecrypt == null) {
            throw new IllegalArgumentException("복호화할 문자열이 null일 수 없습니다");
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(strToDecrypt);

            // IV 추출
            byte[] iv = Arrays.copyOfRange(decoded, 0, 12);
            byte[] encryptedData = Arrays.copyOfRange(decoded, 12, decoded.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            byte[] decrypted = cipher.doFinal(encryptedData);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CookieEncryptionException("복호화 중 오류가 발생했습니다", e);
        }
    }

    private byte[] generateIV() {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
}