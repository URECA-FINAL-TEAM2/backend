package com.beautymeongdang.domain.payment.repository;

import com.beautymeongdang.domain.dog.entity.Dog;
import com.beautymeongdang.domain.payment.entity.Payment;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.quote.entity.SelectedQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentKey(String paymentKey);

    // 매장 논리적 삭제
    List<Payment> findAllBySelectedQuoteIdQuoteIdGroomerIdAndIsDeletedFalse(Groomer groomerId);

    // 미용사 프로필 논리적 삭제
    Payment findBySelectedQuoteId(SelectedQuote selectedQuote);

    // 반려견 프로필 논리적 삭제
    List<Payment> findAllBySelectedQuoteIdQuoteIdDogId(Dog dog);

}
