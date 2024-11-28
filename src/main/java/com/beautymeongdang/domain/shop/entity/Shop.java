package com.beautymeongdang.domain.shop.entity;

import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.global.common.entity.DeletableBaseTimeEntity;
import com.beautymeongdang.global.region.entity.Sigungu;
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
    private Long shopId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groomer_id",nullable = false)
    private Groomer groomerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sigungu_id",nullable = false)
    private Sigungu sigunguId;

    private String shopName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String address;

    @Column(precision = 17, scale = 14)
    private BigDecimal latitude;

    @Column(precision = 17, scale = 14)
    private BigDecimal longitude;

    private String businessTime;

    private String imageUrl;

    // Shop 클래스
    public void updateShopInfo(Sigungu sigungu, String shopName, String description, String address,
                               BigDecimal latitude, BigDecimal longitude, String businessTime, String imageUrl) {
        this.sigunguId = sigungu;
        this.shopName = shopName;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.businessTime = businessTime;
        this.imageUrl = imageUrl;
    }
}
