package com.beautymeongdang.domain.user.dto;

import com.nimbusds.oauth2.sdk.TokenIntrospectionSuccessResponse;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserDTO {
    private String role;
    private String nickname; // name 대신 nickname 사용
    private String username;
    private String profileImage; // 추가
}