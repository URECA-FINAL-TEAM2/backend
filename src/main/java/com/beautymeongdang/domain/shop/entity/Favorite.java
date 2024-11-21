package com.beautymeongdang.domain.shop.entity;

import com.beautymeongdang.global.common.entity.DeletableBaseTimeEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Favorite extends DeletableBaseTimeEntity {

    @EmbeddedId
    private FavoriteId favoriteId;
}
