package com.beautymeongdang.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReviewsImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewsImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Reviews reviewId;

    private String imageUrl;
}
