package com.beautymeongdang.global.common.scheduler.user;

import com.beautymeongdang.domain.shop.entity.Favorite;
import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.shop.repository.FavoriteRepository;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.GroomerPortfolioImage;
import com.beautymeongdang.domain.user.repository.GroomerPortfolioImageRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.infra.s3.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserScheduledService {
    private final GroomerRepository groomerRepository;
    private final ShopRepository shopRepository;
    private final FavoriteRepository favoriteRepository;
    private final FileStore fileStore;
    private final GroomerPortfolioImageRepository groomerPortfolioImageRepository;

    // 미용사 프로필 삭제 스케줄러
    @Scheduled(cron = "0 0 1 * * *")
    public void deleteGroomerProfile() {
        List<Groomer> groomers = groomerRepository.findAllByIsDeletedAndUpdatedAt(LocalDateTime.now().minusDays(30));

        groomers.forEach(groomer -> {
            // 매장, 찜 삭제
            Shop shop = shopRepository.findByGroomerIdAndIsDeleted(groomer.getGroomerId());
            if (shop != null) {
                List<Favorite> favorites = favoriteRepository.findByFavoriteIdShopId(shop);
                favoriteRepository.deleteAll(favorites); // 매장 찜

                if (shop.getImageUrl() != null) {
                    fileStore.deleteFile(shop.getImageUrl());
                }

                shopRepository.delete(shop);
            }

            // 미용사 포트폴리오 이미지 삭제
            List<GroomerPortfolioImage> groomerPortfolioImages = groomerPortfolioImageRepository.findAllByGroomerId(groomer);
            groomerPortfolioImages.forEach(groomerPortfolioImage -> {
                fileStore.deleteFile(groomerPortfolioImage.getImageUrl());
            });
            groomerPortfolioImageRepository.deleteAll(groomerPortfolioImages);

            groomerRepository.delete(groomer);
        });

    }
}
