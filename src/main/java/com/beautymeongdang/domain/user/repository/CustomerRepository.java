package com.beautymeongdang.domain.user.repository;

import com.beautymeongdang.domain.user.dto.CustomerProfileResponseDto;
import com.beautymeongdang.domain.user.dto.GetCustomerAddressResponseDto;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
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

    // 고객 토글
    Optional<Customer> findByUserId(User user);

    @Query("SELECT c.customerId FROM Customer c WHERE c.userId = :user AND c.isDeleted = false")
    Optional<Long> findCustomerIdByUserId(@Param("user") User user);

    // 고객 프로필 물리적 삭제
    @Query("""
    SELECT c
    FROM Customer c
    WHERE c.isDeleted = true
      AND c.updatedAt < :deleteDay
    """)
    List<Customer> findAllByIsDeletedAndUpdatedAtBefore(@Param("deleteDay") LocalDateTime deleteDay);

    // 삭제되지 않은(isDeleted가 false인) 데이터가 존재하는지 확인
    boolean existsByUserIdAndIsDeletedFalse(User user);

    // 탈퇴 후 30일 계산
    @Query("SELECT c FROM Customer c " +
            "WHERE c.userId = :userId " +
            "AND c.isDeleted = true " +
            "AND c.updatedAt > :thirtyDaysAgo")
    Optional<Customer> findDeletedCustomerInLast30Days(
            @Param("userId") User userId,
            @Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo
    );
}
