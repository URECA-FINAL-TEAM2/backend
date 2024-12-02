package com.beautymeongdang.domain.user.repository;

import com.beautymeongdang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);
    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    Optional<User> findByIdWithRoles(@Param("userId") Long userId);
    boolean existsByNickname(String nickname);
    Optional<User> findByProviderIdAndSocialProvider(String providerId, String socialProvider);
}
