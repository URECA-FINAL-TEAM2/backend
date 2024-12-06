package com.beautymeongdang.domain.chat.repository;

import com.beautymeongdang.domain.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE c.customerId.customerId = :customerId AND c.isDeleted = false")
    List<Chat> findAllByCustomerId(@Param("customerId") Long customerId);

}
