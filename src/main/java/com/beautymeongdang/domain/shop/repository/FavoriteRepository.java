package com.beautymeongdang.domain.shop.repository;

import com.beautymeongdang.domain.shop.entity.Favorite;
import com.beautymeongdang.domain.shop.entity.FavoriteId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {

    @Query("SELECT f FROM Favorite f WHERE f.favoriteId = :favoriteId AND f.isDeleted = false")
    Optional<Favorite> findById(@Param("favoriteId") FavoriteId favoriteId);

}