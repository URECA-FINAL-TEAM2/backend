package com.beautymeongdang.domain.dog.repository;

import com.beautymeongdang.domain.dog.entity.Dog;
import com.beautymeongdang.domain.user.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {
    List<Dog> findByCustomerIdAndIsDeletedFalseOrderByCreatedAtDesc(Customer customerId);
    Long countByCustomerIdAndIsDeletedFalse(Customer customer);

    @Query("SELECT d FROM Dog d WHERE d.customerId.customerId = :customerId AND d.isDeleted = false")
    List<Dog> findAllByCustomerId(@Param("customerId") Long customerId);


    // 반려견 프로필 물리적 삭제
    @Query("""
    SELECT d
    FROM Dog d
    WHERE d.isDeleted = true
      AND d.updatedAt < :deleteDay
    """)
    List<Dog> findAllByIsDeletedAndAndUpdatedAt(@Param("deleteDay") LocalDateTime deleteDay);
}