package com.beautymeongdang.global.login.controller;

import com.beautymeongdang.domain.user.dto.UserDTO;
import com.beautymeongdang.global.common.dto.ApiResponse;
import com.beautymeongdang.global.login.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuth2Controller {

    private final OAuth2Service oauth2Service;

    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<?> handleKakaoCallback(@RequestParam String code) {
//        try {
//            log.info("카카오 인가 코드 수신: {}", code);
//
//            Map<String, Object> responseData = oauth2Service.processKakaoLogin(code);
//            log.info("responseData 데이터 확인 ", responseData);
//
//            UserDTO userDTO = (UserDTO) responseData.get("user");
//
//            if (!userDTO.isRegister()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body(ApiResponse.badRequest(400, "추가 정보 입력이 필요한 사용자입니다."));
//            }
//            log.info("데이터 확인 ", responseData);
//            return ResponseEntity.ok(ApiResponse.ok(200, responseData, "로그인 성공"));
//
//        } catch (Exception e) {
//            log.error("OAuth2 인증 실패", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ApiResponse.badRequest(500, "인증 실패: " + e.getMessage()));
//        }

        return ResponseEntity.ok(ApiResponse.ok(200, "responseData 백앤드 테스트", "로그인 성공"));
    }
}