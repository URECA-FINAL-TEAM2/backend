package com.beautymeongdang.global.common.scheduler.user;

import com.beautymeongdang.domain.chat.entity.Chat;
import com.beautymeongdang.domain.chat.entity.ChatMessage;
import com.beautymeongdang.domain.chat.entity.ChatMessageImage;
import com.beautymeongdang.domain.chat.repository.ChatMessageImageRepository;
import com.beautymeongdang.domain.chat.repository.ChatMessageRepository;
import com.beautymeongdang.domain.chat.repository.ChatRepository;
import com.beautymeongdang.domain.payment.entity.Payment;
import com.beautymeongdang.domain.payment.repository.PaymentRepository;
import com.beautymeongdang.domain.quote.entity.*;
import com.beautymeongdang.domain.quote.repository.*;
import com.beautymeongdang.domain.review.entity.Recommend;
import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.review.entity.ReviewsImage;
import com.beautymeongdang.domain.review.repository.RecommendRepository;
import com.beautymeongdang.domain.review.repository.ReviewRepository;
import com.beautymeongdang.domain.review.repository.ReviewsImageRepository;
import com.beautymeongdang.domain.shop.entity.Favorite;
import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.shop.repository.FavoriteRepository;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.GroomerPortfolioImage;
import com.beautymeongdang.domain.user.repository.GroomerPortfolioImageRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserScheduledService {
    private final GroomerRepository groomerRepository;
    private final ShopRepository shopRepository;
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageImageRepository chatMessageImageRepository;
    private final FavoriteRepository favoriteRepository;
    private final GroomerPortfolioImageRepository groomerPortfolioImageRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewsImageRepository reviewsImageRepository;
    private final RecommendRepository recommendRepository;
    private final QuoteRepository quoteRepository;
    private final SelectedQuoteRepository selectedQuoteRepository;
    private final PaymentRepository paymentRepository;
    private final QuoteRequestRepository quoteRequestRepository;
    private final TotalQuoteRequestRepository totalQuoteRequestRepository;
    private final DirectQuoteRequestRepository directQuoteRequestRepository;
    private final QuoteRequestImageRepository quoteRequestImageRepository;

    // 미용사 프로필 물리적 삭제 스케줄러
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteGroomerProfile() {
        List<Groomer> groomers = groomerRepository.findAllByIsDeletedAndAndUpdatedAt(LocalDateTime.now().minusDays(30));

        groomers.forEach(groomer -> {
            // 매장 삭제
            Optional<Shop> shop = shopRepository.findByGroomerId(groomer.getGroomerId());
            shop.ifPresent(shop1 -> {
                List<Favorite> favorites = favoriteRepository.findByFavoriteIdShopId(shop1);
                favoriteRepository.deleteAll(favorites); // 매장 찜
                shopRepository.delete(shop1); // 매장
            });

            // 채팅방, 채팅 메시지, 채팅 메시지 이미지 삭제
            List<Chat> chats = chatRepository.findAllByGroomerId(groomer);
            chats.forEach(chat -> {
                List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatId(chat);
                chatMessages.forEach(chatMessage -> {
                    List<ChatMessageImage> chatMessageImages = chatMessageImageRepository.findAllByMessageId(chatMessage);
                    chatMessageImageRepository.deleteAll(chatMessageImages); // 채팅 메시지 이미지
                    chatMessageRepository.delete(chatMessage); // 채팅 메시지
                });
                chatRepository.delete(chat); // 채팅방
            });

            // 미용사 포트폴리오 이미지 삭제
            List<GroomerPortfolioImage> groomerPortfolioImages = groomerPortfolioImageRepository.findAllByGroomerId(groomer);
            groomerPortfolioImageRepository.deleteAll(groomerPortfolioImages);

            // 리뷰, 리뷰 이미지, 리뷰 추천 삭제
            List<Reviews> reviews = reviewRepository.findAllByGroomerId(groomer);
            reviews.forEach(review -> {
                List<ReviewsImage> reviewsImages = reviewsImageRepository.findAllByReviewId(review);
                reviewsImageRepository.deleteAll(reviewsImages); // 리뷰 이미지
                List<Recommend> recommends = recommendRepository.findAllByReviewId(review);
                recommendRepository.deleteAll(recommends); // 리뷰 추천

                reviewRepository.delete(review); // 리뷰
            });

            // 견적서 시스템 삭제 (갼적서 요청, 견적서, 고객이 선택한 견적서, 결제)
            List<Quote> quotes = quoteRepository.findAllByGroomerId(groomer);

            quotes.forEach(quote -> {
                SelectedQuote selectedQuote = selectedQuoteRepository.findByQuoteId(quote);

                if (selectedQuote != null) {
                    Payment payment = paymentRepository.findBySelectedQuoteId(selectedQuote);
                    if (payment != null) {
                        paymentRepository.delete(payment); // 결제
                    }

                    selectedQuoteRepository.delete(selectedQuote); // 고객이 선택한 견적서

                    QuoteRequest quoteRequest = quoteRequestRepository.findById(quote.getRequestId().getRequestId())
                            .orElseThrow(() -> NotFoundException.entityNotFound("견적서 요청"));

                    quoteRepository.delete(quote); // 견적서

                    // 직접 견적 요청 or 전체 견적 요청 삭제
                    if("010".equals(quoteRequest.getRequestType())) {
                        totalQuoteRequestRepository.deleteByRequestId(quoteRequest); // 전체 견적서 요청
                    } else {
                        DirectQuoteRequestId directQuoteRequestId = DirectQuoteRequestId.builder()
                                .requestId(quoteRequest)
                                .groomerId(groomer)
                                .build();
                        directQuoteRequestRepository.deleteById(directQuoteRequestId); // 직접 견적서 요청
                    }

                    // 견적서 요청 이미지 삭제
                    quoteRequestImageRepository.deleteAllByRequestId(quoteRequest);

                    // 견적서 요청 삭제
                    quoteRequestRepository.delete(quoteRequest);

                }

                quoteRepository.delete(quote); // 견적서
            });

            // 1:1 견적서 요청 삭제
            List<DirectQuoteRequest> directQuoteRequests = directQuoteRequestRepository.findAllByDirectQuoteRequestIdGroomerId(groomer);
            directQuoteRequests.forEach(directQuoteRequest -> System.out.println(directQuoteRequest.getDirectQuoteRequestId().getRequestId()));

            directQuoteRequests.forEach(directQuoteRequest -> {
                QuoteRequest quoteRequest = quoteRequestRepository.findById(directQuoteRequest.getDirectQuoteRequestId().getRequestId().getRequestId())
                        .orElseThrow(() -> NotFoundException.entityNotFound("견적서 요청"));

                directQuoteRequestRepository.delete(directQuoteRequest); // 직접 견적서 요청
                List<QuoteRequestImage> quoteRequestImages = quoteRequestImageRepository.findAllByRequestId(quoteRequest.getRequestId());
                quoteRequestImageRepository.deleteAll(quoteRequestImages); // 견적서 요청 이미지
                quoteRequestRepository.delete(quoteRequest); // 견적서 요청
            });

            groomerRepository.delete(groomer); // 미용사
        });

    }

}
