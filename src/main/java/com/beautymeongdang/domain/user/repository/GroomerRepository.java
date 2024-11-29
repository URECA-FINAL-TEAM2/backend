package com.beautymeongdang.domain.user.repository;

import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroomerRepository extends JpaRepository<Groomer, Long> {
    boolean existsByUserId(User user);
}
