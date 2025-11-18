package com.sib.ibanklosucl.repository.integations;

import com.sib.ibanklosucl.model.VehicleLoanSingleDedupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VLSingleDedupeRepository extends RevisionRepository<VehicleLoanSingleDedupe, Long, Long>, JpaRepository<VehicleLoanSingleDedupe, Long> {
    Optional<VehicleLoanSingleDedupe> findByApplicantIdAndDelFlg(Long applicantId, String delFlg);

    @Query("SELECT v FROM VehicleLoanSingleDedupe v WHERE v.applicantId = :applicantId AND v.delFlg = 'N' ORDER BY v.cmDate DESC")
    List<VehicleLoanSingleDedupe> findAllActiveByApplicantIdOrderByCmDateDesc(@Param("applicantId") Long applicantId);

    @Query("SELECT COUNT(v) > 0 FROM VehicleLoanSingleDedupe v WHERE v.applicantId = :applicantId AND v.delFlg = 'N'")
    boolean existsActiveByApplicantId(@Param("applicantId") Long applicantId);

    @Query("SELECT v FROM VehicleLoanSingleDedupe v WHERE v.wiNum = :wiNum AND v.delFlg = 'N'")
    Optional<VehicleLoanSingleDedupe> findActiveByWiNum(@Param("wiNum") String wiNum);

    @Query("SELECT v FROM VehicleLoanSingleDedupe v WHERE v.delFlg = 'N'")
    List<VehicleLoanSingleDedupe> findAllActive();
     @Query("SELECT d FROM VehicleLoanSingleDedupe d " +
           "WHERE d.wiNum = :wiNum " +
           "AND d.slNo = :slNo " +
           "AND d.delFlg = :delFlg")
    List<VehicleLoanSingleDedupe> findByWiNumAndSlNoAndDelFlg(
        @Param("wiNum") String wiNum,
        @Param("slNo") Long slNo,
        @Param("delFlg") String delFlg
    );

    /**
     * Optional: More specific query that directly checks for pending relations
     * This could replace the filtering in the service layer if you prefer
     */
    @Query("SELECT d FROM VehicleLoanSingleDedupe d " +
           "WHERE d.wiNum = :wiNum " +
           "AND d.slNo = :slNo " +
           "AND d.delFlg = :delFlg " +
           "AND d.checkResult = 'Match Found' " +
           "AND (d.relation IS NULL OR d.relation = '')")
    List<VehicleLoanSingleDedupe> findPendingDedupeRelations(
        @Param("wiNum") String wiNum,
        @Param("slNo") Long slNo,
        @Param("delFlg") String delFlg
    );
}
