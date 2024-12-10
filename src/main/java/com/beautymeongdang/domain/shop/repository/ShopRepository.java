package com.beautymeongdang.domain.shop.repository;

import com.beautymeongdang.domain.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    // grommerId를 통해 shop정보 찾기
    @Query("SELECT s FROM Shop s WHERE s.groomerId.groomerId = :groomerId AND s.isDeleted = false ")
    Optional<Shop> findByGroomerId(@Param("groomerId") Long groomerId);


    // 미용사 평균 별점
    @Query("SELECT COALESCE(AVG(r.starRating), 0) FROM Reviews r WHERE r.groomerId.groomerId = :groomerId AND r.isDeleted = false")
    Double getAverageStarRatingByGroomerId(@Param("groomerId") Long groomerId);


    // 우리동네 미용사 조회 (리뷰 많은 순, 리뷰가 같으면 별점 높은 순으로 2개만)
    @Query("""
    SELECT s FROM Shop s
    JOIN s.groomerId g
    WHERE s.sigunguId = (SELECT c.sigunguId FROM Customer c WHERE c.customerId = :customerId)
    AND s.isDeleted = false
    ORDER BY (
        SELECT COUNT(r)
        FROM Reviews r
        WHERE r.groomerId = g
        AND r.isDeleted = false
    ) DESC,
    (
        SELECT AVG(r.starRating)
        FROM Reviews r
        WHERE r.groomerId = g
        AND r.isDeleted = false
    ) DESC
    LIMIT 2
    """)
    List<Shop> findShopsByCustomerSigunguOrderByReviewCountAndStarScore(@Param("customerId") Long customerId);


    // 미용사 찾기 같은 시군구 매장 리스트 ( 별점 높은 순 )
    @Query("""
    SELECT DISTINCT s, g,
           COALESCE(AVG(r.starRating), 0) as avgRating,
           COUNT(DISTINCT r) as reviewCount
    FROM Shop s
    INNER JOIN FETCH s.groomerId g
    LEFT JOIN Reviews r ON r.groomerId = g AND r.isDeleted = false
    WHERE s.sigunguId.sigunguId = (
        SELECT c.sigunguId.sigunguId 
        FROM Customer c 
        WHERE c.customerId = :customerId
    )
    AND s.isDeleted = false
    GROUP BY s, g
    ORDER BY avgRating DESC, reviewCount DESC
    """)
    List<Object[]> findShopsByCustomerSigunguWithStats(@Param("customerId") Long customerId);


    // 매장 찜 개수
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.favoriteId.shopId = :shop")
    Integer countFavoritesByShop(@Param("shop") Shop shop);



}
