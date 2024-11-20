package com.beautymeongdang.domain.user.entity;

import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroomerPortfolioImage extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portfolioImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groomer_id")
    private Groomer groomer;

    private String imageUrl;
    
}