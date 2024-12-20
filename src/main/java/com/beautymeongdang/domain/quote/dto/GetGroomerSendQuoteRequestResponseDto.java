package com.beautymeongdang.domain.quote.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GetGroomerSendQuoteRequestResponseDto {
    private Long requestId;
    private String userName;
    private String userProfileImage;
    private LocalDateTime beautyDate;
    private String dogBreed;
    private String dogGender;
    private String dogWeight;
    private String requestContent;
    private String requestType;
    private String status;

    @Builder
    public GetGroomerSendQuoteRequestResponseDto(Long requestId, String userName, String profileImage,
                                                 LocalDateTime beautyDate, String dogBreed, String dogGender,
                                                 String dogWeight, String content, String requestType, String quoteStatus) {
        this.requestId = requestId;
        this.userName = userName;
        this.userProfileImage = profileImage;
        this.beautyDate = beautyDate;
        this.dogBreed = dogBreed;
        this.dogGender = dogGender;
        this.dogWeight = dogWeight;
        this.requestContent = content;
        this.requestType = requestType;
        this.status = quoteStatus;
    }

}
