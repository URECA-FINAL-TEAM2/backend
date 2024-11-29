package com.beautymeongdang.global.region.repository;

import com.beautymeongdang.global.region.entity.Sigungu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SigunguRepository extends JpaRepository<Sigungu, Long> {
    List<Sigungu> findBySidoId_SidoId(Long sidoId);
}