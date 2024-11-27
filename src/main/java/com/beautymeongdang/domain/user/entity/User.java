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

    @Column(nullable = false, unique = true)
    private String nickname;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    private String socialProvider;

    private String profileImage;

    private String phone;

    public void updateUserInfo(String phone, String nickname) {
        this.phone = phone;
        this.nickname = nickname;
    }

    public void addRole(Role role) {
        UserRole userRole = UserRole.builder().user(this).role(role).build();
        this.userRoles.add(userRole);
    }

    public void removeRole(Role role) {
        this.userRoles.removeIf(userRole -> userRole.getRole().equals(role));
    }
}