package com.beautymeongdang.global.login.service.impl;

import com.beautymeongdang.domain.user.dto.UserDTO;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.Role;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.global.jwt.JwtProvider;
import com.beautymeongdang.global.login.entity.GoogleToken;
import com.beautymeongdang.global.login.entity.GoogleUserInfo;
import com.beautymeongdang.global.login.entity.KakaoToken;
import com.beautymeongdang.global.login.entity.KakaoUserInfo;
import com.beautymeongdang.global.login.service.OAuth2Service;
import com.beautymeongdang.global.oauth2.CustomOAuth2User;
import com.beautymeongdang.global.oauth2.GoogleResponse;
import com.beautymeongdang.global.oauth2.KakaoResponse;
import com.beautymeongdang.global.login.service.OAuth2ResponseService;

import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.global.oauth2.OAuth2AuthorizationClient;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


// CustomOAuth2UserServiceImpl.java

@Slf4j
@Service
@AllArgsConstructor
public class CustomOAuth2UserServiceImpl extends DefaultOAuth2UserService implements OAuth2Service {
    private static final String DEFAULT_PROFILE_IMAGE = "https://s3-beauty-meongdang.s3.ap-northeast-2.amazonaws.com/%ED%9A%8C%EC%9B%90+%ED%94%84%EB%A1%9C%ED%95%84+%EC%9D%B4%EB%AF%B8%EC%A7%80/%ED%9A%8C%EC%9B%90%EA%B8%B0%EB%B3%B8%EC%9D%B4%EB%AF%B8%EC%A7%80.png";
    private final UserRepository userRepository;
    private final OAuth2AuthorizationClient oauth2Client;
    private final JwtProvider jwtProvider;
    private final CustomerRepository customerRepository;
    private final GroomerRepository groomerRepository;

