package com.beautymeongdang.global.login.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoUserInfo {
    private Long id;
    private String email;
    private String name;
    private String profileImage;

}
