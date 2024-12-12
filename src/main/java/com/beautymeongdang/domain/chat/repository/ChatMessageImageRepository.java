package com.beautymeongdang.domain.chat.repository;

import com.beautymeongdang.domain.chat.entity.ChatMessage;
import com.beautymeongdang.domain.chat.entity.ChatMessageImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageImageRepository extends JpaRepository<ChatMessageImage, Long> {
    // 채팅 물리적 삭제 스케줄러
    List<ChatMessageImage> findAllByMessageId(ChatMessage messageId);
}