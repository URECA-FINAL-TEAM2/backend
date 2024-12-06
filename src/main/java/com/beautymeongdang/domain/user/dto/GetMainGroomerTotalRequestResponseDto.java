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
    private String userName;
    private LocalDateTime closingDate;
    private LocalDateTime beautyDate;
    private String breed;
    private String dogWeight;
    private String dogGender;
    private String requestContent;

    @Builder
    public GetMainGroomerTotalRequestResponseDto(Long requestId, String userName, String profileImage, LocalDateTime createdAt,
                           LocalDateTime beautyDate, String commonName, String dogGender, String dogWeight, String content) {
        this.requestId = requestId;
        this.userName = userName;
        this.profileImage = profileImage;
        this.closingDate = createdAt.plusDays(2);
        this.beautyDate = beautyDate;
        this.breed = commonName;
        this.dogWeight = dogWeight;
        this.dogGender = dogGender;
        this.requestContent = content;
    }
}
