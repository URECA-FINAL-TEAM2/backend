package com.beautymeongdang.domain.user.dto;

import com.nimbusds.oauth2.sdk.TokenIntrospectionSuccessResponse;
import lombok.*;

import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private Set<String> roles;
    private String nickname;
    private String username;
    private String profileImage; // 추가

}