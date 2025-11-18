package com.sib.ibanklosucl.repository.integations;

import com.sib.ibanklosucl.model.integrations.SMSEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface SMSEmailRepository extends RevisionRepository<SMSEmail, Long,Long>,JpaRepository<SMSEmail, Long> {
    @Query("SELECT COUNT(e) FROM SMSEmail e WHERE e.slno = :slno AND e.reqType=:reqtype AND e.alertId=:alertId AND e.sentDate BETWEEN :startTime AND :endTime")
    long countBySlnoAndSendateWithin30Minutes(@Param("slno") Long slno,
                                              @Param("reqtype") String reqtype,
                                              @Param("startTime") Date startTime,
                                              @Param("endTime") Date endTime,
                                              @Param("alertId") String alertId
    );

}
