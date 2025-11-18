package com.sib.ibanklosucl.repository.program;

import com.sib.ibanklosucl.model.VehicleLoanProgramNRI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface VehicleLoanProgramNRIRepository extends RevisionRepository<VehicleLoanProgramNRI, Long,Long>, JpaRepository<VehicleLoanProgramNRI, Long> {
    List<VehicleLoanProgramNRI> findByApplicantIdAndWiNumAndDelFlg(Long ApplicantId, String wiNum, String delFlag);
    List<VehicleLoanProgramNRI> findByApplicantIdAndWiNum(Long ApplicantId, String wiNum);

    void deleteByApplicantIdAndWiNum(Long ApplicantId, String wiNum);
}
