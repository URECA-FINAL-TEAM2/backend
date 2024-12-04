package com.beautymeongdang.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GetMainGroomerTotalRequestResponseDto {
    private Long requestId;
    private String profileImage;
    private String nickname;
    private LocalDateTime closingDate;
    private LocalDateTime beautyDate;
    private String breed;
    private String dogWeight;
    private String dogGender;
    private String requestContent;

    @Builder
    public GetMainGroomerTotalRequestResponseDto(Long requestId, String nickname, String profileImage, LocalDateTime createdAt,
                           LocalDateTime beautyDate, String dogBreed, String dogGender, String dogWeight, String content) {
        this.requestId = requestId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.closingDate = createdAt.plusDays(2);
        this.beautyDate = beautyDate;
        this.breed = dogBreed;
        this.dogWeight = dogWeight;
        this.dogGender = dogGender;
        this.requestContent = content;
    }
}
