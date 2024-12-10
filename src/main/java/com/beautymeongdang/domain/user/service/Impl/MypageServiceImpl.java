package com.beautymeongdang.domain.user.service.Impl;

import com.beautymeongdang.domain.chat.repository.ChatRepository;
import com.beautymeongdang.domain.dog.repository.DogRepository;
import com.beautymeongdang.domain.quote.repository.SelectedQuoteRepository;
import com.beautymeongdang.domain.review.repository.ReviewRepository;
import com.beautymeongdang.domain.user.dto.GetCustomerMypageResponseDto;
import com.beautymeongdang.domain.user.dto.GetGroomerMypageResponseDto;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.domain.user.service.MypageService;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageServiceImpl implements MypageService {
    private final GroomerRepository groomerRepository;
    private final UserRepository userRepository;
    private final SelectedQuoteRepository selectedQuoteRepository;
    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final DogRepository dogRepository;

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
    // 고객 마이페이지 조회
    @Override
    public GetCustomerMypageResponseDto getCustomerMypage(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));

        // 사용자 정보
        GetCustomerMypageResponseDto.UserInfoDto userInfo = GetCustomerMypageResponseDto.UserInfoDto.builder()
                .userName(customer.getUserId().getUserName())
                .email(customer.getUserId().getEmail())
                .profileImage(customer.getUserId().getProfileImage())
                .build();

        // 통계 정보 조회
        GetCustomerMypageResponseDto.CustomrtMypageCountsDto counts = GetCustomerMypageResponseDto.CustomrtMypageCountsDto.builder()
                .completedServices(selectedQuoteRepository.countCompletedServicesByCustomerId(customerId))
                .confirmedReservations(selectedQuoteRepository.countConfirmedReservationsByCustomerId(customerId))
                .myReviews(reviewRepository.countByCustomerId(customerId))
                .build();

        // 반려동물 정보 조회
        List<GetCustomerMypageResponseDto.PetDto> myPets = dogRepository.findAllByCustomerId(customerId)
                .stream()
                .map(dog -> GetCustomerMypageResponseDto.PetDto.builder()
                        .petId(dog.getDogId())
                        .petName(dog.getDogName())
                        .profileImage(dog.getProfileImage())
                        .build())
                .collect(Collectors.toList());

        // Builder를 사용하여 응답 DTO 생성
        return GetCustomerMypageResponseDto.builder()
                .userInfo(userInfo)
                .counts(counts)
                .myPets(myPets)
                .build();
    }

}
