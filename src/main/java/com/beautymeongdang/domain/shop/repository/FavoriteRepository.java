package com.beautymeongdang.domain.shop.repository;

import com.beautymeongdang.domain.shop.entity.Favorite;
import com.beautymeongdang.domain.shop.entity.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    @Query("SELECT f FROM Favorite f WHERE f.favoriteId = :favoriteId")
    Optional<Favorite> findById(@Param("favoriteId") FavoriteId favoriteId);

    //내가 찜을 눌렀는지 안 눌렀는지
    @Query("SELECT COUNT(f) > 0 FROM Favorite f " +
            "WHERE f.favoriteId.shopId.shopId = :shopId " +
            "AND f.favoriteId.customerId.customerId = :customerId ")
    Boolean existsByShopIdAndCustomerId(@Param("shopId") Long shopId, @Param("customerId") Long customerId);

    // 찜한 매장 리스트 조회
    @Query("""
    SELECT f 
    FROM Favorite f 
    JOIN f.favoriteId.shopId s 
    WHERE f.favoriteId.customerId.customerId = :customerId 
    AND s.isDeleted = false
""")
    List<Favorite> findByFavoriteIdCustomerId(@Param("customerId") Long customerId);

}