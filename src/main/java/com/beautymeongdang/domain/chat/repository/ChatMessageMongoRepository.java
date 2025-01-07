package com.beautymeongdang.domain.chat.repository;

import com.beautymeongdang.domain.chat.entity.ChatMessageMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageMongoRepository extends MongoRepository<ChatMessageMongo, String> {

    // 채팅방 ID로 모든 메시지 조회 (시간순)
    List<ChatMessageMongo> findByChatIdOrderByCreatedAtAsc(Long chatId);

    // 채팅방의 안 읽은 메시지 개수 조회
    Long countByChatIdAndIsReadFalseAndSenderIdNot(Long chatId, Long userId);

    // 메시지들 읽음 처리
    @Query("{ 'chatId': ?0, 'senderId': { $ne: ?1 }, 'isRead': false }")
    @Update("{ '$set': { 'isRead': true } }")
    void updateMessagesAsRead(Long chatId, Long userId);

}