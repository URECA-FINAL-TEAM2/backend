package com.beautymeongdang.domain.chat.repository;

import com.beautymeongdang.domain.chat.dto.GetChatMessageResponseDto;
import com.beautymeongdang.domain.chat.entity.Chat;
import com.beautymeongdang.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 미용사 프로필 논리적 삭제
    List<ChatMessage> findAllByChatId(Chat chat);

    // 채팅 조회
    @Query("SELECT new com.beautymeongdang.domain.chat.dto.GetChatMessageResponseDto(" +
            "cm.messageId, cm.customerYn, cm.content, cm.createdAt, cmi.imageUrl) " +
            "FROM Chat c " +
            "JOIN ChatMessage cm ON cm.chatId.chatId = c.chatId " +
            "LEFT JOIN ChatMessageImage cmi ON cmi.messageId.messageId = cm.messageId " +
            "WHERE c.chatId = :chatId " +
            "AND cm.isDeleted = false " +
            "ORDER BY cm.createdAt ASC")
    List<GetChatMessageResponseDto> findChatMessagesWithImages(@Param("chatId") Long chatId);

    // 채팅 물리적 삭제 스케줄러
    @Query("""
    SELECT cm
    FROM ChatMessage cm
    WHERE cm.isDeleted = true
      AND cm.updatedAt < :deleteDay
    """)
    List<ChatMessage> findDeletedMessagesBeforeDate(@Param("deleteDay") LocalDateTime deleteDay);

}
