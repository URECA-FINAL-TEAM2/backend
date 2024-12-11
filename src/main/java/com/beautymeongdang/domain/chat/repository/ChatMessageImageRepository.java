package com.beautymeongdang.domain.chat.repository;

import com.beautymeongdang.domain.chat.entity.ChatMessageImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageImageRepository extends JpaRepository<ChatMessageImage, Long> {
}