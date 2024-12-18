package com.beautymeongdang.global.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = {
        "cookie.encryption.key=test-encryption-key-for-unit-tests"
})
class CookieEncryptionTest {
    @Autowired
    private CookieEncryption cookieEncryption;

    @Test
    @DisplayName("쿠키 암호화 및 복호화 테스트")
    void encryptAndDecryptTest() {
        // given
        String originalValue = "test-token";

        // when
        String encryptedValue = cookieEncryption.encrypt(originalValue);
        String decryptedValue = cookieEncryption.decrypt(encryptedValue);

        // then
        assertThat(decryptedValue).isEqualTo(originalValue);
    }

    @Test
    @DisplayName("null 입력 시 예외 발생 테스트")
    void nullInputTest() {
        assertThatThrownBy(() -> cookieEncryption.encrypt(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("암호화할 문자열이 null일 수 없습니다");
    }

    @Test
    @DisplayName("빈 문자열 암호화 테스트")
    void emptyStringTest() {
        String encrypted = cookieEncryption.encrypt("");
        String decrypted = cookieEncryption.decrypt(encrypted);
        assertThat(decrypted).isEmpty();
    }
}
