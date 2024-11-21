package com.beautymeongdang.domain.dog.entity;

import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.global.common.entity.DeletableBaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Dog extends DeletableBaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Customer customerId;
    
    private String dogName;

    private String dogBreed;

    private String dogWeight;

    private Integer dogAge;

    private String dogBirth;

    @Enumerated(EnumType.STRING)
    private DogGender dogGender;

    private Boolean neutering;

    private Boolean experience;

    @Column(columnDefinition = "TEXT")
    private String significant;

    private String profileImage;

    public enum DogGender {
        male, female
    }
}
