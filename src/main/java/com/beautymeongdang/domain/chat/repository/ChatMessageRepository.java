package com.beautymeongdang.domain.chat.repository;

import com.beautymeongdang.domain.chat.entity.Chat;
import com.beautymeongdang.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 미용사 프로필 논리적 삭제
    List<ChatMessage> findAllByChatId(Chat chat);
}
