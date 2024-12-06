package com.beautymeongdang.domain.dog.service.impl;

import com.beautymeongdang.domain.dog.dto.*;
import com.beautymeongdang.domain.dog.entity.Dog;
import com.beautymeongdang.domain.dog.repository.DogRepository;
import com.beautymeongdang.domain.dog.service.DogService;
import com.beautymeongdang.domain.quote.entity.Quote;
import com.beautymeongdang.domain.quote.entity.QuoteRequest;
import com.beautymeongdang.domain.quote.entity.SelectedQuote;
import com.beautymeongdang.domain.payment.entity.Payment;
import com.beautymeongdang.domain.payment.repository.PaymentRepository;
import com.beautymeongdang.domain.quote.repository.QuoteRepository;
import com.beautymeongdang.domain.quote.repository.QuoteRequestRepository;
import com.beautymeongdang.domain.quote.repository.SelectedQuoteRepository;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.global.common.entity.CommonCode;
import com.beautymeongdang.global.common.entity.CommonCodeId;
import com.beautymeongdang.global.common.entity.UploadedFile;
import com.beautymeongdang.global.common.repository.CommonCodeRepository;
import com.beautymeongdang.global.exception.handler.BadRequestException;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import com.beautymeongdang.infra.s3.FileStore;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DogServiceImpl implements DogService {

    private final FileStore fileStore;
    private final DogRepository dogRepository;
    private final CustomerRepository customerRepository;
    private final QuoteRepository quoteRepository;
    private final QuoteRequestRepository quoteRequestRepository;
    private final SelectedQuoteRepository selectedQuoteRepository;
    private final PaymentRepository paymentRepository;
    private final CommonCodeRepository commonCodeRepository;

    private static final String DEFAULT_DOG_PROFILE_IMAGE = "https://s3-beauty-meongdang.s3.ap-northeast-2.amazonaws.com/%EB%A7%A4%EC%9E%A5+%EB%A1%9C%EA%B3%A0+%EC%9D%B4%EB%AF%B8%EC%A7%80/43936c99-66cd-4cf3-a600-782527c30ab6.jpg";
    private static final String DOG_BREED_GROUP_CODE = "400";

    /**
     * 반려견 프로필 생성
     */
    @Override
    public CreateDogResponseDto createDog(Long customerId, CreateDogRequestDto requestDto, MultipartFile dogProfile) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("고객"));

        Long currentDogCount = dogRepository.countByCustomerIdAndIsDeletedFalse(customer);
        if (currentDogCount >= 5) {
            throw new BadRequestException("반려견은 최대 5마리까지만 등록할 수 있습니다");
        }

        CommonCodeId commonCodeId = new CommonCodeId(requestDto.getDogBreedCodeId(), DOG_BREED_GROUP_CODE);
        CommonCode breedCode = commonCodeRepository.findById(commonCodeId)
                .orElseThrow(() -> NotFoundException.entityNotFound("견종 코드"));

        String dogBreed = breedCode.getCommonName();

        String profileImageUrl = DEFAULT_DOG_PROFILE_IMAGE;
        if (dogProfile != null && !dogProfile.isEmpty()) {
            List<UploadedFile> uploadedFiles = fileStore.storeFiles(List.of(dogProfile), FileStore.DOG_PROFILE);
            profileImageUrl = uploadedFiles.get(0).getFileUrl();
        }

        Dog savedDog = dogRepository.save(Dog.builder()
                .customerId(customer)
                .dogName(requestDto.getDogName())
                .dogBreed(dogBreed)
                .dogWeight(requestDto.getDogWeight())
                .dogBirth(requestDto.getDogBirth())
                .dogGender(Dog.DogGender.valueOf(requestDto.getDogGender()))
                .neutering(requestDto.isNeutering())
                .experience(requestDto.isExperience())
                .significant(requestDto.getSignificant())
                .profileImage(profileImageUrl)
                .build());

        return CreateDogResponseDto.builder()
                .customerId(savedDog.getCustomerId().getCustomerId())
                .dogId(savedDog.getDogId())
                .dogName(savedDog.getDogName())
                .dogBreedCodeId(breedCode.getId().getCodeId())
                .dogBreed(dogBreed)
                .dogWeight(savedDog.getDogWeight())
                .dogBirth(savedDog.getDogBirth())
                .dogGender(savedDog.getDogGender().name())
                .neutering(savedDog.getNeutering())
                .experience(savedDog.getExperience())
                .significant(savedDog.getSignificant())
                .dogProfileImage(savedDog.getProfileImage())
                .build();
    }


    /**
     * 반려견 프로필 조회
     */
    @Override
    public GetDogResponseDto getDog(Long dogId, Long customerId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> NotFoundException.entityNotFound("반려견"));

        if (!dog.getCustomerId().getCustomerId().equals(customerId)) {
            throw BadRequestException.invalidRequest("해당 반려견의 소유자");
        }

        String breedCodeId = commonCodeRepository.findAll()
                .stream()
                .filter(code -> code.getId().getGroupId().equals(DOG_BREED_GROUP_CODE))
                .filter(code -> code.getCommonName().equals(dog.getDogBreed()))
                .map(code -> code.getId().getCodeId())
                .findFirst()
                .orElseThrow(() -> NotFoundException.entityNotFound("견종 코드"));

        return GetDogResponseDto.builder()
                .customerId(dog.getCustomerId().getCustomerId())
                .dogId(dog.getDogId())
                .dogName(dog.getDogName())
                .dogBreedCodeId(breedCodeId)
                .dogBreed(dog.getDogBreed())
                .dogWeight(dog.getDogWeight())
                .dogBirth(dog.getDogBirth())
                .dogGender(dog.getDogGender().name())
                .neutering(dog.getNeutering())
                .experience(dog.getExperience())
                .significant(dog.getSignificant())
                .dogProfileImage(dog.getProfileImage())
                .build();
    }


    /**
     * 반려견 프로필 수정
     */
    @Override
    public UpdateDogResponseDto updateDog(Long dogId, Long customerId, UpdateDogRequestDto requestDto, MultipartFile dogProfile) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> NotFoundException.entityNotFound("반려견"));

        if (!dog.getCustomerId().getCustomerId().equals(customerId)) {
            throw BadRequestException.invalidRequest("해당 반려견의 소유자");
        }

        CommonCodeId commonCodeId = new CommonCodeId(requestDto.getDogBreedCodeId(), DOG_BREED_GROUP_CODE);
        CommonCode breedCode = commonCodeRepository.findById(commonCodeId)
                .orElseThrow(() -> NotFoundException.entityNotFound("견종 코드"));
        String dogBreed = breedCode.getCommonName();

        String profileImageUrl = dog.getProfileImage();
        if (dogProfile != null && !dogProfile.isEmpty()) {
            if (!profileImageUrl.equals(DEFAULT_DOG_PROFILE_IMAGE)) {
                fileStore.deleteFile(profileImageUrl);
            }

            List<UploadedFile> uploadedFiles = fileStore.storeFiles(List.of(dogProfile), FileStore.DOG_PROFILE);
            profileImageUrl = uploadedFiles.get(0).getFileUrl();
        }

        Dog updatedDog = dogRepository.save(Dog.builder()
                .dogId(dogId)
                .customerId(dog.getCustomerId())
                .dogName(requestDto.getDogName())
                .dogBreed(dogBreed)
                .dogWeight(requestDto.getDogWeight())
                .dogBirth(requestDto.getDogBirth())
                .dogGender(Dog.DogGender.valueOf(requestDto.getDogGender()))
                .neutering(requestDto.isNeutering())
                .experience(requestDto.isExperience())
                .significant(requestDto.getSignificant())
                .profileImage(profileImageUrl)
                .build());

        return UpdateDogResponseDto.builder()
                .customerId(updatedDog.getCustomerId().getCustomerId())
                .dogId(updatedDog.getDogId())
                .dogName(updatedDog.getDogName())
                .dogBreedCodeId(breedCode.getId().getCodeId())
                .dogBreed(dogBreed)
                .dogWeight(updatedDog.getDogWeight())
                .dogBirth(updatedDog.getDogBirth())
                .dogGender(updatedDog.getDogGender().name())
                .neutering(updatedDog.getNeutering())
                .experience(updatedDog.getExperience())
                .significant(updatedDog.getSignificant())
                .dogProfileImage(updatedDog.getProfileImage())
                .build();
    }

    /**
     * 반려견 프로필 삭제
     */
    @Override
    @Transactional
    public DeleteDogResponseDto deleteDog(Long dogId, Long customerId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> NotFoundException.entityNotFound("반려견"));

        if (!dog.getCustomerId().getCustomerId().equals(customerId)) {
            throw BadRequestException.invalidRequest("해당 반려견의 소유자");
        }

        paymentRepository.findAllBySelectedQuoteIdQuoteIdDogId(dog).forEach(Payment::delete);
        selectedQuoteRepository.findAllByQuoteIdDogId(dog).forEach(SelectedQuote::delete);
        quoteRepository.findAllByDogId(dog).forEach(Quote::delete);
        quoteRequestRepository.findAllByDogId(dog).forEach(QuoteRequest::delete);

        // 반려견 논리적 삭제
        dog.delete();

        return DeleteDogResponseDto.builder()
                .dogId(dog.getDogId())
                .build();
    }



}


