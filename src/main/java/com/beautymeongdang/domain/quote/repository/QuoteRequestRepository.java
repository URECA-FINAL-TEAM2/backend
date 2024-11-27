package com.beautymeongdang.domain.quote.repository;

import com.beautymeongdang.domain.quote.dto.GroomerDirectRequestListResponseDto;
import com.beautymeongdang.domain.quote.entity.QuoteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuoteRequestRepository extends JpaRepository<QuoteRequest, Long> {
    // '1:1 견적' 요청 조회
    @Query("SELECT qr FROM QuoteRequest qr " +
            "JOIN FETCH qr.dogId d " +
            "WHERE d.customerId.customerId = :customerId " +
            "AND qr.requestType = '020' " +
            "ORDER BY qr.createdAt DESC")
    List<QuoteRequest> findAllByCustomerId(@Param("customerId") Long customerId);

    // '전체 견적' 요청 조회
    @Query("SELECT DISTINCT qr FROM QuoteRequest qr " +
            "JOIN FETCH qr.dogId d " +
            "WHERE d.customerId.customerId = :customerId " +
            "AND qr.requestType = '010' " +
            "ORDER BY qr.createdAt DESC")
    List<QuoteRequest> findAllRequestsByCustomerId(@Param("customerId") Long customerId);

    // 미용사가 받은 1:1 요청 조회
    @Query(value = """
            SELECT new com.beautymeongdang.domain.quote.dto.GroomerDirectRequestListResponseDto(
                            qr.requestId,
                            u.nickname,
                            u.profileImage,
                            qr.createdAt,
                            qr.beautyDate,
                            d.dogBreed,
                            CAST(d.dogGender AS string),
                            d.dogWeight,
                            qr.content
                        )
                        FROM
                            QuoteRequest qr
                        LEFT JOIN
                            Quote q ON q.requestId = qr
                        JOIN
                            qr.dogId d
                        JOIN
                            d.customerId c
                        JOIN
                            c.userId u
                        JOIN
                            DirectQuoteRequest dqr ON dqr.directQuoteRequestId.requestId = qr
                        WHERE
                            q.requestId IS NULL
                          AND qr.isDeleted = false
                          AND qr.requestType = '020'
                          AND qr.status = '010'
                          AND dqr.directQuoteRequestId.groomerId.groomerId = :groomerId
           """)
    List<GroomerDirectRequestListResponseDto> findQuoteRequestsByGroomerId(@Param("groomerId") Long groomerId);


}
