package com.beautymeongdang.domain.quote.repository;


import com.beautymeongdang.domain.quote.dto.GetGroomerQuoteRequestResponseDto;
import com.beautymeongdang.domain.quote.dto.GetGroomerSendQuoteRequestResponseDto;
import com.beautymeongdang.domain.quote.entity.QuoteRequest;
import com.beautymeongdang.domain.user.dto.GetMainGroomerResponseDto;
import com.beautymeongdang.domain.user.dto.GetMainGroomerTotalRequestResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface QuoteRequestRepository extends JpaRepository<QuoteRequest, Long> {
    // '1:1 견적' 요청 조회
    @Query("SELECT qr FROM QuoteRequest qr " +
            "JOIN FETCH qr.dogId d " +
            "WHERE d.customerId.customerId = :customerId " +
            "AND qr.requestType = '020' " +
            "AND qr.isDeleted = false " +
            "ORDER BY qr.createdAt DESC")
    List<QuoteRequest> findAllByCustomerId(@Param("customerId") Long customerId);

    // '전체 견적' 요청 조회
    @Query("SELECT DISTINCT qr FROM QuoteRequest qr " +
            "JOIN FETCH qr.dogId d " +
            "WHERE d.customerId.customerId = :customerId " +
            "AND qr.requestType = '010' " +
            "AND qr.isDeleted = false " +
            "ORDER BY qr.createdAt DESC")
    List<QuoteRequest> findAllRequestsByCustomerId(@Param("customerId") Long customerId);

    // 미용사가 받은 1:1 요청 조회
    @Query(value = """
            SELECT new com.beautymeongdang.domain.quote.dto.GetGroomerQuoteRequestResponseDto(
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
    List<GetGroomerQuoteRequestResponseDto> findQuoteRequestsByGroomerId(@Param("groomerId") Long groomerId);

    // 미용사 매장 근처 견적서 요청 공고 조회
    @Query(value = """
            SELECT new com.beautymeongdang.domain.quote.dto.GetGroomerQuoteRequestResponseDto(
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
                            TotalQuoteRequest tqr ON tqr.requestId = qr
                        WHERE
                            q.requestId IS NULL
                          AND qr.isDeleted = false
                          AND qr.requestType = '010'
                          AND qr.status = '010'
                          AND tqr.sigunguId.sigunguId = :sigunguId
                        ORDER BY
                            qr.createdAt DESC
           """)
    List<GetGroomerQuoteRequestResponseDto> findQuoteRequestsBySigunguId(@Param("sigunguId") Long sigunguId);
           
    // 미용사가 견적서 보낸 견적 요청 조회
    @Query(value = """
            SELECT new com.beautymeongdang.domain.quote.dto.GetGroomerSendQuoteRequestResponseDto(
                            qr.requestId,
                            u.nickname,
                            u.profileImage,
                            qr.beautyDate,
                            d.dogBreed,
                            CAST(d.dogGender AS string),
                            d.dogWeight,
                            qr.content,
                            qr.requestType
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
                        WHERE
                            q.requestId IS NOT NULL
                          AND qr.isDeleted = false
                          AND qr.status = '010'
                          AND q.groomerId.groomerId = :groomerId
                        ORDER BY
                            q.createdAt DESC
           """)
    List<GetGroomerSendQuoteRequestResponseDto> findSendQuoteRequestsByGroomerId(@Param("groomerId") Long groomerId);

    // 미용사 메인 페이지 - 전체 1:1 견적 요청 건수 조회
    @Query(value = """
            SELECT count (*) as totalDirectRequest
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
                        WHERE qr.isDeleted = false
                          AND qr.requestType = '020'
                          AND dqr.directQuoteRequestId.groomerId.groomerId = :groomerId
           """)
    Integer countTotalDirectRequest(@Param("groomerId") Long groomerId);

    // 미용사 메인 페이지 - 오늘의 1:1 견적 요청 건수 조회
    @Query(value = """
        SELECT COUNT(qr) AS todayRequest
        FROM QuoteRequest qr
        LEFT JOIN Quote q ON q.requestId = qr
        JOIN qr.dogId d
        JOIN d.customerId c
        JOIN c.userId u
        JOIN DirectQuoteRequest dqr ON dqr.directQuoteRequestId.requestId = qr
        WHERE qr.isDeleted = false
          AND qr.requestType = '020'
          AND dqr.directQuoteRequestId.groomerId.groomerId = :groomerId
          AND FUNCTION('DATE', qr.createdAt) = FUNCTION('DATE', :todayDate)
    """)
    Integer countTodayRequests(@Param("groomerId") Long groomerId, @Param("todayDate") LocalDateTime todayDate);

    // 미용사 메인 페이지 - 견적서 미발송 건수 조회
    @Query(value = """
            SELECT COUNT(qr) AS unsentQuote
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
    Integer countUnsentQuote(@Param("groomerId") Long groomerId);

    // 미용사 메인 페이지 - 우리동네 견적 공고
    @Query(value = """
        SELECT new com.beautymeongdang.domain.user.dto.GetMainGroomerTotalRequestResponseDto(
                    qr.requestId,
                    u.userName,
                    u.profileImage,
                    qr.createdAt,
                    qr.beautyDate,
                    cc.commonName,
                    CAST(d.dogGender AS string),
                    d.dogWeight,
                    qr.content
               )
        FROM
            QuoteRequest qr
        LEFT JOIN Quote q ON q.requestId = qr
        JOIN qr.dogId d
        JOIN CommonCode cc ON cc.id.codeId = d.dogBreed AND cc.id.groupId = '400'
        JOIN GroupCode gc ON gc.groupId = cc.id.groupId
        JOIN d.customerId c
        JOIN c.userId u
        JOIN TotalQuoteRequest tqr ON tqr.requestId = qr
        WHERE
            q.requestId IS NULL
          AND qr.isDeleted = false
          AND qr.requestType = '010'
          AND qr.status = '010'
          AND tqr.sigunguId.sigunguId = :sigunguId
        ORDER BY
            qr.createdAt DESC
       LIMIT 3
    """)
    List<GetMainGroomerTotalRequestResponseDto> findTop3LatestRequestsBySigunguId(@Param("sigunguId") Long sigunguId);

    // 미용사 프로필 논리적 삭제
    List<QuoteRequest> findAllByRequestType(String requestType);
}
