package com.beautymeongdang.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
public class UpdateCustomerProfileDto {
    private Long customerId;
    private String nickname;
    private String phone;

    @Builder
    public UpdateCustomerProfileDto(Long customerId, String nickname, String phone) {
        this.customerId = customerId;
        this.nickname = nickname;
        this.phone = phone;
    }
}
