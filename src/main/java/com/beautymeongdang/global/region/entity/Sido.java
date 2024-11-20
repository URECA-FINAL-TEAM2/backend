package com.beautymeongdang.global.region.entity;

import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Sido extends BaseTimeEntity{
    @Id
    @Column(name = "sido_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cityName;

    @OneToMany(mappedBy = "sido")
    private List<Sigungu> sigungus = new ArrayList<>();
}