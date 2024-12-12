package com.beautymeongdang.domain.user.entity;

import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import com.beautymeongdang.global.common.entity.DeletableBaseTimeEntity;
import com.beautymeongdang.global.region.entity.Sigungu;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Customer extends DeletableBaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sigungu_id",nullable = false)
    private Sigungu sigunguId;

    public void updateSigungu(Sigungu sigungu) {
        this.sigunguId = sigungu;
    }
}
