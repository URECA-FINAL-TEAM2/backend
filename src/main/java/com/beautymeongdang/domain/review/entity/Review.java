package com.beautymeongdang.domain.review.entity;

import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review extends BaseTimeEntity {
}
