package com.beautymeongdang.global.login.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleTokenResponse {
    private String access_token;
    private String token_type;
    private Integer expires_in;
    private String scope;
    private String id_token;
}