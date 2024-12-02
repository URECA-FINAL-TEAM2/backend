package com.beautymeongdang.domain.user.service.Impl;

import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.user.dto.CustomerRegisterRequestDTO;
import com.beautymeongdang.domain.user.dto.GroomerRegisterRequestDTO;
import com.beautymeongdang.domain.user.entity.*;
import com.beautymeongdang.domain.user.repository.*;
import com.beautymeongdang.domain.user.service.UserService;
import com.beautymeongdang.global.region.entity.Sigungu;
import com.beautymeongdang.global.region.repository.SigunguRepository;
import jakarta.persistence.EntityNotFoundException;
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
    public User registerCustomer(Long userId, CustomerRegisterRequestDTO customerDTO) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

            // 이미 Customer로 등록되어 있는지 확인
            if (customerRepository.existsByUserId(user)) {
                throw new RuntimeException("User is already registered as a customer");
            }

            // 닉네임 중복 확인
            if (user.getNickname() != null && !user.getNickname().equals(customerDTO.getNickName()) &&
                    userRepository.existsByNickname(customerDTO.getNickName())) {
                throw new RuntimeException("Nickname is already in use");
            }


            user.getRoles().add(Role.고객);
            user.updateUserInfo(customerDTO.getPhone(), customerDTO.getNickName());
            user.completeRegistration();
            userRepository.save(user);

            Sigungu sigungu = sigunguRepository.findById(customerDTO.getSigunguId())
                    .orElseThrow(() -> new EntityNotFoundException("Sigungu not found with id: " + customerDTO.getSigunguId()));

            Customer customer = Customer.builder()
                    .userId(user)
                    .sigunguId(sigungu)
                    .address(customerDTO.getAddress())
                    .latitude(customerDTO.getLatitude())
                    .longitude(customerDTO.getLongitude())
                    .build();

            customerRepository.save(customer);
            return user;
        } catch (Exception e) {
            log.error("Customer registration failed: {}", e.getMessage(), e);
            throw new RuntimeException("Customer registration failed", e);
        }
    }

    @Override
    public User registerGroomer(Long userId, GroomerRegisterRequestDTO registrationDTO) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

            // 이미 Groomer로 등록되어 있는지 확인
            if (groomerRepository.existsByUserId(user)) {
                throw new RuntimeException("User is already registered as a groomer");
            }

            // 닉네임 중복 확인
            if (user.getNickname() != null && !user.getNickname().equals(registrationDTO.getNickName()) &&
                    userRepository.existsByNickname(registrationDTO.getNickName())) {
                throw new RuntimeException("Nickname is already in use");
            }


            user.getRoles().add(Role.미용사);
            user.updateUserInfo(registrationDTO.getPhone(), registrationDTO.getNickName());
            user.completeRegistration();
            userRepository.save(user);

            Sigungu sigungu = sigunguRepository.findById(registrationDTO.getSigunguId())
                    .orElseThrow(() -> new EntityNotFoundException("Sigungu not found"));

            Groomer groomer = Groomer.builder()
                    .userId(user)
                    .skill(registrationDTO.getSkill())
                    .build();

            groomerRepository.save(groomer);

            Shop shop = Shop.builder()
                    .groomerId(groomer)
                    .sigunguId(sigungu)
                    .shopName(registrationDTO.getShopName())
                    .description(registrationDTO.getDescription())
                    .address(registrationDTO.getAddress())
                    .latitude(registrationDTO.getLatitude())
                    .longitude(registrationDTO.getLongitude())
                    .businessTime(registrationDTO.getBusinessTime())
                    .imageUrl(registrationDTO.getImageUrl())
                    .build();

            shopRepository.save(shop);
            return user;
        } catch (Exception e) {
            log.error("Groomer registration failed: {}", e.getMessage(), e);
            throw new RuntimeException("Groomer registration failed", e);
        }
    }


    public void addRoleToUser(User user, Role role) {
        user.addRole(role);
        userRepository.save(user);
    }

}