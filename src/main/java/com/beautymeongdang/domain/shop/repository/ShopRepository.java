package com.beautymeongdang.domain.shop.repository;

import com.beautymeongdang.domain.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    // grommerId를 통해 shop정보 찾기
    @Query("SELECT s FROM Shop s WHERE s.groomerId.groomerId = :groomerId")
    Optional<Shop> findByGroomerId(@Param("groomerId") Long groomerId);
}
