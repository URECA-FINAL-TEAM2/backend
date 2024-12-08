package com.beautymeongdang.domain.chat.entity;

import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.global.common.entity.DeletableBaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Chat extends DeletableBaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id",nullable = false)
    private Customer customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groomer_id",nullable = false)
    private Groomer groomerId;

    @Column(nullable = false)
    private Boolean customerExitedYn = false;

    @Column(nullable = false)
    private Boolean groomerExitedYn = false;

    public void customerExited() {
        this.customerExitedYn= true;
    }

    public void groomerExited() {
        this.groomerExitedYn= true;
    }
}
