package com.beautymeongdang.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class UpdateGroomerProfileDto {
    private Long groomerId;
    private String profileImage;
    private String nickname;
    private String phone;
    private String skill;

    @Builder
    public UpdateGroomerProfileDto(Long groomerId, String profileImage, String nickname, String phone, String skills) {
        this.groomerId = groomerId;
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.phone = phone;
        this.skill = skills;
    }
}
