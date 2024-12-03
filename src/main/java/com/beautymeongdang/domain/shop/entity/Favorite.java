package com.beautymeongdang.domain.shop.entity;

import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Favorite extends BaseTimeEntity {

    @EmbeddedId
    private FavoriteId favoriteId;
}
