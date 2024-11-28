package com.beautymeongdang.global.login.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserInfo {
    private String id;
    private String email;
    private String name;
    private String picture;
}