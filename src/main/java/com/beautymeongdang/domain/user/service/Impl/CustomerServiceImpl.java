package com.beautymeongdang.domain.user.service.Impl;

import com.beautymeongdang.domain.chat.entity.Chat;
import com.beautymeongdang.domain.chat.repository.ChatRepository;
import com.beautymeongdang.domain.dog.entity.Dog;
import com.beautymeongdang.domain.dog.repository.DogRepository;
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
import com.beautymeongdang.domain.user.dto.CustomerProfileResponseDto;
import com.beautymeongdang.domain.user.dto.GetCustomerMypageResponseDto;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.repository.DeleteCustomerResponseDto;
import com.beautymeongdang.domain.user.service.CustomerService;
import com.beautymeongdang.global.exception.handler.BadRequestException;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import com.beautymeongdang.global.region.entity.Sigungu;
import com.beautymeongdang.global.region.repository.SigunguRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final SigunguRepository sigunguRepository;
    private final DogRepository dogRepository;
    private final QuoteRepository quoteRepository;
    private final SelectedQuoteRepository selectedQuoteRepository;
    private final ReviewRepository reviewRepository;
    private final PaymentRepository paymentRepository;
    private final ChatRepository chatRepository;
    private final QuoteRequestRepository quoteRequestRepository;

    // 고객 프로필 조회
    @Override
    @Transactional
    public CustomerProfileResponseDto getCustomerProfile(Long customerId) {
        return customerRepository.findCustomerProfileById(customerId);
    }

    // 고객 프로필 논리적 삭제
    @Override
    @Transactional
    public DeleteCustomerResponseDto deleteCustomerProfile(Long customerId) {
        // 고객 엔티티 조회 및 검증
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("고객"));

        if (customer.isDeleted()) {
            throw BadRequestException.invalidRequest("이미 삭제된 고객");
        }

        // 고객 관련 테이블 데이터 논리적 삭제
        dogRepository.findAllByCustomerId(customerId).forEach(Dog::delete); // 반려견 삭제
        chatRepository.findAllByCustomerId(customerId).forEach(Chat::delete); // 채팅방 삭제
        quoteRepository.findAllByCustomerDogs(customerId).forEach(Quote::delete); // 견적서 삭제
        selectedQuoteRepository.findAllByCustomerId(customerId).forEach(SelectedQuote::delete); // 선택된 견적서 삭제
        reviewRepository.findAllByCustomerId(customerId).forEach(Reviews::delete); // 리뷰 삭제
        paymentRepository.findAllBySelectedQuotes(
                selectedQuoteRepository.findAllByCustomerId(customerId)
        ).forEach(Payment::delete); // 결제 삭제
        quoteRequestRepository.findAllByCustomerDogs(customerId).forEach(QuoteRequest::delete); // 견적서 요청 삭제

        // 고객 엔티티 논리적 삭제
        customer.delete();

        // 응답 DTO 생성 및 반환
        return DeleteCustomerResponseDto.builder()
                .customerId(customer.getCustomerId())
                .userId(customer.getUserId().getUserId())
                .build();
    }


    // 고객 주소 수정
    @Transactional
    @Override
    public void updateAddress(Long customerId, String sidoName, String sigunguName) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("고객을 찾을 수 없습니다."));

        Sigungu sigungu = sigunguRepository.findBySidoId_SidoNameAndSigunguName(sidoName, sigunguName)
                .orElseThrow(() -> new EntityNotFoundException("시군구를 찾을 수 없습니다."));

        customer.updateSigungu(sigungu);
    }

    @Override
    @Transactional(readOnly = true)
    public GetCustomerMypageResponseDto getCustomerMypage(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));

        // 사용자 정보
        GetCustomerMypageResponseDto.UserInfoDto userInfo = GetCustomerMypageResponseDto.UserInfoDto.builder()
                .userName(customer.getUserId().getNickname() + " 고객님")
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
