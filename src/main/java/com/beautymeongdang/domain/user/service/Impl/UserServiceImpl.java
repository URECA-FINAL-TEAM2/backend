package com.beautymeongdang.domain.user.service.Impl;

import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.user.dto.CustomerDTO;
import com.beautymeongdang.domain.user.dto.GroomerRegistrationDTO;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.Role;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.domain.user.repository.RoleRepository;
import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.domain.user.service.UserService;
import com.beautymeongdang.global.common.repository.CommonCodeRepository;
import com.beautymeongdang.global.region.entity.Sigungu;
import com.beautymeongdang.global.region.repository.SigunguRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    private final CommonCodeRepository commonCodeRepository;
    private final RoleRepository roleRepository;

    @Override
    public User registerCustomer(Long userId, CustomerDTO customerDTO) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

            Role customerRole = roleRepository.findByName("고객")
                    .orElseThrow(() -> new EntityNotFoundException("CUSTOMER role not found"));

            addRoleToUser(user, customerRole);
            user.updateUserInfo(customerDTO.getPhone(), user.getNickname());

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
    public User registerGroomer(Long userId, GroomerRegistrationDTO registrationDTO) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

            Role groomerRole = roleRepository.findByName("미용사")
                    .orElseThrow(() -> new EntityNotFoundException("GROOMER role not found"));

            addRoleToUser(user, groomerRole);
            user.updateUserInfo(registrationDTO.getPhone(), user.getNickname());

            userRepository.save(user);

            Sigungu sigungu = sigunguRepository.findById(registrationDTO.getSigunguId())
                    .orElseThrow(() -> new EntityNotFoundException("Sigungu not found with id: " + registrationDTO.getSigunguId()));

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

    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    public void addRoleToUser(User user, Role role) {
        user.addRole(role);
        userRepository.save(user);
    }

    public void removeRoleFromUser(User user, Role role) {
        user.removeRole(role);
        userRepository.save(user);
    }
}