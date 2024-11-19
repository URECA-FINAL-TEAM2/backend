package com.beautymeongdang.domain.quote.entity;

import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DirectQuoteRequest extends BaseTimeEntity {

}
