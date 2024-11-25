package com.beautymeongdang.domain.quote.repository;

import com.beautymeongdang.domain.quote.entity.SelectedQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectedQuoteRepository  extends JpaRepository<SelectedQuote, Long> {
}
