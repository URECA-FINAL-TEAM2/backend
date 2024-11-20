package com.beautymeongdang.global.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public class DeletableBaseTimeEntity extends BaseTimeEntity {

    @Column(nullable = false)
    private Boolean isDeleted = false;

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
