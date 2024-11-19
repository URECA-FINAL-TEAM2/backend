package com.beautymeongdang.domain.dog.entity;

import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Dog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dog_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "dog_name", nullable = false)
    private String name;

    @Column(name = "breed")
    private String breed;

    @Column(name = "dog_weight")
    private String weight;

    @Column(name = "dog_age")
    private Integer age;

    @Column(name = "dog_gender")
    @Enumerated(EnumType.STRING)
    private DogGender gender;

    private Boolean neutering;

    @Column(columnDefinition = "TEXT")
    private String significant;

    @Column(name = "profile_image")
    private String profileImage;

    public enum DogGender {
        남, 여
    }
}
