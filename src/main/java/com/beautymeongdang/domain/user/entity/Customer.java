package com.beautymeongdang.domain.user.entity;

import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import com.beautymeongdang.global.common.entity.DeletableBaseTimeEntity;
import com.beautymeongdang.global.region.entity.Sigungu;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Customer extends DeletableBaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User userId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Sigungu sigunguId;

    private String address;

    private Double latitude;

    private Double longitude;
}
