package com.beautymeongdang.global.region.entity;

import com.beautymeongdang.domain.user.entity.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Sigungu {
    @Id
    @Column(name = "sigungu_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sido_id")
    private Sido sido;

    private String sigungu_name;
}