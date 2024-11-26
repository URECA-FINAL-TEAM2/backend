package com.beautymeongdang.domain.user.service.Impl;

import com.beautymeongdang.domain.shop.dto.ShopDTO;
import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.user.dto.CustomerDTO;
import com.beautymeongdang.domain.user.dto.GroomerDTO;
import com.beautymeongdang.domain.user.dto.GroomerRegistrationDTO;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.domain.user.service.UserService;
import com.beautymeongdang.global.region.entity.Sigungu;
import com.beautymeongdang.global.region.repository.SigunguRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final GroomerRepository groomerRepository;
    private final CustomerRepository customerRepository;
    private final ShopRepository shopRepository;
    private final SigunguRepository sigunguRepository;

    @Override
    public User registerCustomer(String username, CustomerDTO customerDTO) {
        try {
            User user = userRepository.findByUserName(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            User updatedUser = User.builder()
                    .userId(user.getUserId())
                    .userName(user.getUserName())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .role("ROLE_USER")
                    .socialProvider(user.getSocialProvider())
                    .profileImage(user.getProfileImage())
                    .phone(customerDTO.getPhone())
                    .build();

            updatedUser = userRepository.save(updatedUser);

            Sigungu sigungu = sigunguRepository.findById(customerDTO.getSigunguId())
                    .orElseThrow(() -> new RuntimeException("Invalid sigungu id"));

            Customer customer = Customer.builder()
                    .userId(updatedUser)
                    .sigunguId(sigungu)
                    .address(customerDTO.getAddress())
                    .latitude(customerDTO.getLatitude())
                    .longitude(customerDTO.getLongitude())
                    .build();

            customerRepository.save(customer);
            return updatedUser;
        } catch (Exception e) {
            // 예외 발생 시 로그에 자세한 정보 기록
            log.error("Customer registration failed: {}", e.getMessage(), e);
            throw new RuntimeException("Customer registration failed", e); // 또는 더 구체적인 예외
        }
    }

    @Override
    public User registerGroomer(String username, GroomerRegistrationDTO registrationDTO) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User updatedUser = User.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role("ROLE_STYLIST")
                .socialProvider(user.getSocialProvider())
                .profileImage(user.getProfileImage())
                .phone(registrationDTO.getPhone())  // GroomerRegistrationDTO에 phone 필드 추가 필요
                .build();

        updatedUser = userRepository.save(updatedUser);

        // Groomer 정보 저장
        Groomer groomer = Groomer.builder()
                .userId(updatedUser)
                .skill(registrationDTO.getSkill())
                .build();

        groomer = groomerRepository.save(groomer);


        // Shop 정보 저장
        Sigungu sigungu = sigunguRepository.findById(registrationDTO.getSigunguId())
                .orElseThrow(() -> new RuntimeException("Invalid sigungu id"));

        Shop shop = Shop.builder()
                .groomerId(groomer)
                .sigunguId(sigungu)
                .shopName(registrationDTO.getShopName())
                .description(registrationDTO.getDescription())
                .address(registrationDTO.getAddress())
                .latitude(registrationDTO.getLatitude())   // DTO에 추가 필요
                .longitude(registrationDTO.getLongitude()) // DTO에 추가 필요
                .businessTime(registrationDTO.getBusinessTime())
                .imageUrl(registrationDTO.getImageUrl())
                .build();

        shopRepository.save(shop);
        return updatedUser;
    }
}