package com.beautymeongdang.domain.user.repository;

import com.beautymeongdang.domain.user.dto.GetGroomerProfileResponseDto;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GroomerRepository extends JpaRepository<Groomer, Long> {
    boolean existsByUserId(User user);

    // 미용사 정보 조회
    @Query("SELECT new com.beautymeongdang.domain.user.dto.GetGroomerProfileResponseDto( " +
            "g.groomerId, u.profileImage, u.userName, u.email, u.nickname, u.phone, g.skill) " +
            "FROM Groomer g JOIN g.userId u " +
            "WHERE g.groomerId = :groomerId")
    GetGroomerProfileResponseDto findGroomerInfoById(@Param("groomerId") Long groomerId);
  
    // 미용사 토글
    Optional<Groomer> findByUserId(User user);
  
    @Query("SELECT g.groomerId FROM Groomer g WHERE g.userId = :user AND g.isDeleted = false")
    Optional<Long> findGroomerIdByUserId(@Param("user") User user);

    // 삭제되지 않은(isDeleted가 false인) 데이터가 존재하는지 확인
    boolean existsByUserIdAndIsDeletedFalse(User userId);

    // 미용사 프로필 삭제 스케줄러
    @Query("""
    SELECT g
    FROM Groomer g
    WHERE g.isDeleted = true
      AND g.updatedAt < :deleteDay
    """)
    List<Groomer> findAllByIsDeletedAndAndUpdatedAt(@Param("deleteDay") LocalDateTime deleteDay);
}
