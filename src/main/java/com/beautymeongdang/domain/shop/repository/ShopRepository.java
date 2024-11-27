package com.beautymeongdang.domain.shop.repository;

import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.user.entity.Groomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    Optional<Shop> findByGroomerId(Groomer groomerId);
}
