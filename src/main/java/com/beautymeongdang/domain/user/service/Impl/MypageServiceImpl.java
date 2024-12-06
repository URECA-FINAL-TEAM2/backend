package com.beautymeongdang.domain.user.service.Impl;

import com.beautymeongdang.domain.quote.repository.SelectedQuoteRepository;
import com.beautymeongdang.domain.review.repository.ReviewRepository;
import com.beautymeongdang.domain.user.dto.GetGroomerMypageResponseDto;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.domain.user.service.MypageService;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageServiceImpl implements MypageService {
    private final GroomerRepository groomerRepository;
    private final UserRepository userRepository;
    private final SelectedQuoteRepository selectedQuoteRepository;
    private final ReviewRepository reviewRepository;

    // 미용사 마이페이지 조회
    @Override
    public GetGroomerMypageResponseDto getGroomerMypage(Long groomerId) {
        Groomer groomer = groomerRepository.findById(groomerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));

        User user = userRepository.findById(groomer.getUserId().getUserId())
                .orElseThrow(() -> NotFoundException.entityNotFound("회원"));

        // 미용완료 건수
        Integer completedServices = selectedQuoteRepository.countCompletedServices(groomerId);

        // 확정된 예약 건수
        Integer confirmedReservations = selectedQuoteRepository.countConfirmedReservations(groomerId);

        // 미용사 리뷰 건수
        Integer myReviews = reviewRepository.countGroomerReviews(groomerId);

        GetGroomerMypageResponseDto.GroomerMypageCountsDto groomerMypageCountsDto = GetGroomerMypageResponseDto.GroomerMypageCountsDto.builder()
                .completedServices(completedServices)
                .confirmedReservations(confirmedReservations)
                .myReviews(myReviews)
                .build();

        return GetGroomerMypageResponseDto.builder()
                .groomerId(groomer.getGroomerId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .phoneNumber(user.getPhone())
                .profileImage(user.getProfileImage())
                .counts(groomerMypageCountsDto)
                .build();
    }

}
