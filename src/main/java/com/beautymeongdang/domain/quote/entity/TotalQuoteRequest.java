package com.beautymeongdang.domain.quote.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TotalQuoteRequest{

    @EmbeddedId
    private TotalQuoteRequestId totalQuoteRequestId;
}
