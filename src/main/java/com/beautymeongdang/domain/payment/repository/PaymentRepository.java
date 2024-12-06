package com.beautymeongdang.domain.payment.repository;

import com.beautymeongdang.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentKey(String paymentKey);

    @Query("SELECT p FROM Payment p WHERE p.selectedQuoteId.quoteId.groomerId.groomerId = :groomerId AND p.isDeleted = false")
    List<Payment> findByGroomerId(@Param("groomerId") Long groomerId);

}
