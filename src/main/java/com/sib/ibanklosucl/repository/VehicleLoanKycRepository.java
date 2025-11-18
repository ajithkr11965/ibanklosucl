package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanKyc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface VehicleLoanKycRepository extends RevisionRepository<VehicleLoanKyc, Long,Long>, JpaRepository<VehicleLoanKyc, Long> {
    VehicleLoanKyc findByApplicantIdAndDelFlg(Long applicant_id,String delflag);

    @Query("SELECT v FROM VehicleLoanKyc v where v.wiNum = :wiNum and v.slno=:slno AND v.applicantId=:applicantId AND v.delFlg = 'N'")
    Optional<VehicleLoanKyc> findActiveVehicleLoanKyc(String wiNum,Long slno,Long applicantId);
}
