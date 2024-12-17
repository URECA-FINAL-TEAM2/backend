package com.beautymeongdang.global.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
class CookieEncryptionTest {

    @Test
    @DisplayName("쿠키 암호화 및 복호화 테스트")
    void encryptAndDecryptTest() {
        // given
        String originalValue = "test-token";

        // when
        String encryptedValue = CookieEncryption.encrypt(originalValue);
        String decryptedValue = CookieEncryption.decrypt(encryptedValue, originalValue);

        // then
        assertThat(decryptedValue).isEqualTo(originalValue);
    }
}