package com.beautymeongdang.domain.quote.repository;

import com.beautymeongdang.domain.quote.dto.GetCustomerSelectedQuoteResponseDto;
import com.beautymeongdang.domain.quote.dto.GetSelectedQuoteDetailResponseDto;
import com.beautymeongdang.domain.quote.dto.GetGroomerSelectedQuoteResponseDto;
import com.beautymeongdang.domain.quote.entity.SelectedQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelectedQuoteRepository extends JpaRepository<SelectedQuote, Long> {

    @Query("SELECT new com.beautymeongdang.domain.quote.dto.GetCustomerSelectedQuoteResponseDto(" +
            "sq.selectedQuoteId, q.quoteId, d.profileImage, s.shopName, " +
            "g.userId.userName, q.beautyDate, d.dogName, sq.status) " +
            "FROM SelectedQuote sq " +
            "JOIN sq.quoteId q " +
            "JOIN q.dogId d " +
            "JOIN q.groomerId g " +
            "JOIN Shop s ON s.groomerId = g " +
            "WHERE sq.customerId.customerId = :customerId AND sq.isDeleted = false")
    List<GetCustomerSelectedQuoteResponseDto> findCustomerSelectedQuotes(@Param("customerId") Long customerId);


    @Query("SELECT new com.beautymeongdang.domain.quote.dto.GetGroomerSelectedQuoteResponseDto(" +
            "sq.selectedQuoteId, q.quoteId, d.profileImage, c.userId.userName, " +
            "c.userId.nickname, c.userId.phone, d.dogName, q.beautyDate, sq.status) " +
            "FROM SelectedQuote sq " +
            "JOIN sq.quoteId q " +
            "JOIN sq.customerId c " +
            "JOIN q.dogId d " +
            "WHERE q.groomerId.groomerId = :groomerId AND sq.isDeleted = false")
    List<GetGroomerSelectedQuoteResponseDto> findGroomerSelectedQuotes(@Param("groomerId") Long groomerId);

    @Query("SELECT new com.beautymeongdang.domain.quote.dto.GetSelectedQuoteDetailResponseDto(" +
            "c.userId.userName, g.userId.userName, s.shopName, s.address, g.userId.phone, " +
            "d.dogName, d.profileImage, d.dogBreed, d.dogWeight, d.dogAge, " +
            "CAST(d.dogGender AS string), " +
            "d.neutering, d.experience, d.significant, " +
            "q.quoteId, q.beautyDate, qr.content, q.content) " +
            "FROM SelectedQuote sq " +
            "JOIN sq.quoteId q " +
            "JOIN q.requestId qr " +
            "JOIN q.dogId d " +
            "JOIN q.groomerId g " +
            "JOIN Shop s ON s.groomerId = g " +
            "JOIN sq.customerId c " +
            "WHERE sq.selectedQuoteId = :selectedQuoteId AND sq.isDeleted = false")
    GetSelectedQuoteDetailResponseDto findQuoteDetailById(@Param("selectedQuoteId") Long selectedQuoteId);









}