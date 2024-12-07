package com.beautymeongdang.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerProfileResponseDto {
    private String userName;
    private String email;
    private String nickname;
    private String phone;
    private String profileImage;
    private Long sidoId;
    private Long sigunguId;
    private String sidoName;
    private String sigunguName;

    public CustomerProfileResponseDto(String userName, String email, String nickname, String phone,
                                      String profileImage, Long sidoId, Long sigunguId, String sidoName, String sigunguName) {
        this.userName = userName;
        this.email = email;
        this.nickname = nickname;
        this.phone = phone;
        this.profileImage = profileImage;
        this.sidoId = sidoId;
        this.sigunguId = sigunguId;
        this.sidoName = sidoName;
        this.sigunguName = sigunguName;
    }
}
