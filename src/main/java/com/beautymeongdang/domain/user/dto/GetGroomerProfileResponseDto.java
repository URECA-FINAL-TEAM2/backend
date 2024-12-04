package com.beautymeongdang.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class GetGroomerProfileResponseDto {
    private Long groomerId;
    private String profileImage;
    private String username;
    private String email;
    private String nickname;
    private String phone;
    private String skills;

    @Builder
    public GetGroomerProfileResponseDto(Long groomerId, String profileImage, String username, String email,
                                        String nickname, String phone, String skill) {
        this.groomerId = groomerId;
        this.profileImage = profileImage;
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.phone = phone;
        this.skills = skill;
    }

}
