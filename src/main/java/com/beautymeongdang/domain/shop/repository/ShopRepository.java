package com.beautymeongdang.domain.shop.repository;

import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.shop.entity.Favorite;
import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.user.entity.Groomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    // grommerId를 통해 shop정보 찾기
    @Query("SELECT s FROM Shop s WHERE s.groomerId.groomerId = :groomerId")
    Optional<Shop> findByGroomerId(@Param("groomerId") Long groomerId);


    // 미용사 평균 별점
    @Query("SELECT COALESCE(AVG(r.starRating), 0) FROM Reviews r WHERE r.groomerId.groomerId = :groomerId AND r.isDeleted = false")
    Double getAverageStarRatingByGroomerId(@Param("groomerId") Long groomerId);


    // 우리동네 미용사 조회 (일단 최신순으로 두 개만 받아오기?)
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
    """)
    List<Shop> findShopsByCustomerSigunguOrderByReviewCountAndStarScore(@Param("customerId") Long customerId);


    // 미용사 찾기 같은 시군구 매장 리스트 ( 별점 높은 순 )
    @Query("""
    SELECT s FROM Shop s
    JOIN s.groomerId g
    WHERE s.sigunguId = (SELECT c.sigunguId FROM Customer c WHERE c.customerId = :customerId)
    AND s.isDeleted = false
    ORDER BY (SELECT AVG(r.starRating) FROM Reviews r
             WHERE r.groomerId = g
             AND r.isDeleted = false) DESC
    """)
    List<Shop> findShopsByCustomerSigunguOrderByStarScore(@Param("customerId") Long customerId);

    // 매장 찜 개수
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.favoriteId.shopId = :shop")
    Integer countFavoritesByShop(@Param("shop") Shop shop);


    //매장 삭제 할 떄 관련된 정보들 논리적 삭제
    @Query("SELECT s FROM Shop s WHERE s.shopId = :shopId AND s.isDeleted = false")
    Optional<Shop> findById(@Param("shopId") Long shopId);

    @Query("SELECT r FROM Reviews r WHERE r.groomerId = :groomer AND r.isDeleted = false")
    List<Reviews> findReviewsByGroomer(@Param("groomer") Groomer groomer);

    @Query("SELECT f FROM Favorite f WHERE f.favoriteId.shopId = :shop")
    List<Favorite> findFavoritesByShop(@Param("shop") Shop shop);


}
