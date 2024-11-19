package com.beautymeongdang.domain.shop.entity;

import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Shop extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "shop_id")
    private Long groomerId;

    private String shopName;

    private String description;

    private String address;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String businessTime;

    private String imageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean isDeleted;
}
