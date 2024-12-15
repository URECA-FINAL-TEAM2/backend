package com.beautymeongdang.domain.payment.repository;

import com.beautymeongdang.domain.dog.entity.Dog;
import com.beautymeongdang.domain.payment.entity.Payment;
import com.beautymeongdang.domain.quote.entity.SelectedQuote;
import com.beautymeongdang.domain.user.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentKey(String paymentKey);

    @Query("SELECT p FROM Payment p WHERE p.selectedQuoteId IN :selectedQuotes AND p.isDeleted = false")
    List<Payment> findAllBySelectedQuotes(@Param("selectedQuotes") List<SelectedQuote> selectedQuotes);


    // 미용사 프로필 논리적 삭제
    Payment findBySelectedQuoteId(SelectedQuote selectedQuote);

    // 반려견 프로필 논리적 삭제
    List<Payment> findAllBySelectedQuoteIdQuoteIdDogId(Dog dog);

    // 결제 물리적 삭제
    @Query("""
    SELECT p
    FROM Payment p
    WHERE p.isDeleted = true
      AND p.updatedAt < :deleteDay
    """)
    List<Payment> findAllByIsDeletedAndUpdatedAtBefore(@Param("deleteDay") LocalDateTime deleteDay);
}