    // 탈퇴 상태 및 남은 일수 확인
    private Map<String, Object> checkDeletionStatus(User user) {
        Map<String, Object> status = new HashMap<>();
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // 고객 탈퇴 상태 확인
        Optional<Customer> deletedCustomer = customerRepository
                .findDeletedCustomerInLast30Days(user, thirtyDaysAgo);
        boolean isCustomerDeleted = deletedCustomer.isPresent();
        long customerDaysLeft = 0;
        if (deletedCustomer.isPresent()) {
            long daysLeft = 30 - ChronoUnit.DAYS.between(deletedCustomer.get().getUpdatedAt(), LocalDateTime.now());
            customerDaysLeft = Math.max(0, daysLeft);  // 음수인 경우 0으로 설정
            isCustomerDeleted = daysLeft > 0;  // 남은 일수가 0 이하면 탈퇴 상태 false
        }

        // 미용사 탈퇴 상태 확인도 동일하게 수정
        Optional<Groomer> deletedGroomer = groomerRepository
                .findDeletedGroomerInLast30Days(user, thirtyDaysAgo);
        boolean isGroomerDeleted = deletedGroomer.isPresent();
        long groomerDaysLeft = 0;
        if (deletedGroomer.isPresent()) {
            long daysLeft = 30 - ChronoUnit.DAYS.between(deletedGroomer.get().getUpdatedAt(), LocalDateTime.now());
            groomerDaysLeft = Math.max(0, daysLeft);  // 음수인 경우 0으로 설정
            isGroomerDeleted = daysLeft > 0;  // 남은 일수가 0 이하면 탈퇴 상태 false
        }

        status.put("customerDeleted", isCustomerDeleted);
        status.put("groomerDeleted", isGroomerDeleted);
        status.put("customerDaysUntilReregister", customerDaysLeft);
        status.put("groomerDaysUntilReregister", groomerDaysLeft);

        return status;
    }


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("login-log🔑 OAuth2 로그인 시작 - Provider: {}", userRequest.getClientRegistration().getRegistrationId());
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("login-log👤 OAuth2 유저 정보 로드 완료: {}", oAuth2User.getAttributes());
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2ResponseService oAuth2Response = null;
        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
            log.info("login-log🟡 카카오 응답 처리 중...");
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            log.info("login-log🟡 구글 응답 처리 중...");
        } else {
            return null;
        }

        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();

        // 먼저 providerId로 사용자 찾기
        Optional<User> existingUser = userRepository.findByProviderIdAndSocialProvider(providerId, provider);
        log.info("🔍login-log 기존 유저 조회 결과: {}", existingUser.isPresent() ? "유저 존재" : "신규 유저");

        User user;
        if (existingUser.isEmpty()) {
            // 신규 사용자
            user = User.builder()
                    .userName(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .providerId(providerId)
                    .socialProvider(provider)
                    .profileImage(DEFAULT_PROFILE_IMAGE)
                    .isRegister(false)
                    .build();
            userRepository.save(user);
        } else {
            user = existingUser.get();
        }
        // 탈퇴 상태 확인
        Map<String, Object> deletionStatus = checkDeletionStatus(user);

        UserDTO userDTO = UserDTO.builder()
                .id(user.getUserId())
                .username(user.getUserName())
                .nickname(user.getNickname())
                .roles(findActiveRoles(user))
                .profileImage(user.getProfileImage())
                .isRegister(user.isRegister())
                .customerDeletionStatus((Boolean) deletionStatus.get("customerDeleted"))
                .groomerDeletionStatus((Boolean) deletionStatus.get("groomerDeleted"))
                .customerDaysUntilReregister((Long) deletionStatus.get("customerDaysUntilReregister"))
                .groomerDaysUntilReregister((Long) deletionStatus.get("groomerDaysUntilReregister"))
                .build();

        return new CustomOAuth2User(userDTO);
    }

    private Map<String, Object> addUserRoleIds(User user, Map<String, Object> responseData) {
        // Customer ID 확인 및 추가
        customerRepository.findCustomerIdByUserId(user)
                .ifPresent(customerId -> responseData.put("customerId", customerId));

        // Groomer ID 확인 및 추가
        groomerRepository.findGroomerIdByUserId(user)
                .ifPresent(groomerId -> responseData.put("groomerId", groomerId));

        return responseData;
    }

    @Override
    public Map<String, Object> processKakaoLogin(String code, HttpServletResponse response) {
        log.info("login-log 🟡 카카오 로그인 프로세스 시작 - 인가 코드: {}", code);
        // 1. 프론트엔드에서 받은 인가 코드로 카카오 액세스 토큰을 요청하고 받아옴
        KakaoToken kakaoToken = oauth2Client.getKakaoAccessToken(code);
        log.info("login-log 🎫 카카오 액세스 토큰 발급 완료");

        // 2. 받아온 액세스 토큰으로 카카오 API를 호출하여 사용자 정보(이름, 이메일 등)를 조회
        KakaoUserInfo userInfo = oauth2Client.getKakaoUserInfo(kakaoToken.getAccess_token());
        log.info("login-log 👤 카카오 유저 정보 조회 완료 - ID: {}, Email: {}", userInfo.getId(), userInfo.getEmail());

        // 3. 카카오에서 받은 고유 ID와 제공자 정보(KAKAO)로 기존 사용자가 있는지 DB에서 조회
        Optional<User> existingUser = userRepository.findByProviderIdAndSocialProvider(
                String.valueOf(userInfo.getId()),
                "KAKAO"
        );

        User user;
        if (existingUser.isEmpty()) {
            // 기존 사용자가 없으면 새로운 사용자 객체를 생성
            // isRegister(false)로 설정하여 추가 정보 입력이 필요함을 표시
            user = User.builder()
                    .userName(userInfo.getName())
                    .email(userInfo.getEmail())
                    .providerId(String.valueOf(userInfo.getId()))
                    .socialProvider("KAKAO")
                    .profileImage(DEFAULT_PROFILE_IMAGE)
                    .isRegister(false)
                    .build();
            userRepository.save(user); // 새로운 사용자 정보를 DB에 저장
        } else {
            // 기존 사용자가 있으면 해당 사용자 정보를 가져옴
            user = existingUser.get();
        }

        // 4. 사용자 인증을 위한 JWT 토큰을 생성 (접근 토큰, 리프레시 토큰 등)
        Map<String, Object> tokenInfo = jwtProvider.createTokens(user, response);

        // 5. 클라이언트에 전달할 사용자 정보를 DTO 객체로 변환
        UserDTO userDTO = UserDTO.builder()
                .id(user.getUserId())
                .username(user.getUserName())
                .nickname(user.getNickname())
                .roles(findActiveRoles(user))
                .profileImage(user.getProfileImage())
                .isRegister(user.isRegister())
                .build();

        // 6. 클라이언트에 반환할 응답 데이터를 구성
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", tokenInfo.get("access_token")); // JWT 접근 토큰
        responseData.put("user", userDTO);                              // 사용자 정보
        responseData.put("email", user.getEmail());
        responseData.put("isNewUser", !user.isRegister());             // 신규 사용자 여부

        // 7. 최종 응답 데이터 반환
        return addUserRoleIds(user, responseData);
    }

    @Override
    public Map<String, Object> processGoogleLogin(String code, HttpServletResponse response) {
        log.info("login-log 🔵 구글 로그인 프로세스 시작 - 인가 코드: {}", code);

        // 1. 구글 액세스 토큰 요청
        GoogleToken googleToken = oauth2Client.getGoogleAccessToken(code);
        log.info("login-log 🎫 구글 액세스 토큰 발급 완료");

        // 2. 사용자 정보 조회
        GoogleUserInfo userInfo = oauth2Client.getGoogleUserInfo(googleToken.getAccess_token());
        log.info("login-log 👤 구글 유저 정보 조회 완료 - ID: {}, Email: {}", userInfo.getId(), userInfo.getEmail());

        // 3. DB에서 사용자 조회
        Optional<User> existingUser = userRepository.findByProviderIdAndSocialProvider(
                String.valueOf(userInfo.getId()),
                "GOOGLE"
        );

        User user;
        if (existingUser.isEmpty()) {
            // 신규 사용자 생성
            user = User.builder()
                    .userName(userInfo.getName())
                    .email(userInfo.getEmail())
                    .providerId(String.valueOf(userInfo.getId()))
                    .socialProvider("GOOGLE")
                    .profileImage(DEFAULT_PROFILE_IMAGE)
                    .isRegister(false)
                    .build();
            userRepository.save(user);
        } else {
            user = existingUser.get();
        }

        // 4. JWT 토큰 생성
        Map<String, Object> tokenInfo = jwtProvider.createTokens(user, response);

        // 5. UserDTO 생성
        UserDTO userDTO = UserDTO.builder()
                .id(user.getUserId())
                .username(user.getUserName())
                .nickname(user.getNickname())
                .roles(findActiveRoles(user))
                .profileImage(user.getProfileImage())
                .isRegister(user.isRegister())
                .build();

        // 6. 응답 데이터 구성
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", tokenInfo.get("access_token"));
        responseData.put("user", userDTO);
        responseData.put("email", user.getEmail());
        responseData.put("isNewUser", !user.isRegister());

        return addUserRoleIds(user, responseData);
    }

    // 활성화된 역할만 조회하는 메소드
    private Set<Role> findActiveRoles(User user) {
        Set<Role> activeRoles = new HashSet<>();

        // 고객 역할 체크
        if (user.getRoles().contains(Role.고객) &&
                customerRepository.existsByUserIdAndIsDeletedFalse(user)) {
            activeRoles.add(Role.고객);
        }

        // 미용사 역할 체크
        if (user.getRoles().contains(Role.미용사) &&
                groomerRepository.existsByUserIdAndIsDeletedFalse(user)) {
            activeRoles.add(Role.미용사);
        }

        return activeRoles;
    }
}
