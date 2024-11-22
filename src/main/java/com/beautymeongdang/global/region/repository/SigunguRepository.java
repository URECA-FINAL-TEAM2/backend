package com.beautymeongdang.global.region.repository;

import com.beautymeongdang.global.region.entity.Sigungu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SigunguRepository extends JpaRepository<Sigungu, Long> {
}