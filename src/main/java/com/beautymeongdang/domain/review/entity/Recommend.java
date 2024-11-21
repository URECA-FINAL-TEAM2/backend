package com.beautymeongdang.domain.review.entity;


import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Recommend extends BaseTimeEntity {

    @EmbeddedId
    private RecommendId recommendId;


}


