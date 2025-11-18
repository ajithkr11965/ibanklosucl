package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanTat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleLoanTatRepository extends RevisionRepository<VehicleLoanTat, Long,Long>, JpaRepository<VehicleLoanTat, Long> {

    VehicleLoanTat findAllByQueueExitDateIsNullAndSlnoAndDelFlg(Long slno,String delflg);

      @Query("SELECT v FROM VehicleLoanTat v WHERE v.wiNum = :wiNum AND v.queueExitDate IS NOT NULL ORDER BY v.queueExitDate DESC")
      List<VehicleLoanTat> findPreviousQueue(@Param("wiNum") String wiNum);
}

