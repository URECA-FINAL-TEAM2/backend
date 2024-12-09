package com.beautymeongdang.domain.user.service.Impl;

import com.beautymeongdang.domain.chat.entity.Chat;
import com.beautymeongdang.domain.chat.entity.ChatMessage;
import com.beautymeongdang.domain.chat.repository.ChatMessageRepository;
import com.beautymeongdang.domain.chat.repository.ChatRepository;
import com.beautymeongdang.domain.payment.entity.Payment;
import com.beautymeongdang.domain.payment.repository.PaymentRepository;
import com.beautymeongdang.domain.quote.entity.Quote;
import com.beautymeongdang.domain.quote.entity.QuoteRequest;
import com.beautymeongdang.domain.quote.entity.SelectedQuote;
import com.beautymeongdang.domain.quote.repository.QuoteRepository;
import com.beautymeongdang.domain.quote.repository.QuoteRequestRepository;
import com.beautymeongdang.domain.quote.repository.SelectedQuoteRepository;
import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.review.repository.ReviewRepository;
import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.user.dto.DeleteGroomerProfileResponseDto;
import com.beautymeongdang.domain.user.dto.UpdateGroomerPortfolioDto;
import com.beautymeongdang.domain.user.dto.GetGroomerProfileResponseDto;
import com.beautymeongdang.domain.user.dto.UpdateGroomerProfileDto;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.GroomerPortfolioImage;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.GroomerPortfolioImageRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.domain.user.service.GroomerService;
import com.beautymeongdang.global.common.entity.DeletableBaseTimeEntity;
import com.beautymeongdang.global.common.entity.UploadedFile;
import com.beautymeongdang.global.exception.handler.BadRequestException;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import com.beautymeongdang.infra.s3.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroomerServiceImpl implements GroomerService {
    private final GroomerRepository groomerRepository;
    private final GroomerPortfolioImageRepository groomerPortfolioImageRepository;
    private final FileStore fileStore;
    private final SelectedQuoteRepository selectedQuoteRepository;
    private final ShopRepository shopRepository;
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final QuoteRepository quoteRepository;
    private final PaymentRepository paymentRepository;
    private final QuoteRequestRepository quoteRequestRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    // 미용사 정보 조회
    @Override
    public GetGroomerProfileResponseDto getGroomerProfile(Long groomerId) {
        return groomerRepository.findGroomerInfoById(groomerId);
    }

    // 미용사 포트폴리오 수정
    @Override
    @Transactional
    public UpdateGroomerPortfolioDto updateGroomerPortfolio(UpdateGroomerPortfolioDto updateGroomerPortfolioDto, List<MultipartFile> images) {
        if(images!= null && images.size() > 9) {
            throw new BadRequestException("등록 가능한 포트폴리오 이미지 수를 초과하였습니다.");
        }

        Groomer groomer = groomerRepository.findById(updateGroomerPortfolioDto.getGroomerId())
                .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));

        List<GroomerPortfolioImage> groomerPortfolioImages = groomerPortfolioImageRepository.findAllByGroomerId(groomer);

        // 이미지 S3 삭제
        for (GroomerPortfolioImage groomerImage: groomerPortfolioImages) {
            fileStore.deleteFile(groomerImage.getImageUrl());
        }

        // 이미지 DB 삭제
        groomerPortfolioImageRepository.deleteAllByGroomerId(groomer);

        // 이미지 저장
        List<GroomerPortfolioImage> savedImages = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            List<UploadedFile> uploadedFiles = fileStore.storeFiles(images, FileStore.GROOMER_PORTFOLIO);

            List<GroomerPortfolioImage> groomerPortfolioImageList = uploadedFiles.stream()
                    .map(uploadedFile -> GroomerPortfolioImage.builder()
                            .groomerId(groomer)
                            .imageUrl(uploadedFile.getFileUrl())
                            .build())
                    .collect(Collectors.toList());

            savedImages = groomerPortfolioImageRepository.saveAll(groomerPortfolioImageList);
        }

        return UpdateGroomerPortfolioDto.builder()
                .groomerId(groomer.getGroomerId())
                .images(savedImages.stream()
                        .map(GroomerPortfolioImage::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }

    // 미용사 프로필 논리적 삭제
    @Override
    @Transactional
    public DeleteGroomerProfileResponseDto deleteGroomerProfile(Long groomerId) {
        Integer isConfirmedReservations = selectedQuoteRepository.countConfirmedReservations(groomerId);
        if (isConfirmedReservations > 0) {
            throw new BadRequestException("등록된 예약이 있습니다. 예약 취소 후 다시 시도해 주세요.");
        }

        // 미용사
        Groomer groomer = groomerRepository.findById(groomerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));
        groomer.delete();

        // 매장
        Optional<Shop> shop = shopRepository.findByGroomerId(groomerId);
        if (shop.isPresent()) {
            shop.get().delete();
        }

        // 채팅방, 채팅 메시지
        List<Chat> chats = chatRepository.findAllByGroomerId(groomer);
        chats.forEach(chat -> {
            List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatId(chat);
            chatMessages.forEach(DeletableBaseTimeEntity::delete);
            chat.delete();
        });

        // 견적 시스템
        List<Quote> quotes = quoteRepository.findAllByGroomerId(groomer);

        quotes.forEach(quote -> {
            SelectedQuote selectedQuote = selectedQuoteRepository.findByQuoteId(quote);

            if (selectedQuote != null) { // 견적서가 선택된 경우
                Payment payment = paymentRepository.findBySelectedQuoteId(selectedQuote);
                payment.delete(); // 결제 삭제

                QuoteRequest quoteRequest = quoteRequestRepository.findById(quote.getRequestId().getRequestId())
                        .orElseThrow(() -> NotFoundException.entityNotFound("견적서 요청"));
                quoteRequest.delete(); // 요청 삭제

                selectedQuote.delete(); // 예약 삭제
            }

            quote.delete(); // 견적서 삭제
        });

        // 1:1 견적서 요청 삭제
        List<QuoteRequest> quoteRequests = quoteRequestRepository.findAllByRequestType("020");
        quoteRequests.forEach(DeletableBaseTimeEntity::delete);

        // 리뷰
        List<Reviews> reviews = reviewRepository.findAllByGroomerId(groomer);
        reviews.forEach(DeletableBaseTimeEntity::delete);

        return new DeleteGroomerProfileResponseDto(groomer.getGroomerId());
    }

    // 미용사 프로필 수정
    @Override
    @Transactional
    public UpdateGroomerProfileDto updateGroomerProfile(UpdateGroomerProfileDto updateGroomerProfileDto, List<MultipartFile> images) {
        if (images != null && images.size() > 1) {
            throw new BadRequestException("프로필 이미지는 1장만 등록할 수 있습니다");
        }

        // 미용사 스킬 수정
        Groomer groomer = groomerRepository.findById(updateGroomerProfileDto.getGroomerId())
                .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));

        Groomer updateGroomer = Groomer.builder()
                .groomerId(groomer.getGroomerId())
                .userId(groomer.getUserId())
                .skill(updateGroomerProfileDto.getSkills())
                .build();

        Groomer saveGroomer = groomerRepository.save(updateGroomer);

        // 회원 수정
        User user = userRepository.findById(groomer.getUserId().getUserId())
                .orElseThrow(() -> NotFoundException.entityNotFound("회원"));

        // S3 회원 이미지 수정
        String groomerProfileUrl = user.getProfileImage();
        if(images != null && !images.isEmpty()) {
            // S3 이미지 삭제
            fileStore.deleteFile(user.getProfileImage());

            // S3 이미지 등록
            List<UploadedFile> uploadedFiles = fileStore.storeFiles(images, FileStore.USER_PROFILE);
            groomerProfileUrl = uploadedFiles.get(0).getFileUrl();

        }

        User updateUser = User.builder()
                .userId(user.getUserId())
                .roles(user.getRoles())
                .userName(user.getUserName())
                .email(user.getEmail())
                .nickname(updateGroomerProfileDto.getNickname())
                .socialProvider(user.getSocialProvider())
                .profileImage(groomerProfileUrl)
                .phone(updateGroomerProfileDto.getPhone())
                .isRegister(user.isRegister())
                .providerId(user.getProviderId())
                .build();

        User saveUser = userRepository.save(updateUser);

        return UpdateGroomerProfileDto.builder()
                .groomerId(saveGroomer.getGroomerId())
                .profileImage(saveUser.getProfileImage())
                .nickname(saveUser.getNickname())
                .phone(saveUser.getPhone())
                .skills(saveGroomer.getSkill())
                .build();
    }
}
