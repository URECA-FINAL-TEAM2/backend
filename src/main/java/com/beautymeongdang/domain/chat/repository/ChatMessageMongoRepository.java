package com.beautymeongdang.domain.chat.repository;

import com.beautymeongdang.domain.chat.entity.ChatMessageMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageMongoRepository extends MongoRepository<ChatMessageMongo, String> {

    // 채팅방 ID로 모든 메시지 조회 (시간순)
    List<ChatMessageMongo> findByChatIdOrderByCreatedAtAsc(Long chatId);
}