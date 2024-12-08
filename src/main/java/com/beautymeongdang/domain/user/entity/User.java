package com.beautymeongdang.domain.user.entity;

import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import com.beautymeongdang.global.common.entity.DeletableBaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends DeletableBaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String email;

    private String nickname;

    // User.java의 roles 부분만
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    private String socialProvider;

    private String profileImage;

    private String phone;

    @Column
    @Builder.Default
    private boolean isRegister = false;

    @Column(nullable = false)
    private String providerId;

    public void updateUserInfo(String phone, String nickname) {
        this.phone = phone;
        this.nickname = nickname;
    }


    public void completeRegistration() {
        this.isRegister = true;
    }
    public void updateProfileImage(String profileImageUrl) {
        this.profileImage = profileImageUrl;
    }
}
