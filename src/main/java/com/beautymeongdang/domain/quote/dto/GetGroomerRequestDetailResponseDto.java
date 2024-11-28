package com.beautymeongdang.domain.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class GetGroomerRequestDetailResponseDto {
    // quoteRequest
    private Long requestId;
    private LocalDateTime expiryDate;
    private LocalDateTime beautyDate;
    private String requestContent;

    // user
    private String userProfileImage;
    private String nickname;

    // dog
    private Long dogId;
    private String dogProfileImage;
    private String dogName;
    private String dogBreed;
    private String dogWeight;
    private Integer dogAge;
    private String dogGender;
    private Boolean neutering;
    private Boolean experience;
    private String significant;

    // quoteRequestImage
    private List<String> requestImages;

    @Builder
    public GetGroomerRequestDetailResponseDto(Long requestId, LocalDateTime expiryDate, LocalDateTime beautyDate, String requestContent,
                                              String userProfileImage, String nickname, Long dogId, String dogProfileImage,
                                              String dogName, String dogBreed, String dogWeight, Integer dogAge, String dogGender,
                                              Boolean neutering, Boolean experience, String significant, List<String> requestImages) {
        this.requestId = requestId;
        this.expiryDate = expiryDate;
        this.beautyDate = beautyDate;
        this.requestContent = requestContent;
        this.userProfileImage = userProfileImage;
        this.nickname = nickname;
        this.dogId = dogId;
        this.dogProfileImage = dogProfileImage;
        this.dogName = dogName;
        this.dogBreed = dogBreed;
        this.dogWeight = dogWeight;
        this.dogAge = dogAge;
        this.dogGender = dogGender;
        this.neutering = neutering;
        this.experience = experience;
        this.significant = significant;
        this.requestImages = requestImages;
    }

}
