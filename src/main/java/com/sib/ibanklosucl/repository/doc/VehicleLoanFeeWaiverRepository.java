package com.sib.ibanklosucl.repository.doc;

import com.sib.ibanklosucl.model.doc.VehicleLoanChargeWaiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface VehicleLoanFeeWaiverRepository extends RevisionRepository<VehicleLoanChargeWaiver, Long,Long>, JpaRepository<VehicleLoanChargeWaiver, Long> {
    @Modifying
    @Transactional
    @Query("update VehicleLoanChargeWaiver v set v.delFlag='Y' where v.slno=:slno")
     void updateDelflag(Long slno);

    @Query("SELECT v FROM VehicleLoanChargeWaiver v WHERE v.taskId = :taskId")
    List<VehicleLoanChargeWaiver> findByTaskId(@Param("taskId") Long taskId);

    List<VehicleLoanChargeWaiver> findByTaskIdAndWaiverFlg(Long taskId, String waiverFlg);

    List<VehicleLoanChargeWaiver> findBySlnoOrderByInoDesc(Long slno);
    List<VehicleLoanChargeWaiver> findBySlnoAndDelFlagOrderByInoDesc(Long slno,String delFlag);
}

