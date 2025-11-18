package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanFcvCpvCfr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface VehicleLoanFcvCpvCfrDetailsRepository extends RevisionRepository<VehicleLoanFcvCpvCfr, Long,Long>, JpaRepository<VehicleLoanFcvCpvCfr, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM VehicleLoanFcvCpvCfr v WHERE v.wiNum = :wiNum AND v.slno = :slno")
    void deleteByWiNumAndSlno(String wiNum, Long slno);

}
