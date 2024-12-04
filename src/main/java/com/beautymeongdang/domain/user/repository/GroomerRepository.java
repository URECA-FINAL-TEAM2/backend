package com.beautymeongdang.domain.user.repository;

import com.beautymeongdang.domain.user.dto.GetGroomerProfileResponseDto;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroomerRepository extends JpaRepository<Groomer, Long> {
    boolean existsByUserId(User user);

    // 미용사 정보 조회
    @Query("SELECT new com.beautymeongdang.domain.user.dto.GetGroomerProfileResponseDto( " +
            "g.groomerId, u.profileImage, u.userName, u.email, u.nickname, u.phone, g.skill) " +
            "FROM Groomer g JOIN g.userId u " +
            "WHERE g.groomerId = :groomerId")
    GetGroomerProfileResponseDto findGroomerInfoById(@Param("groomerId") Long groomerId);

}
