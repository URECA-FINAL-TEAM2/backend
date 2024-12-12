package com.beautymeongdang.domain.user.repository;

import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.GroomerPortfolioImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroomerPortfolioImageRepository extends JpaRepository<GroomerPortfolioImage, Long> {

    //groomerId를 통해 미용사 포트폴리오 이미지 찾기
    @Query("SELECT gpi.imageUrl FROM GroomerPortfolioImage gpi WHERE gpi.groomerId.groomerId = :groomerId")
    List<String> findImageUrlsByGroomerId(@Param("groomerId") Long groomerId);

    // 미용사 포트폴리오 수정 - 미용사 포트폴리오 이미지 조회
    List<GroomerPortfolioImage> findAllByGroomerId(Groomer groomerId);

    // 미용사 포트폴리오 수정 - 미용사 포트폴리오 이미지 삭제
    void deleteAllByGroomerId(Groomer groomer);

    // 미용사 포트폴리오 수정
    void deleteAllByGroomerIdAndImageUrlIn(Groomer groomer, List<String> imagesToDelete);
}
