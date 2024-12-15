package com.beautymeongdang.domain.user.service.impl;

import com.beautymeongdang.domain.user.dto.*;
import com.beautymeongdang.domain.user.entity.*;
import com.beautymeongdang.domain.user.repository.*;
import com.beautymeongdang.domain.user.service.UserService;
import com.beautymeongdang.global.oauth2.CustomOAuth2User;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    public Map<String, Object> registerCustomer(CustomerRegisterRequestDTO requestDto, MultipartFile profileImage) {
        Long userId = getCurrentUserId();
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

            // 탈퇴한 고객인지 확인
            Optional<Customer> deletedCustomer = customerRepository.findByUserId(user);
            if (deletedCustomer.isPresent() && deletedCustomer.get().isDeleted()) {

                // LocalDateTime을 사용하여 30일 체크
                LocalDateTime deletedAt = deletedCustomer.get().getUpdatedAt();
                if (deletedAt != null &&
                        ChronoUnit.DAYS.between(deletedAt, LocalDateTime.now()) < 30) {
                    throw new RuntimeException("탈퇴 후 30일이 지나지 않았습니다. 30일 이후에 다시 가입해주세요.");
                }
            }

            if (customerRepository.existsByUserIdAndIsDeletedFalse(user)) {
                throw new RuntimeException("이미 회원가입된 고객입니다.");
            }

            String profileImageUrl = null;
            if (profileImage != null && !profileImage.isEmpty()) {
                List<UploadedFile> uploadedFiles = fileStore.storeFiles(List.of(profileImage), FileStore.USER_PROFILE);
                profileImageUrl = uploadedFiles.get(0).getFileUrl();
            }

            user.getRoles().add(Role.고객);
            user.updateUserInfo(requestDto.getPhone(), requestDto.getNickName());
            if (profileImageUrl != null) {
                user.updateProfileImage(profileImageUrl);
            }
            user.completeRegistration();
            userRepository.save(user);

            Sigungu sigungu = sigunguRepository.findById(requestDto.getSigunguId())
                    .orElseThrow(() -> new EntityNotFoundException("Sigungu not found with id: " + requestDto.getSigunguId()));

            Customer customer = Customer.builder()
                    .userId(user)
                    .sigunguId(sigungu)
                    .build();

            Customer savedCustomer = customerRepository.save(customer);

            Map<String, Object> responseData = Map.of(
                    "userId", user.getUserId(),
                    "customerId", savedCustomer.getCustomerId(),
                    "nickname", user.getNickname(),
                    "isRegister", user.isRegister(),
                    "roles", findActiveRoles(user),
                    "sigunguId", sigungu.getSigunguId(),
                    "sidoId", sigungu.getSidoId().getSidoId(),
                    "profileImage", profileImageUrl != null ? profileImageUrl : user.getProfileImage()
            );
            return responseData;
        } catch (Exception e) {
            log.error("Customer registration failed: {}", e.getMessage(), e);
            throw new RuntimeException("Customer registration failed: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> registerGroomer(GroomerRegisterRequestDTO requestDto, MultipartFile profileImage) {
        Long userId = getCurrentUserId();
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

            // 탈퇴한 미용사인지 확인
            Optional<Groomer> deletedGroomer = groomerRepository.findByUserId(user);
            if (deletedGroomer.isPresent() && deletedGroomer.get().isDeleted()) {
                LocalDateTime deletedAt = deletedGroomer.get().getUpdatedAt();
                if (deletedAt != null &&
                        ChronoUnit.DAYS.between(deletedAt, LocalDateTime.now()) < 30) {
                    throw new RuntimeException("탈퇴 후 30일이 지나지 않았습니다. 30일 이후에 다시 가입해주세요.");
                }
            }

            if (groomerRepository.existsByUserIdAndIsDeletedFalse(user)) {
                throw new RuntimeException("User is already registered as a groomer");
            }

            String profileImageUrl = null;
            if (profileImage != null && !profileImage.isEmpty()) {
                List<UploadedFile> uploadedFiles = fileStore.storeFiles(List.of(profileImage), FileStore.USER_PROFILE);
                profileImageUrl = uploadedFiles.get(0).getFileUrl();
            }

            user.getRoles().add(Role.미용사);
            user.updateUserInfo(requestDto.getPhone(), requestDto.getNickName());
            if (profileImageUrl != null) {
                user.updateProfileImage(profileImageUrl);
            }
            user.completeRegistration();
            userRepository.save(user);

            Groomer groomer = Groomer.builder()
                    .userId(user)
                    .skill(requestDto.getSkill())
                    .build();

            Groomer savedGroomer = groomerRepository.save(groomer);

            Map<String, Object> responseData = Map.of(
                    "userId", user.getUserId(),
                    "groomerId", savedGroomer.getGroomerId(),
                    "nickname", user.getNickname(),
                    "isRegister", user.isRegister(),
                    "roles", findActiveRoles(user),
                    "profileImage", profileImageUrl != null ? profileImageUrl : user.getProfileImage()
            );
            return responseData;
        } catch (Exception e) {
            log.error("Groomer registration failed: {}", e.getMessage(), e);
            throw new RuntimeException("Groomer registration failed: " + e.getMessage());
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

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomOAuth2User)) {
            throw new RuntimeException("Not authenticated");
        }
        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
        return oauth2User.getUserId();
    }

    @Transactional
    public void checkAndUpdateRegistrationStatus(User user) {
        boolean hasCustomer = customerRepository.existsByUserIdAndIsDeletedFalse(user);
        boolean hasGroomer = groomerRepository.existsByUserIdAndIsDeletedFalse(user);

        if (!hasCustomer && !hasGroomer) {
            user.resetRegistration();
            userRepository.save(user);
        }
    }

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