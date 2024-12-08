package com.beautymeongdang.domain.user.service.Impl;

import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.user.dto.*;
import com.beautymeongdang.domain.user.entity.*;
import com.beautymeongdang.domain.user.repository.*;
import com.beautymeongdang.domain.user.service.UserService;
import com.beautymeongdang.global.region.entity.Sigungu;
import com.beautymeongdang.global.region.repository.SigunguRepository;
import com.beautymeongdang.infra.s3.FileStore;
import com.beautymeongdang.global.common.entity.UploadedFile;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final GroomerRepository groomerRepository;
    private final SigunguRepository sigunguRepository;
    private final FileStore fileStore;

    @Override
    public Map<String, Object> registerCustomer(Long userId, CustomerRegisterRequestDTO requestDto, MultipartFile profileImage) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

            if (customerRepository.existsByUserId(user)) {
                throw new RuntimeException("User is already registered as a customer");
            }

            // 프로필 이미지 처리
            handleProfileImage(user, profileImage);

            user.getRoles().add(Role.고객);
            user.updateUserInfo(requestDto.getPhone(), requestDto.getNickName());
            user.completeRegistration();
            userRepository.save(user);

            // 시군구 정보 조회
            Sigungu sigungu = sigunguRepository.findById(requestDto.getSigungoId())
                    .orElseThrow(() -> new EntityNotFoundException("Sigungu not found with id: " + requestDto.getSigungoId()));

            // Customer 정보 저장
            Customer customer = Customer.builder()
                    .userId(user)
                    .sigunguId(sigungu)
                    .latitude(requestDto.getLatitude())
                    .longitude(requestDto.getLongitude())
                    .build();

            customerRepository.save(customer);

            // 필요한 데이터 반환
            Map<String, Object> responseData = Map.of(
                    "userId", user.getUserId(),
                    "nickname", user.getNickname(),
                    "isRegister", user.isRegister()
            );
            return responseData;
        } catch (Exception e) {
            log.error("Customer registration failed: {}", e.getMessage(), e);
            throw new RuntimeException("Customer registration failed: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> registerGroomer(Long userId, GroomerRegisterRequestDTO requestDto, MultipartFile profileImage) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

            if (groomerRepository.existsByUserId(user)) {
                throw new RuntimeException("User is already registered as a groomer");
            }

            // 프로필 이미지 처리
            handleProfileImage(user, profileImage);

            user.getRoles().add(Role.미용사);
            user.updateUserInfo(requestDto.getPhone(), requestDto.getNickName());
            user.completeRegistration();
            userRepository.save(user);

            // Groomer 정보 저장
            Groomer groomer = Groomer.builder()
                    .userId(user)
                    .skill(requestDto.getSkill())
                    .build();

            groomerRepository.save(groomer);

            Map<String, Object> responseData = Map.of(
                    "userId", user.getUserId(),
                    "nickname", user.getNickname(),
                    "isRegister", user.isRegister()
            );
            return responseData;
        } catch (Exception e) {
            log.error("Groomer registration failed: {}", e.getMessage(), e);
            throw new RuntimeException("Groomer registration failed: " + e.getMessage());
        }
    }


    private void handleProfileImage(User user, MultipartFile profileImage) {
        if (profileImage != null && !profileImage.isEmpty()) {
            List<UploadedFile> uploadedFiles = fileStore.storeFiles(List.of(profileImage), FileStore.USER_PROFILE);
            user.updateProfileImage(uploadedFiles.get(0).getFileUrl());
        }
    }

    @Override
    public String getNicknameCheckMessage(String nickname) {
        boolean isAvailable = !userRepository.existsByNickname(nickname);
        return isAvailable ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다.";
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.error("Cookie deletion failed: {}", e.getMessage(), e);
            throw new RuntimeException("Cookie deletion failed", e);
        }
    }
}