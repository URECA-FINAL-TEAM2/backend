package com.beautymeongdang.domain.chat.repository;

import com.beautymeongdang.domain.chat.dto.GetCustomerChatListResponseDto;
import com.beautymeongdang.domain.chat.dto.GetGroomerChatListResponseDto;
import com.beautymeongdang.domain.chat.entity.Chat;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE c.customerId.customerId = :customerId AND c.isDeleted = false")
    List<Chat> findAllByCustomerId(@Param("customerId") Long customerId);

    // 미용사 프로필 논리적 삭제
    List<Chat> findAllByGroomerId(Groomer groomer);

    // customer와 groomer 간의 기존 채팅방을 찾는 메서드
    @Query("SELECT c FROM Chat c WHERE c.customerId = :customer AND c.groomerId = :groomer AND c.isDeleted = false")
    Optional<Chat> findByCustomerIdAndGroomerIdAndNotDeleted(
            @Param("customer") Customer customer,
            @Param("groomer") Groomer groomer
    );


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

    // 고객 채팅방 목록 검색 조회
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
    WHERE c.isDeleted = false
    AND cu.customerId = :customerId
    AND (
           u.nickname LIKE CONCAT('%', :searchKeyword, '%')
           OR s.shopName LIKE CONCAT('%', :searchKeyword, '%')
    )
    ORDER BY cm.createdAt DESC
    """)
    List<GetCustomerChatListResponseDto> getCustomerChatListBySearchKeyword(@Param("customerId") Long customerId, @Param("searchKeyword") String searchKeyword);

    // 미용사 채팅방 목록 조회
    @Query("""
        SELECT new com.beautymeongdang.domain.chat.dto.GetGroomerChatListResponseDto(
            c.chatId,
            cu.customerId,
            u.userName,
            u.profileImage,
            cm.content,
            cm.createdAt
        )
        FROM Chat c
        JOIN c.customerId cu
        JOIN cu.userId u
        JOIN c.groomerId g
        LEFT JOIN ChatMessage cm ON cm.chatId = c
            AND cm.messageId = (
                SELECT MAX(cm2.messageId)
                FROM ChatMessage cm2
                WHERE cm2.chatId = c AND cm2.isDeleted = false
            )
        WHERE c.isDeleted = false
          AND g.groomerId = :groomerId
        ORDER BY cm.createdAt DESC
    """)
    List<GetGroomerChatListResponseDto> getGroomerChatList(@Param("groomerId") Long groomerId);

    // 미용사 채팅방 목록 검색 조회
    @Query("""
        SELECT new com.beautymeongdang.domain.chat.dto.GetGroomerChatListResponseDto(
            c.chatId,
            cu.customerId,
            u.userName,
            u.profileImage,
            cm.content,
            cm.createdAt
        )
        FROM Chat c
        JOIN c.customerId cu
        JOIN cu.userId u
        JOIN c.groomerId g
        LEFT JOIN ChatMessage cm ON cm.chatId = c
            AND cm.messageId = (
                SELECT MAX(cm2.messageId)
                FROM ChatMessage cm2
                WHERE cm2.chatId = c AND cm2.isDeleted = false
            )
        WHERE c.isDeleted = false
          AND g.groomerId = :groomerId
          AND (
                u.userName LIKE CONCAT('%', :searchKeyword, '%')
           )
        ORDER BY cm.createdAt DESC
    """)
    List<GetGroomerChatListResponseDto> getGroomerChatListBySearchKeyword(@Param("groomerId") Long groomerId, @Param("searchKeyword") String searchKeyword);

    // 채팅 조회 - 미용사
    @Query("SELECT u " +
            "FROM Chat c " +
            "JOIN c.groomerId g " +
            "JOIN g.userId u " +
            "WHERE c.chatId = :chatId")
    User findGroomerByChatId(@Param("chatId") Long chatId);

    // 채팅 조회 - 고객
    @Query("SELECT u " +
            "FROM Chat c " +
            "JOIN c.customerId cu " +
            "JOIN cu.userId u " +
            "WHERE c.chatId = :chatId")
    User findCustomerByChatId(@Param("chatId") Long chatId);


    // 채팅 물리적 삭제 스케줄러
    @Query("""
    SELECT c
    FROM Chat c
    WHERE c.isDeleted = true
      AND c.updatedAt < :deleteDay
    """)
    List<Chat> findAllByDeletedAndUpdatedAt(@Param("deleteDay") LocalDateTime deleteDay);



}
