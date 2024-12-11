package com.beautymeongdang.global.common.repository;

import com.beautymeongdang.global.common.entity.GroupCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupCodeRepository extends JpaRepository<GroupCode, String> {
}
