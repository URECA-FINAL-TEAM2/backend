package com.beautymeongdang.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class GetGroomerMypageResponseDto {
    private Long groomerId;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String profileImage;
    private GroomerMypageCountsDto counts;


    @Getter
    @Builder
    @AllArgsConstructor
    public static class GroomerMypageCountsDto {
        private Integer completedServices;
        private Integer confirmedReservations;
        private Integer myReviews;
    }

    @Builder
    public GetGroomerMypageResponseDto(Long groomerId, String nickname, String email,
                                       String phoneNumber, String profileImage, GroomerMypageCountsDto counts) {
        this.groomerId = groomerId;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
        this.counts = counts;
    }

}
