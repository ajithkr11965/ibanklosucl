package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.LosDedupeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LosDedupeRepository extends RevisionRepository<LosDedupeEntity, Long,Long>, JpaRepository<LosDedupeEntity, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE LosDedupeEntity e SET e.activeFlag = 'N' WHERE e.applicantId = :appid")
    void updateActiveFlagToInactive(Long appid);

    List<LosDedupeEntity> findAllBySlnoAndDelFlagAndActiveFlag(Long slno, String delflg, String activeflg);


}
