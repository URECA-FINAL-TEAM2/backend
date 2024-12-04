package com.beautymeongdang.domain.dog.service.impl;

import com.beautymeongdang.domain.dog.dto.*;
import com.beautymeongdang.domain.dog.entity.Dog;
import com.beautymeongdang.domain.dog.repository.DogRepository;
import com.beautymeongdang.domain.dog.service.DogService;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.global.common.entity.UploadedFile;
import com.beautymeongdang.global.exception.handler.BadRequestException;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import com.beautymeongdang.infra.s3.FileStore;
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

    private static final String DEFAULT_DOG_PROFILE_IMAGE = "https://s3-beauty-meongdang.s3.ap-northeast-2.amazonaws.com/%EB%B0%98%EB%A0%A4%EA%B2%AC+%ED%94%84%EB%A1%9C%ED%95%84+%EC%9D%B4%EB%AF%B8%EC%A7%80/%EB%AF%B8%EC%9A%A9%EC%82%AC%ED%94%84%EB%A1%9C%ED%95%84%EA%B8%B0%EB%B3%B8.jpg";

    /**
     * 반려견 프로필 생성
     */
    @Override
    public CreateDogResponseDto createDog(Long customerId, CreateDogRequestDto requestDto, MultipartFile dogProfile) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("고객"));

        String profileImageUrl = DEFAULT_DOG_PROFILE_IMAGE;
        if (dogProfile != null && !dogProfile.isEmpty()) {
            List<UploadedFile> uploadedFiles = fileStore.storeFiles(List.of(dogProfile), FileStore.DOG_PROFILE);
            profileImageUrl = uploadedFiles.get(0).getFileUrl();
        }

        Dog savedDog = dogRepository.save(Dog.builder()
                .customerId(customer)
                .dogName(requestDto.getDogName())
                .dogBreed(requestDto.getDogBreed())
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
                .dogBreed(savedDog.getDogBreed())
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

        return GetDogResponseDto.builder()
                .customerId(dog.getCustomerId().getCustomerId())
                .dogId(dog.getDogId())
                .dogName(dog.getDogName())
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



    @Override
    public UpdateDogResponseDto updateDog(Long dogId, Long customerId, UpdateDogRequestDto requestDto, MultipartFile dogProfile) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> NotFoundException.entityNotFound("반려견"));

        if (!dog.getCustomerId().getCustomerId().equals(customerId)) {
            throw BadRequestException.invalidRequest("해당 반려견의 소유자");
        }

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
                .dogBreed(requestDto.getDogBreed())
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
                .dogBreed(updatedDog.getDogBreed())
                .dogWeight(updatedDog.getDogWeight())
                .dogBirth(updatedDog.getDogBirth())
                .dogGender(updatedDog.getDogGender().name())
                .neutering(updatedDog.getNeutering())
                .experience(updatedDog.getExperience())
                .significant(updatedDog.getSignificant())
                .dogProfileImage(updatedDog.getProfileImage())
                .build();
    }
}


