package com.beautymeongdang.domain.user.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteCustomerResponseDto  {
    private Long customerId;
    private Long userId;
}