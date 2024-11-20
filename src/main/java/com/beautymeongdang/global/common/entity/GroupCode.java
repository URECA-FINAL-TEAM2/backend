package com.beautymeongdang.global.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupCode {

    @Id
    @Column(name = "group_code", length = 3)
    private String groupId;

    @Column(name = "group_name", length = 20)
    private String groupName;

    public GroupCode(String groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }
}
