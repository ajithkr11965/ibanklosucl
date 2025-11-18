package com.sib.ibanklosucl.repository.program;

import com.sib.ibanklosucl.model.VehicleLoanProgram;
import com.sib.ibanklosucl.model.VehicleLoanProgramSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleLoanProgramSalaryRepository  extends RevisionRepository<VehicleLoanProgramSalary, Long,Long>,JpaRepository<VehicleLoanProgramSalary, Long> {
    List<VehicleLoanProgramSalary> findByApplicantIdAndWiNumAndDelFlg(Long ApplicantId, String wiNum, String delFlag);
    List<VehicleLoanProgramSalary> findByApplicantIdAndWiNum(Long ApplicantId, String wiNum);
    void deleteByVlprogramSal(VehicleLoanProgram vehicleLoanProgram);

    void deleteByApplicantIdAndWiNum(Long ApplicantId, String wiNum);
}
