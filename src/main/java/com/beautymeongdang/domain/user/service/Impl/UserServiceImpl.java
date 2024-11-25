package com.beautymeongdang.domain.user.service.Impl;

import com.beautymeongdang.domain.shop.dto.ShopDTO;
import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.user.dto.CustomerDTO;
import com.beautymeongdang.domain.user.dto.GroomerDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    }

    @Override
    public User registerGroomer(String username, GroomerDTO groomerDTO, ShopDTO shopDTO) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User updatedUser = User.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role("ROLE_STYLIST")
                .socialProvider(user.getSocialProvider())
                .profileImage(user.getProfileImage())
                .phone(groomerDTO.getPhone())
                .build();

        updatedUser = userRepository.save(updatedUser);

        // Groomer 정보 저장
        Groomer groomer = Groomer.builder()
                .userId(updatedUser)
                .skill(null)  // skill 필드가 GroomerDTO에 없음
                .build();

        groomer = groomerRepository.save(groomer);

        // Shop 정보 저장
        Sigungu sigungu = sigunguRepository.findById(shopDTO.getSigunguId())
                .orElseThrow(() -> new RuntimeException("Invalid sigungu id"));

        Shop shop = Shop.builder()
                .groomerId(groomer)
                .sigunguId(sigungu)
                .shopName(shopDTO.getShopName())
                .description(shopDTO.getDescription())
                .address(shopDTO.getAddress())
                .latitude(shopDTO.getLatitude())
                .longitude(shopDTO.getLongitude())
                .businessTime(shopDTO.getBusinessTime())
                .imageUrl(shopDTO.getImageUrl())
                .build();

        shopRepository.save(shop);
        return updatedUser;
    }

}