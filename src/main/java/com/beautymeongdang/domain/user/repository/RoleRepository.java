package com.beautymeongdang.domain.user.repository;

import com.beautymeongdang.domain.user.entity.Role;
import com.beautymeongdang.global.common.entity.CommonCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}