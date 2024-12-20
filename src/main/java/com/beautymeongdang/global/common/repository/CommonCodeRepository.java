package com.beautymeongdang.global.common.repository;

import com.beautymeongdang.global.common.entity.CommonCode;
import com.beautymeongdang.global.common.entity.CommonCodeId;
import com.beautymeongdang.global.common.entity.GroupCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCode, CommonCodeId> {
//    @Query("SELECT c FROM CommonCode c " +
//            "WHERE c.groupCode.groupCode = :groupCode " +
//            "AND c.commonCode = :commonCode")
//    Optional<CommonCode> findByGroupCodeAndCommonCode(
//            @Param("groupCode") String groupCode, @Param("commonCode") String commonCode);
    @Query("SELECT c FROM CommonCode c WHERE c.id.codeId = :code AND c.id.groupId = :groupCode")
    Optional<CommonCode> findByCodeAndGroupCode(@Param("code") String code, @Param("groupCode") String groupCode);

    // 반려견 견종 목록 조회
    List<CommonCode> findAllByGroupId(GroupCode groupId);
}
