package com.beautymeongdang.domain.user.entity;

import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import com.beautymeongdang.global.region.entity.Sigungu;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Customer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sigungu_id")
    private Sigungu sigunguId;

    private String address;

    private Double latitude;

    private Double longitude;
}
