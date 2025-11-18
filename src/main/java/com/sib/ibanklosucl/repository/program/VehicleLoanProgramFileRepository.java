package com.sib.ibanklosucl.repository.program;

import com.sib.ibanklosucl.model.VehicleLoanProgramFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleLoanProgramFileRepository extends JpaRepository<VehicleLoanProgramFile, Long> {

    /**
     * Find all files by applicant ID and work item number where delete flag is N
     */
    List<VehicleLoanProgramFile> findByApplicantIdAndWiNumAndDelFlg(Long applicantId, String wiNum, String delFlg);

    /**
     * Find all files by work item number and delete flag
     */
    List<VehicleLoanProgramFile> findByWiNumAndDelFlg(String wiNum, String delFlg);

    /**
     * Delete all files by applicant ID and work item number (soft delete - marks DEL_FLG as Y)
     */
    @Transactional
    @Modifying
    @Query("UPDATE VehicleLoanProgramFile v SET v.delFlg = 'Y' WHERE v.applicantId = ?1 AND v.wiNum = ?2")
    void softDeleteByApplicantIdAndWiNum(Long applicantId, String wiNum);

    /**
     * Delete all files by applicant ID and work item number (hard delete)
     */
    @Transactional
    void deleteByApplicantIdAndWiNum(Long applicantId, String wiNum);

    /**
     * Find file by ID and delete flag
     */
    Optional<VehicleLoanProgramFile> findByIdAndDelFlg(Long id, String delFlg);
}
