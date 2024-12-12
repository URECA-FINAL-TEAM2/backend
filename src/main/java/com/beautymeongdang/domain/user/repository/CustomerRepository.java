package com.beautymeongdang.domain.user.repository;

import com.beautymeongdang.domain.user.dto.CustomerProfileResponseDto;
import com.beautymeongdang.domain.user.dto.GetCustomerAddressResponseDto;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByUserId(User user);

    // 고객 프로필 조회
    @Query("""
    SELECT new com.beautymeongdang.domain.user.dto.CustomerProfileResponseDto(
        u.userName,
        u.email,
        u.nickname,
        u.phone,
        u.profileImage,
        sido.sidoId,
        sigungu.sigunguId,
        sido.sidoName,
        sigungu.sigunguName
    )
    FROM Customer c
    JOIN c.userId u
    JOIN c.sigunguId sigungu
    JOIN sigungu.sidoId sido
    WHERE c.customerId = :customerId AND c.isDeleted = false""")
    CustomerProfileResponseDto findCustomerProfileById(@Param("customerId") Long customerId);

    // 고객 주소 조회
    @Query("""
    SELECT new com.beautymeongdang.domain.user.dto.GetCustomerAddressResponseDto(
        sido.sidoId,
        sigungu.sigunguId,
        sido.sidoName,
        sigungu.sigunguName
    )
    FROM Customer c
    JOIN c.sigunguId sigungu
    JOIN sigungu.sidoId sido
    WHERE c.customerId = :customerId AND c.isDeleted = false""")
    GetCustomerAddressResponseDto findCustomerAddressById(@Param("customerId") Long customerId);

    @Query("SELECT c.customerId FROM Customer c WHERE c.userId = :user AND c.isDeleted = false")
    Optional<Long> findCustomerIdByUserId(@Param("user") User user);
}
