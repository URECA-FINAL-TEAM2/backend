package com.beautymeongdang.global.config;

import com.beautymeongdang.global.common.entity.CommonCode;
import com.beautymeongdang.global.common.entity.CommonCodeId;
import com.beautymeongdang.global.common.entity.GroupCode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class CodeInitializer implements CommandLineRunner {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void run(String... args) {
        List<GroupCode> groupCodes = em.createQuery("SELECT gc FROM GroupCode gc", GroupCode.class)
                .getResultList();

        if (!groupCodes.isEmpty()) {
            return;
        }

        // 요청 관련 그룹
        GroupCode requestGroup = new GroupCode("100", "요청 관리");
        em.persist(requestGroup);

        // 요청 상태
        em.persist(new CommonCode(new CommonCodeId("010", "100"), requestGroup, "요청", true));
        em.persist(new CommonCode(new CommonCodeId("020", "100"), requestGroup, "거절", true));
        em.persist(new CommonCode(new CommonCodeId("030", "100"), requestGroup, "마감", true));
        em.persist(new CommonCode(new CommonCodeId("040", "100"), requestGroup, "제안 완료", true));

        // 견적서 관련 그룹
        GroupCode quotationGroup = new GroupCode("200", "견적서 관리");
        em.persist(quotationGroup);

        // 견적서 상태
        em.persist(new CommonCode(new CommonCodeId("010", "200"), quotationGroup, "제안", true));
        em.persist(new CommonCode(new CommonCodeId("020", "200"), quotationGroup, "수락", true));
        em.persist(new CommonCode(new CommonCodeId("030", "200"), quotationGroup, "마감", true));

        // 예약 관련 그룹
        GroupCode reservationGroup = new GroupCode("250", "예약 관리");
        em.persist(reservationGroup);

        // 예약 상태
        em.persist(new CommonCode(new CommonCodeId("010", "250"), reservationGroup, "예약 완료", true));
        em.persist(new CommonCode(new CommonCodeId("020", "250"), reservationGroup, "예약 취소", true));
        em.persist(new CommonCode(new CommonCodeId("030", "250"), reservationGroup, "미용 완료", true));

        // 결제 관련 그룹
        GroupCode paymentGroup = new GroupCode("300", "결제 관리");
        em.persist(paymentGroup);

        // 결제 상태
        em.persist(new CommonCode(new CommonCodeId("010", "300"), paymentGroup, "결제 대기", true));
        em.persist(new CommonCode(new CommonCodeId("020", "300"), paymentGroup, "결제 완료", true));
        em.persist(new CommonCode(new CommonCodeId("030", "300"), paymentGroup, "결제 취소", true));
        em.persist(new CommonCode(new CommonCodeId("040", "300"), paymentGroup, "결제 실패", true));
        em.persist(new CommonCode(new CommonCodeId("050", "300"), paymentGroup, "결제 취소 실패", true));

        

        // 견종 관련 그룹
        GroupCode dogBreedGroup = new GroupCode("400", "견종 관리");
        em.persist(dogBreedGroup);

        // 견종 목록
        em.persist(new CommonCode(new CommonCodeId("010", "400"), dogBreedGroup, "말티즈", true));
        em.persist(new CommonCode(new CommonCodeId("020", "400"), dogBreedGroup, "푸들", true));
        em.persist(new CommonCode(new CommonCodeId("030", "400"), dogBreedGroup, "포메라니안", true));
        em.persist(new CommonCode(new CommonCodeId("040", "400"), dogBreedGroup, "치와와", true));
        em.persist(new CommonCode(new CommonCodeId("050", "400"), dogBreedGroup, "시츄", true));
        em.persist(new CommonCode(new CommonCodeId("060", "400"), dogBreedGroup, "비숑프리제", true));
        em.persist(new CommonCode(new CommonCodeId("070", "400"), dogBreedGroup, "닥스훈트", true));
        em.persist(new CommonCode(new CommonCodeId("080", "400"), dogBreedGroup, "요크셔테리어", true));
        em.persist(new CommonCode(new CommonCodeId("090", "400"), dogBreedGroup, "비글", true));
        em.persist(new CommonCode(new CommonCodeId("100", "400"), dogBreedGroup, "골든 리트리버", true));
        em.persist(new CommonCode(new CommonCodeId("110", "400"), dogBreedGroup, "시베리안 허스키", true));
        em.persist(new CommonCode(new CommonCodeId("120", "400"), dogBreedGroup, "진돗개", true));
        em.persist(new CommonCode(new CommonCodeId("130", "400"), dogBreedGroup, "웰시코기", true));
        em.persist(new CommonCode(new CommonCodeId("140", "400"), dogBreedGroup, "시바견", true));
        em.persist(new CommonCode(new CommonCodeId("150", "400"), dogBreedGroup, "비숑", true));
        em.persist(new CommonCode(new CommonCodeId("160", "400"), dogBreedGroup, "도베르만", true));
        em.persist(new CommonCode(new CommonCodeId("170", "400"), dogBreedGroup, "사모예드", true));
        em.persist(new CommonCode(new CommonCodeId("180", "400"), dogBreedGroup, "삽살개", true));
        em.persist(new CommonCode(new CommonCodeId("190", "400"), dogBreedGroup, "불독", true));
        em.persist(new CommonCode(new CommonCodeId("200", "400"), dogBreedGroup, "차우차우", true));
        em.persist(new CommonCode(new CommonCodeId("210", "400"), dogBreedGroup, "꼬똥", true));
        em.persist(new CommonCode(new CommonCodeId("220", "400"), dogBreedGroup, "퍼그", true));
        em.persist(new CommonCode(new CommonCodeId("230", "400"), dogBreedGroup, "말라뮤트", true));
        em.persist(new CommonCode(new CommonCodeId("240", "400"), dogBreedGroup, "믹스", true));
        em.persist(new CommonCode(new CommonCodeId("250", "400"), dogBreedGroup, "보더콜리", true));
        em.persist(new CommonCode(new CommonCodeId("260", "400"), dogBreedGroup, "슈나우저", true));
        em.persist(new CommonCode(new CommonCodeId("270", "400"), dogBreedGroup, "달마시안", true));
        em.persist(new CommonCode(new CommonCodeId("280", "400"), dogBreedGroup, "셰퍼드", true));
        em.persist(new CommonCode(new CommonCodeId("290", "400"), dogBreedGroup, "그레이하운드", true));
        em.persist(new CommonCode(new CommonCodeId("300", "400"), dogBreedGroup, "기타", true));

        // 회원 관련 그룹
        GroupCode memberGroup = new GroupCode("500", "회원 관리");
        em.persist(memberGroup);

        // 회원 역할
        em.persist(new CommonCode(new CommonCodeId("010", "500"), memberGroup, "고객", true));
        em.persist(new CommonCode(new CommonCodeId("020", "500"), memberGroup, "미용사", true));

        // 소셜 로그인 관련 그룹
        GroupCode socialGroup = new GroupCode("600", "소셜 로그인");
        em.persist(socialGroup);

        // 소셜 로그인 제공자
        em.persist(new CommonCode(new CommonCodeId("010", "600"), socialGroup, "카카오", true));
        em.persist(new CommonCode(new CommonCodeId("020", "600"), socialGroup, "구글", true));

        // 고객 알림 관련 그룹
        GroupCode customerNotiGroup = new GroupCode("700", "고객 알림");
        em.persist(customerNotiGroup);

        // 고객 알림 구분
        em.persist(new CommonCode(new CommonCodeId("010", "700"), customerNotiGroup, "견적서 제안알림", true));
        em.persist(new CommonCode(new CommonCodeId("020", "700"), customerNotiGroup, "예약 알림", true));
        em.persist(new CommonCode(new CommonCodeId("030", "700"), customerNotiGroup, "채팅 알림", true));

        // 미용사 알림 관련 그룹
        GroupCode groomerNotiGroup = new GroupCode("800", "미용사 알림");
        em.persist(groomerNotiGroup);

        // 미용사 알림 구분
        em.persist(new CommonCode(new CommonCodeId("010", "800"), groomerNotiGroup, "견적서 요청알림", true));
        em.persist(new CommonCode(new CommonCodeId("020", "800"), groomerNotiGroup, "예약 알림", true));
        em.persist(new CommonCode(new CommonCodeId("030", "800"), groomerNotiGroup, "채팅 알림", true));
        em.persist(new CommonCode(new CommonCodeId("040", "800"), groomerNotiGroup, "리뷰 알림", true));


        // 전체 요청인지 1:1 요청인지
        GroupCode AllorGroomerGroup = new GroupCode("900", "요청 타입 구분");
        em.persist(AllorGroomerGroup);

        em.persist(new CommonCode(new CommonCodeId("010", "900"), AllorGroomerGroup, "전체요청", true));
        em.persist(new CommonCode(new CommonCodeId("020", "900"), AllorGroomerGroup, "1:1요청", true));



        em.flush();
    }
}