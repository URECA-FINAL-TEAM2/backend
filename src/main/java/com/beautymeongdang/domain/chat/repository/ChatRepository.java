package com.beautymeongdang.domain.chat.repository;

import com.beautymeongdang.domain.chat.entity.Chat;
import com.beautymeongdang.domain.user.entity.Groomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    // 미용사 프로필 논리적 삭제
    List<Chat> findAllByGroomerId(Groomer groomer);
}
