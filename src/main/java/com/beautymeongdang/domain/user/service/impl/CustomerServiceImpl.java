package com.beautymeongdang.domain.user.service.impl;

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
import com.beautymeongdang.domain.user.dto.GetCustomerAddressResponseDto;
import com.beautymeongdang.domain.user.dto.UpdateCustomerProfileDto;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.dto.DeleteCustomerResponseDto;
import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.domain.user.service.CustomerService;
import com.beautymeongdang.domain.user.service.UserService;
import com.beautymeongdang.global.common.entity.UploadedFile;
import com.beautymeongdang.global.exception.handler.BadRequestException;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import com.beautymeongdang.global.region.entity.Sigungu;
import com.beautymeongdang.global.region.repository.SigunguRepository;
import com.beautymeongdang.infra.s3.FileStore;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Value("${user.default-profile-image}")
    private String defaultProfileImage;
    private final CustomerRepository customerRepository;
    private final SigunguRepository sigunguRepository;
    private final DogRepository dogRepository;
    private final QuoteRepository quoteRepository;
    private final SelectedQuoteRepository selectedQuoteRepository;
    private final ReviewRepository reviewRepository;
    private final PaymentRepository paymentRepository;
    private final ChatRepository chatRepository;
    private final QuoteRequestRepository quoteRequestRepository;
    private final FileStore fileStore;
    private final UserRepository userRepository;
    private final UserService userService;

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


        // 회원의 등록 상태 체크 및 업데이트
        try {
            userService.checkAndUpdateRegistrationStatus(customer.getUserId());
        } catch (Exception e) {
            throw new RuntimeException("회원 상태 업데이트 중 오류가 발생했습니다", e);
        }


        // 응답 DTO 생성 및 반환
        return DeleteCustomerResponseDto.builder()
                .customerId(customer.getCustomerId())
                .userId(customer.getUserId().getUserId())
                .build();
    }

    @Override
    @Transactional
    public UpdateCustomerProfileDto updateCustomerProfile(UpdateCustomerProfileDto updateCustomerProfileDto, List<MultipartFile> images) {

        if (images != null && images.size() > 1) {
            throw new BadRequestException("프로필 이미지는 1장만 등록할 수 있습니다");
        }

        // 고객 조회
        Customer customer = customerRepository.findById(updateCustomerProfileDto.getCustomerId())
                .orElseThrow(() -> NotFoundException.entityNotFound("고객"));

        // 회원 정보 조회
        User user = userRepository.findById(customer.getUserId().getUserId())
                .orElseThrow(() -> NotFoundException.entityNotFound("회원"));

        // S3 이미지 처리
        String profileImageUrl = user.getProfileImage();
        if (images != null && !images.isEmpty()) {

            // 기존 이미지가 기본 이미지가 아니고, null이 아닌 경우에만 S3 이미지 삭제
            String currentProfileImage = user.getProfileImage();
            if (currentProfileImage != null && !currentProfileImage.equals(defaultProfileImage)) {
                fileStore.deleteFile(currentProfileImage);
            }

            // 새로운 이미지 업로드
            List<UploadedFile> uploadedFiles = fileStore.storeFiles(images, FileStore.USER_PROFILE);
            profileImageUrl = uploadedFiles.get(0).getFileUrl(); // 여러 이미지 중 첫 번째 이미지를 사용
        }

        // 회원 정보 업데이트
        user.updateUserInfo(updateCustomerProfileDto.getPhone(), updateCustomerProfileDto.getNickname());
        if (profileImageUrl != null) {
            user.updateProfileImage(profileImageUrl);
        }

        // 변경된 회원 정보 저장
        userRepository.save(user);

        // 응답 DTO 생성 및 반환
        return UpdateCustomerProfileDto.builder()
                .customerId(customer.getCustomerId())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .build();
    }

    // 고객 주소 조회
    @Override
    public GetCustomerAddressResponseDto getCustomerAddress(Long customerId) {
        return customerRepository.findCustomerAddressById(customerId);
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
    
    // 고객 프로필 물리적 삭제
    @Override
    @Transactional
    public void deleteExpiredLogicalDeletedCustomers() {
        // 30일 이전 데이터를 삭제 기준으로 설정
        LocalDateTime deleteDay = LocalDateTime.now().minusDays(30);
        List<Customer> expiredCustomers = customerRepository.findAllByIsDeletedAndUpdatedAtBefore(deleteDay);

        // 물리적 삭제 실행
        customerRepository.deleteAll(expiredCustomers);
    }



}
