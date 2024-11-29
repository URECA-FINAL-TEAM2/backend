package com.beautymeongdang.domain.user.dto;


import com.beautymeongdang.domain.user.entity.Role;
import lombok.*;

import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String nickname;
    private Set<Role> roles;
    private String profileImage;
}