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
    private boolean isRegister;

    // 탈퇴 상태
    private boolean customerDeletionStatus;
    private boolean groomerDeletionStatus;
    private long customerDaysUntilReregister;  // 고객 재가입까지 남은 일수
    private long groomerDaysUntilReregister;   // 미용사 재가입까지 남은 일수
}