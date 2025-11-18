package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanBasic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;


public interface VehicleLoanBasicRepository extends RevisionRepository<VehicleLoanBasic, Long,Long>, JpaRepository<VehicleLoanBasic, Long> {
    VehicleLoanBasic findByApplicantIdAndDelFlg(Long applicant_id, String delflag);

    @Query("select e.state from VehicleLoanBasic e join VehicleLoanApplicant a on a.applicantId=e.applicantId where a.delFlg='N' and  a.applicantType='A' and e.slno=:slno and e.delFlg='N'")
    String findAppState(Long slno);
}
