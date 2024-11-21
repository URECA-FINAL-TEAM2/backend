package com.beautymeongdang.domain.review.entity;


import com.beautymeongdang.global.common.entity.DeletableBaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Recommend extends DeletableBaseTimeEntity {

    @EmbeddedId
    private RecommendId recommendId;


}


