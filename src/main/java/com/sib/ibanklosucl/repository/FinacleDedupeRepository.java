package com.sib.ibanklosucl.repository;


import com.sib.ibanklosucl.model.FinDedupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FinacleDedupeRepository extends RevisionRepository<FinDedupEntity, Long,Long>, JpaRepository<FinDedupEntity, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE FinDedupEntity e SET e.activeFlag = 'N' WHERE e.applicantId = :appid")
    void updateActiveFlagToInactive(Long appid);


    List<FinDedupEntity> findAllBySlnoAndDelFlagAndActiveFlagAndDedupflag(Long slno, String delflg, String activeflg, String dedupflg);
    List<FinDedupEntity> findAllBySlnoAndDelFlagAndActiveFlag(Long slno, String delflg, String activeflg);
    List<FinDedupEntity> findAllBySlnoAndDelFlagAndActiveFlagAndApplicantId(Long slno, String delflg, String activeflg,Long appid);
    Long countBySlnoAndDelFlagAndActiveFlag(Long slno, String delflg, String activeflg);

}
