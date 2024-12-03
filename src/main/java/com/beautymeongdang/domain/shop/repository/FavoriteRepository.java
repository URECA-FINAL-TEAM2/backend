package com.beautymeongdang.domain.shop.repository;

import com.beautymeongdang.domain.shop.entity.Favorite;
import com.beautymeongdang.domain.shop.entity.FavoriteId;
import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.user.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {

    // 매장 찜 삭제
    Optional<Favorite> findByFavoriteId_CustomerIdAndFavoriteId_ShopId(Customer customer, Shop shop);
}