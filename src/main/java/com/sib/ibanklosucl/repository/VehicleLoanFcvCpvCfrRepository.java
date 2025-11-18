package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanFcvCpvCfr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleLoanFcvCpvCfrRepository extends RevisionRepository<VehicleLoanFcvCpvCfr, Long, Long>, JpaRepository<VehicleLoanFcvCpvCfr, Long> {
    Optional<VehicleLoanFcvCpvCfr> findByWiNum(String wiNum);
    Optional<VehicleLoanFcvCpvCfr> findBySlnoAndDelFlg(Long slno, String DelFlg);

    @Query("SELECT v FROM VehicleLoanFcvCpvCfr v WHERE v.slno = :slno AND v.delFlg = 'N' ORDER BY v.cmdate DESC")
    Optional<VehicleLoanFcvCpvCfr> findLatestBySlno(Long slno);
}
