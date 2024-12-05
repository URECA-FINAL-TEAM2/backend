package com.beautymeongdang.global.login.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleUserInfo {
    private String id;
    private String email;
    private String name;
    private String profileImage;
    private boolean emailVerified;
    private String locale;
}