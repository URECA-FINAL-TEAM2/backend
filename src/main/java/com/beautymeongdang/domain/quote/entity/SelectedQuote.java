package com.beautymeongdang.domain.quote.entity;

import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SelectedQuote extends BaseTimeEntity {
}
