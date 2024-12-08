package com.beautymeongdang.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomerMypageResponseDto {
    private UserInfoDto userInfo;
    private CustomrtMypageCountsDto counts;
    private List<PetDto> myPets;


    @Getter
    @Builder
    @AllArgsConstructor
    public static class UserInfoDto {
        private String userName;
        private String email;
        private String profileImage;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CustomrtMypageCountsDto {
        private Integer completedServices;
        private Integer confirmedReservations;
        private Integer myReviews;
    }


    @Getter
    @Builder
    @AllArgsConstructor
    public static class PetDto {
        private Long petId;
        private String petName;
        private String profileImage;
    }

}
