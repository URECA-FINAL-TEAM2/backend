package com.beautymeongdang.domain.shop.entity;

import com.beautymeongdang.global.common.entity.DeletableBaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Shop extends DeletableBaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "groomer_id")
    private Long groomerId;

    private String shopName;

    private String description;

    private String address;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String businessTime;

    private String imageUrl;
}
