package com.beautymeongdang.domain.chat.repository;

import com.beautymeongdang.domain.chat.dto.GetCustomerChatListResponseDto;
import com.beautymeongdang.domain.chat.entity.Chat;
import com.beautymeongdang.domain.user.entity.Groomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE c.customerId.customerId = :customerId AND c.isDeleted = false")
    List<Chat> findAllByCustomerId(@Param("customerId") Long customerId);

    // 미용사 프로필 논리적 삭제
    List<Chat> findAllByGroomerId(Groomer groomer);

    // 고객 채팅방 목록 조회
    @Query("""
    SELECT new com.beautymeongdang.domain.chat.dto.GetCustomerChatListResponseDto(
        c.chatId,
        g.groomerId,
        u.nickname,
        u.profileImage,
        s.shopName,
        si.sidoName,
        sig.sigunguName,
        cm.content,
        cm.createdAt
    )
    FROM Chat c
    JOIN c.customerId cu
    JOIN c.groomerId g
    JOIN g.userId u
    JOIN Shop s ON s.groomerId.groomerId = g.groomerId
    JOIN s.sigunguId sig
    JOIN sig.sidoId si
    LEFT JOIN ChatMessage cm ON cm.chatId = c
        AND cm.messageId = (
            SELECT MAX(cm2.messageId)
            FROM ChatMessage cm2
            WHERE cm2.chatId = c AND cm2.isDeleted = false
        )
    WHERE c.isDeleted = false AND cu.customerId = :customerId
    ORDER BY cm.createdAt DESC
    """)
    List<GetCustomerChatListResponseDto> getCustomerChatList(@Param("customerId") Long customerId);
}
