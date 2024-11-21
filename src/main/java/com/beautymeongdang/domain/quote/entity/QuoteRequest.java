package com.beautymeongdang.domain.quote.entity;

import com.beautymeongdang.domain.dog.entity.Dog;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.global.common.entity.DeletableBaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class QuoteRequest extends DeletableBaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Dog dogId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime beautyDate;

    private String requestType;

    private String status;
}
