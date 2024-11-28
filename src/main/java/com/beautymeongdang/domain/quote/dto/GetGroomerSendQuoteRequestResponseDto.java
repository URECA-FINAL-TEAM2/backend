package com.beautymeongdang.domain.quote.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GetGroomerSendQuoteRequestResponseDto {
    private Long requestId;
    private String nickname;
    private String userProfileImage;
    private LocalDateTime beautyDate;
    private String dogBreed;
    private String dogGender;
    private String dogWeight;
    private String requestContent;
    private String requestType;

    @Builder
    public GetGroomerSendQuoteRequestResponseDto(Long requestId, String nickname, String profileImage, LocalDateTime beautyDate,
                                                 String dogBreed, String dogGender, String dogWeight, String content, String requestType) {
        this.requestId = requestId;
        this.nickname = nickname;
        this.userProfileImage = profileImage;
        this.beautyDate = beautyDate;
        this.dogBreed = dogBreed;
        this.dogGender = dogGender;
        this.dogWeight = dogWeight;
        this.requestContent = content;
        this.requestType = requestType;
    }

}
