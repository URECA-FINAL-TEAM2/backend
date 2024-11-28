package com.beautymeongdang.domain.quote.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GetGroomerQuoteRequestResponseDto {
    private Long requestId;
    private String nickname;
    private String userProfileImage;
    private LocalDateTime expiryDate;
    private LocalDateTime beautyDate;
    private String dogBreed;
    private String dogGender;
    private String dogWeight;
    private String requestContent;

    @Builder
    public GetGroomerQuoteRequestResponseDto(Long requestId, String nickname, String profileImage, LocalDateTime createdAt,
                                             LocalDateTime beautyDate, String dogBreed,
                                             String dogGender,
                                             String dogWeight, String content) {
        this.requestId = requestId;
        this.nickname = nickname;
        this.userProfileImage = profileImage;
        this.expiryDate = createdAt.plusDays(2);
        this.beautyDate = beautyDate;
        this.dogBreed = dogBreed;
        this.dogGender = dogGender;
        this.dogWeight = dogWeight;
        this.requestContent = content;
    }
}
