package com.beautymeongdang.domain.review.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomerReviewListResponseDto {
    private Long reviewId;
    private String content;
    private String shopName;
    private String groomerName;
    private Integer reviewCount;
    private BigDecimal starRating;
    private LocalDate reviewDate;
    private Long groomerId;
    private Long customerId;
}
