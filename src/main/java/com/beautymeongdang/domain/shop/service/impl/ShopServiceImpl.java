package com.beautymeongdang.domain.shop.service.impl;

import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.review.entity.ReviewsImage;
import com.beautymeongdang.domain.review.repository.RecommendRepository;
import com.beautymeongdang.domain.review.repository.ReviewRepository;
import com.beautymeongdang.domain.review.repository.ReviewsImageRepository;
import com.beautymeongdang.domain.shop.dto.*;

import static com.beautymeongdang.domain.shop.dto.GetGroomerShopListResponseDto.ShopDto;

import com.beautymeongdang.domain.shop.entity.Favorite;
import com.beautymeongdang.domain.shop.entity.FavoriteId;
import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.shop.repository.FavoriteRepository;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.shop.service.ShopService;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.repository.GroomerPortfolioImageRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.global.common.entity.UploadedFile;
import com.beautymeongdang.global.exception.handler.BadRequestException;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import com.beautymeongdang.global.region.entity.Sigungu;
import com.beautymeongdang.global.region.repository.SigunguRepository;
import com.beautymeongdang.infra.s3.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopServiceImpl implements ShopService {
    private final ReviewRepository reviewRepository;
    private final ReviewsImageRepository reviewsImageRepository;
    private final GroomerRepository groomerRepository;
    private final GroomerPortfolioImageRepository groomerPortfolioImageRepository;
    private final ShopRepository shopRepository;
    private final RecommendRepository recommendRepository;
    private final CustomerRepository customerRepository;
    private final SigunguRepository sigunguRepository;
    private final FileStore fileStore;
    private final FavoriteRepository favoriteRepository;

    /**
     * 매장 등록
     */
    @Override
    @Transactional
    public CreateShopResponseDto createShop(Long groomerId, CreateShopRequestDto requestDto, MultipartFile shopLogo) {
        List<UploadedFile> uploadedFiles = fileStore.storeFiles(List.of(shopLogo), FileStore.SHOP_LOGO);
        String LogoUrl = uploadedFiles.get(0).getFileUrl();

        Groomer groomer = groomerRepository.findById(groomerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));

        Sigungu sigungu = sigunguRepository.findBySidoId_SidoNameAndSigunguName(
                        requestDto.getSidoName(),
                        requestDto.getSigunguName())
                .orElseThrow(() -> NotFoundException.entityNotFound("시군구"));

        Shop shop = Shop.builder()
                .groomerId(groomer)
                .sigunguId(sigungu)
                .shopName(requestDto.getShopName())
                .description(requestDto.getDescription())
                .address(requestDto.getAddress())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .businessTime(requestDto.getBusinessTime())
                .imageUrl(LogoUrl)
                .build();

        Shop savedShop = shopRepository.save(shop);

        return CreateShopResponseDto.builder()
                .shopId(savedShop.getShopId())
                .shopName(savedShop.getShopName())
                .description(savedShop.getDescription())
                .businessTime(savedShop.getBusinessTime())
                .sidoName(sigungu.getSidoId().getSidoName())
                .sigunguName(sigungu.getSigunguName())
                .address(savedShop.getAddress())
                .latitude(savedShop.getLatitude())
                .longitude(savedShop.getLongitude())
                .shopLogo(savedShop.getImageUrl())
                .build();
    }



    /**
     * 매장 조회 (미용사 마이 페이지)
     */
    @Override
    public GetShopResponseDto getGroomerShop(Long groomerId) {
        Shop shop = shopRepository.findByGroomerId(groomerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("등록된 매장"));

        Integer favoriteCount = shopRepository.countFavoritesByShop(shop);
        Integer reviewCount = reviewRepository.countGroomerReviews(groomerId);
        List<String> portfolioImages = groomerPortfolioImageRepository.findImageUrlsByGroomerId(groomerId);

        return GetShopResponseDto.builder()
                .shopId(shop.getShopId())
                .shopName(shop.getShopName())
                .description(shop.getDescription())
                .businessTime(shop.getBusinessTime())
                .sidoName(shop.getSigunguId().getSidoId().getSidoName())
                .sigunguName(shop.getSigunguId().getSigunguName())
                .address(shop.getAddress())
                .shopLogo(shop.getImageUrl())
                .favoriteCount(favoriteCount)
                .reviewCount(reviewCount)
                .groomerPortfolioImages(portfolioImages)
                .build();
    }


    /**
     * 매장 수정
     */
    @Override
    @Transactional
    public UpdateShopResponseDto updateShop(Long shopId, Long groomerId, UpdateShopRequestDto requestDto, MultipartFile shopLogo) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> NotFoundException.entityNotFound("매장"));

        if (!shop.getGroomerId().getGroomerId().equals(groomerId)) {
            throw BadRequestException.invalidRequest("매장 수정 권한");
        }

        Sigungu sigungu = sigunguRepository.findBySidoId_SidoNameAndSigunguName(
                        requestDto.getSidoName(),
                        requestDto.getSigunguName())
                .orElseThrow(() -> NotFoundException.entityNotFound("시군구"));

        String logoUrl = shop.getImageUrl();
        if (shopLogo != null && !shopLogo.isEmpty()) {
            // 기존 이미지 삭제
            fileStore.deleteFile(logoUrl);

            // 새로운 이미지 업로드
            List<UploadedFile> uploadedFiles = fileStore.storeFiles(List.of(shopLogo), FileStore.SHOP_LOGO);
            logoUrl = uploadedFiles.get(0).getFileUrl();
        }

        shop.updateShopInfo(
                sigungu,
                requestDto.getShopName(),
                requestDto.getDescription(),
                requestDto.getAddress(),
                requestDto.getLatitude(),
                requestDto.getLongitude(),
                requestDto.getBusinessTime(),
                logoUrl
        );

        Shop updatedShop = shopRepository.save(shop);

        return UpdateShopResponseDto.builder()
                .shopId(updatedShop.getShopId())
                .shopName(updatedShop.getShopName())
                .description(updatedShop.getDescription())
                .businessTime(updatedShop.getBusinessTime())
                .sidoName(sigungu.getSidoId().getSidoName())
                .sigunguName(sigungu.getSigunguName())
                .address(updatedShop.getAddress())
                .latitude(updatedShop.getLatitude())
                .longitude(updatedShop.getLongitude())
                .shopLogo(logoUrl)
                .build();
    }



    /**
     * 매장 상세 조회 (customer)
     */
    @Override
    public GetShopDetailResponseDto getShopDetail(Long shopId, Long customerId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> NotFoundException.entityNotFound("매장"));

        Groomer groomer = shop.getGroomerId();
        Double starScoreAvg = shopRepository.getAverageStarRatingByGroomerId(groomer.getGroomerId());
        Integer starCount = reviewRepository.countGroomerReviews(groomer.getGroomerId());

        List<String> portfolioImages = groomerPortfolioImageRepository.findImageUrlsByGroomerId(groomer.getGroomerId());


        List<Long> recommendedReviewIds = (customerId != null) ?
                recommendRepository.findReviewIdsByCustomerId(customerId) :
                Collections.emptyList();

        List<Reviews> reviews = reviewRepository.findGroomerReviews(groomer.getGroomerId());
        List<GetShopDetailResponseDto.ReviewDetailDto> reviewDtos = reviews.stream()
                .map(review -> {
                    Integer recommendCount = reviewRepository.countRecommendsByReviewId(review.getReviewId());
                    List<ReviewsImage> reviewImages = reviewsImageRepository.findReviewImagesByReviewId(review.getReviewId());
                    List<String> reviewImageUrls = reviewImages.stream()
                            .map(ReviewsImage::getImageUrl)
                            .collect(Collectors.toList());

                    return GetShopDetailResponseDto.ReviewDetailDto.builder()
                            .reviewId(review.getReviewId())
                            .customerNickname(review.getCustomerId().getUserId().getNickname())
                            .starScore(review.getStarRating().doubleValue())
                            .content(review.getContent())
                            .recommendCount(recommendCount)
                            .reviewsImage(reviewImageUrls)
                            .createdAt(review.getCreatedAt())
                            .isRecommended(recommendedReviewIds.contains(review.getReviewId()))
                            .build();
                })
                .collect(Collectors.toList());

        Boolean isFavorite = favoriteRepository.existsByShopIdAndCustomerId(shopId, customerId);

        return GetShopDetailResponseDto.builder()
                .groomerId(groomer.getGroomerId())
                .shopId(shop.getShopId())
                .shopLogo(shop.getImageUrl())
                .shopName(shop.getShopName())
                .starScoreAvg(starScoreAvg)
                .starCount(starCount)
                .address(shop.getAddress())
                .businessTime(shop.getBusinessTime())
                .skills(groomer.getSkill())
                .latitude(shop.getLatitude().doubleValue())
                .longitude(shop.getLongitude().doubleValue())
                .favoriteCount(shopRepository.countFavoritesByShop(shop))
                .isFavorite(isFavorite)
                .description(shop.getDescription())
                .groomerPortfolioImages(portfolioImages)
                .groomerUsername(groomer.getUserId().getNickname())
                .groomerProfileImage(groomer.getUserId().getProfileImage())
                .reviews(reviewDtos)
                .build();
    }


    /**
     * 자기 매장 상세 조회 (groomer)
     */
    @Override
    public GetMyGroomerShopDetailResponseDto getMyShopDetail(Long groomerId) {
        Shop shop = shopRepository.findByGroomerId(groomerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("매장"));

        Groomer groomer = shop.getGroomerId();
        Double starScoreAvg = shopRepository.getAverageStarRatingByGroomerId(groomer.getGroomerId());
        Integer starCount = reviewRepository.countGroomerReviews(groomer.getGroomerId());

        List<String> portfolioImages = groomerPortfolioImageRepository.findImageUrlsByGroomerId(groomer.getGroomerId());

        List<Reviews> reviews = reviewRepository.findGroomerReviews(groomer.getGroomerId());
        List<GetMyGroomerShopDetailResponseDto.ReviewDetailDto> reviewDtos = reviews.stream()
                .map(review -> {
                    Integer recommendCount = reviewRepository.countRecommendsByReviewId(review.getReviewId());
                    List<ReviewsImage> reviewImages = reviewsImageRepository.findReviewImagesByReviewId(review.getReviewId());
                    List<String> reviewImageUrls = reviewImages.stream()
                            .map(ReviewsImage::getImageUrl)
                            .collect(Collectors.toList());

                    return GetMyGroomerShopDetailResponseDto.ReviewDetailDto.builder()
                            .reviewId(review.getReviewId())
                            .customerNickname(review.getCustomerId().getUserId().getNickname())
                            .starScore(review.getStarRating().doubleValue())
                            .content(review.getContent())
                            .recommendCount(recommendCount)
                            .reviewsImage(reviewImageUrls)
                            .createdAt(review.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());

        return GetMyGroomerShopDetailResponseDto.builder()
                .groomerId(groomer.getGroomerId())
                .shopId(shop.getShopId())
                .shopLogo(shop.getImageUrl())
                .shopName(shop.getShopName())
                .starScoreAvg(starScoreAvg)
                .starCount(starCount)
                .address(shop.getAddress())
                .businessTime(shop.getBusinessTime())
                .skills(groomer.getSkill())
                .latitude(shop.getLatitude().doubleValue())
                .longitude(shop.getLongitude().doubleValue())
                .favoriteCount(shopRepository.countFavoritesByShop(shop))
                .description(shop.getDescription())
                .groomerPortfolioImages(portfolioImages)
                .groomerUsername(groomer.getUserId().getNickname())
                .groomerProfileImage(groomer.getUserId().getProfileImage())
                .reviews(reviewDtos)
                .build();
    }



    /**
     * 매장 삭제
     */
    @Override
    @Transactional
    public DeleteShopResponseDto deleteShop(Long shopId, Long groomerId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> NotFoundException.entityNotFound("매장"));

        if (!shop.getGroomerId().getGroomerId().equals(groomerId)) {
            throw BadRequestException.invalidRequest("매장 삭제 권한");
        }

        // 해당 매장의 찜 데이터 삭제
        List<Favorite> favorites = favoriteRepository.findByFavoriteIdShopId(shop);
        favoriteRepository.deleteAll(favorites);

        shopRepository.delete(shop);

        return DeleteShopResponseDto.builder()
                .shopId(shopId)
                .shopName(shop.getShopName())
                .build();
    }

    /**
     * 미용사 찾기 매장 리스트 조회
     */
    @Override
    public GetGroomerShopListResponseDto.ShopListResponse getShopList(Long customerId) {
        List<Object[]> results = shopRepository.findShopsByCustomerSigunguWithStats(customerId);

        List<ShopDto> shopDtos = results.stream()
                .map(result -> {
                    Shop shop = (Shop) result[0];
                    Groomer groomer = (Groomer) result[1];
                    Double avgRating = ((Double) result[2]);
                    Long reviewCount = ((Long) result[3]);

                    return ShopDto.builder()
                            .groomerId(groomer.getGroomerId())
                            .shopId(shop.getShopId())
                            .shopLogo(shop.getImageUrl())
                            .shopName(shop.getShopName())
                            .starScoreAvg(avgRating)
                            .reviewCount(reviewCount.intValue())
                            .address(shop.getAddress())
                            .businessTime(shop.getBusinessTime())
                            .skills(groomer.getSkill())
                            .latitude(shop.getLatitude().doubleValue())
                            .longitude(shop.getLongitude().doubleValue())
                            .favoriteCount(shopRepository.countFavoritesByShop(shop))
                            .build();
                })
                .collect(Collectors.toList());

        return GetGroomerShopListResponseDto.ShopListResponse.builder()
                .shopLists(shopDtos)
                .build();
    }


    /**
     * 매장 찜 삭제
     */
    @Override
    @Transactional
    public DeleteFavoriteResponseDto deleteFavorite(Long customerId, Long shopId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("고객"));

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> NotFoundException.entityNotFound("매장"));

        FavoriteId favoriteId = new FavoriteId(customer, shop);

        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> NotFoundException.entityNotFound("찜"));

        favoriteRepository.delete(favorite);

        return DeleteFavoriteResponseDto.builder()
                .shopId(shopId)
                .build();
    }

    // 매장 찜 등록
    @Override
    @Transactional
    public CreateFavoriteResponseDto createFavorite(CreateFavoriteRequestDto requestDto) {
        Shop shop = shopRepository.findById(requestDto.getShopId())
                .orElseThrow(() -> NotFoundException.entityNotFound("매장"));

        Customer customer = customerRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> NotFoundException.entityNotFound("고객"));

        FavoriteId favoriteId = FavoriteId.builder()
                .shopId(shop)
                .customerId(customer)
                .build();

        if(favoriteRepository.existsById(favoriteId)) {
            throw new BadRequestException("이미 찜한 매장입니다.");
        }

        Favorite favorite = Favorite.builder()
                .favoriteId(favoriteId)
                .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);

        return CreateFavoriteResponseDto.builder()
                .shopId(savedFavorite.getFavoriteId().getShopId().getShopId())
                .build();
    }

    @Override
    public List<GetFavoriteShopListResponseDto> getFavoriteShops(Long customerId) {
        // Favorite 조회
        List<Favorite> favorites = favoriteRepository.findByFavoriteIdCustomerId(customerId);

        return favorites.stream()
                .map(favorite -> {
                    Shop shop = favorite.getFavoriteId().getShopId(); // Shop 확인
                    Groomer groomer = shop.getGroomerId(); // Groomer 확인

                    // 별점 평균 및 리뷰 개수 계산
                    Double starScoreAvg = Optional.ofNullable(reviewRepository.getAverageStarRatingByGroomerId(groomer.getGroomerId()))
                            .orElse(0.0);
                    Integer reviewCount = Optional.ofNullable(reviewRepository.countGroomerReviews(groomer.getGroomerId()))
                            .orElse(0);

                    return GetFavoriteShopListResponseDto.builder()
                            .groomerId(groomer.getGroomerId())
                            .shopId(shop.getShopId())
                            .shopLogo(shop.getImageUrl())
                            .shopName(shop.getShopName())
                            .address(shop.getAddress())
                            .businessTime(shop.getBusinessTime())
                            .skill(groomer.getSkill())
                            .starScoreAvg(starScoreAvg)
                            .reviewCount(reviewCount)
                            .build();
                })
                .collect(Collectors.toList());
    }


}