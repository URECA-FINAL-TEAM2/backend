package com.beautymeongdang.global.login.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoToken {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String id_token;
    private Integer expires_in;
    private String scope;
    private Integer refresh_token_expires_in;
}
