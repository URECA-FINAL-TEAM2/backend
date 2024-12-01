package com.beautymeongdang.domain.shop.service.impl;

import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.review.entity.ReviewsImage;
import com.beautymeongdang.domain.review.repository.RecommendRepository;
import com.beautymeongdang.domain.review.repository.ReviewRepository;
import com.beautymeongdang.domain.review.repository.ReviewsImageRepository;
import com.beautymeongdang.domain.shop.dto.*;

import static com.beautymeongdang.domain.shop.dto.GetGroomerShopListResponseDto.ShopDto;

import com.beautymeongdang.domain.shop.entity.Favorite;
import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.shop.service.ShopService;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.repository.GroomerPortfolioImageRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.global.common.entity.UploadedFile;
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

    /**
     * 매장 등록
     */
    @Override
    @Transactional
    public CreateShopResponseDto createShop(CreateShopRequestDto requestDto, MultipartFile shopLogo) {
        List<UploadedFile> uploadedFiles = fileStore.storeFiles(List.of(shopLogo), FileStore.SHOP_LOGO);
        String LogoUrl = uploadedFiles.get(0).getFileUrl();

        Groomer groomer = groomerRepository.findById(requestDto.getGroomerId())
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
                .build();
    }

    /**
     * 매장 상세 조회
     */
    @Override
    public GetShopDetailResponseDto.ShopDetailResponseDto getShopDetail(Long groomerId, Long customerId) {
        Groomer groomer = groomerRepository.findById(groomerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));

        Shop shop = shopRepository.findByGroomerId(groomerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("매장"));

        List<String> portfolioImages = groomerPortfolioImageRepository.findImageUrlsByGroomerId(groomerId);

        List<Long> recommendedReviewIds = (customerId != null) ?
                recommendRepository.findReviewIdsByCustomerId(customerId) :
                Collections.emptyList();

        List<Reviews> reviews = reviewRepository.findGroomerReviews(groomerId);
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
                            .recommended(recommendedReviewIds.contains(review.getReviewId()))
                            .build();
                })
                .collect(Collectors.toList());

        return GetShopDetailResponseDto.ShopDetailResponseDto.builder()
                .description(shop.getDescription())
                .shopImage(shop.getImageUrl())
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
    public DeleteShopResponseDto deleteShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> NotFoundException.entityNotFound("매장"));

        // 매장에 리뷰 논리적 삭제
        List<Reviews> reviews = shopRepository.findReviewsByGroomer(shop.getGroomerId());
        reviews.forEach(Reviews::delete);

        // 매장 찜 논리적 삭제
        List<Favorite> favorites = shopRepository.findFavoritesByShop(shop);
        favorites.forEach(Favorite::delete);

        // 매장 논리적 삭제
        shop.delete();

        return DeleteShopResponseDto.builder()
                .shopId(shop.getShopId())
                .shopName(shop.getShopName())
                .build();
    }



    /**
     * 미용사 찾기 매장 리스트 조회
     */
    @Override
    public GetGroomerShopListResponseDto.ShopListResponse getShopList(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("고객"));

        List<Shop> shops = shopRepository.findShopsByCustomerSigunguOrderByStarScore(customer.getSigunguId().getSigunguId());

        List<ShopDto> shopDtos = shops.stream()
                .map(shop -> {
                    Groomer groomer = shop.getGroomerId();
                    return ShopDto.builder()
                            .groomerId(groomer.getGroomerId())
                            .shopId(shop.getShopId())
                            .shopLogo(shop.getImageUrl())
                            .shopName(shop.getShopName())
                            .starScoreAvg(shopRepository.getAverageStarRatingByGroomerId(groomer.getGroomerId()))
                            .reviewCount(reviewRepository.countGroomerReviews(groomer.getGroomerId()))
                            .address(shop.getAddress())
                            .businessTime(shop.getBusinessTime())
                            .skills(groomer.getSkill())
                            .latitude(shop.getLatitude().doubleValue())
                            .longitude(shop.getLongitude().doubleValue())
                            .favorite(shopRepository.countFavoritesByShop(shop))
                            .build();
                })
                .collect(Collectors.toList());

        return GetGroomerShopListResponseDto.ShopListResponse.builder()
                .shopLists(shopDtos)
                .build();
    }
}