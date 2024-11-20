package com.beautymeongdang.domain.user.entity;

import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import com.beautymeongdang.global.common.entity.CommonCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String username;

    private String email;

    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    private CommonCode role;

    @ManyToOne(fetch = FetchType.LAZY)
    private CommonCode socialProvider;

    private String profileImage;

    private String phone;
}
