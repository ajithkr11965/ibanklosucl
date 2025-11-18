package com.sib.ibanklosucl.repository.program;

import com.sib.ibanklosucl.model.VehicleLoanITR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ITRAlertRepository extends RevisionRepository<VehicleLoanITR, Long, Long>, JpaRepository<VehicleLoanITR, Long> {
    List<VehicleLoanITR> findByMobileNo(String mobileNo);

    List<VehicleLoanITR> findByApplicantIdAndWiNumAndDelFlg(Long applicantId, String wiNum, String delFlg);

    @Query("SELECT v FROM VehicleLoanITR v WHERE v.applicantId = :applicantId AND v.wiNum = :wiNum AND v.perfiosTransactionId = :perfiosTransactionId AND v.delFlg = 'N'")
    VehicleLoanITR findByApplicantIdAndWiNumAndPerfiosTransactionId(@Param("applicantId") Long applicantId, @Param("wiNum") String wiNum, @Param("perfiosTransactionId") String perfiosTransactionId);

    @Query("SELECT v FROM VehicleLoanITR v WHERE v.applicantId = :applicantId AND v.wiNum = :wiNum AND v.delFlg = 'N'")
    List<VehicleLoanITR> findByApplicantIdAndWiNum(@Param("applicantId") Long applicantId, @Param("wiNum") String wiNum);
    @Query("SELECT v FROM VehicleLoanITR v WHERE v.applicantId = :applicantId AND v.wiNum = :wiNum")
    List<VehicleLoanITR> findALLITRByApplicantIdAndWiNum(@Param("applicantId") Long applicantId, @Param("wiNum") String wiNum);


    void deleteByApplicantIdAndWiNum(Long applicantId, String wiNum);

    //  @Query("SELECT v FROM VehicleLoanITR v WHERE v.applicantId = :applicantId AND v.wiNum = :wiNum AND (v.perfiosTransactionId IS NULL OR v.fetchResponse is null ) AND v.delFlg = :delFlg order by v.timestamp desc")
    @Query("SELECT v FROM VehicleLoanITR v WHERE v.applicantId = :applicantId AND v.wiNum = :wiNum AND  v.delFlg = :delFlg order by v.timestamp desc")
    List<VehicleLoanITR> findByApplicantIdAndWiNumAndPerfiosTransactionIdIsNullOrFetchResponseIsNullAndDelFlgOrderByTimestampDesc(
            Long applicantId, String wiNum, String delFlg);

    VehicleLoanITR findTopByApplicantIdAndWiNumAndPerfiosTransactionIdIsNotNullAndDelFlgOrderByTimestampDesc(
            Long applicantId, String wiNum, String delFlg);

    List<VehicleLoanITR> findByApplicantIdAndWiNumAndDelFlgAndTimestampBeforeAndPerfiosTransactionIdIsNull(
            Long applicantId, String wiNum, String delFlg, Date timestamp);

    List<VehicleLoanITR> findByApplicantIdAndWiNumAndDelFlgAndTimestampAfterAndPerfiosTransactionIdIsNullOrderByTimestampDesc(
            Long applicantId, String wiNum, String delFlg, Date timestamp);

    boolean existsByApplicantIdAndWiNumAndDelFlg(Long applicantId, String wiNum, String delFlg);

    Optional<VehicleLoanITR> findTopByApplicantIdAndWiNumAndDelFlgOrderByTimestampDesc(Long applicantId, String wiNum, String n);

    VehicleLoanITR findByApplicantIdAndWiNumAndClientTxnIdAndPerfiosTransactionIdIsNull(Long applicantId, String wiNum, String clientTxnId);

    VehicleLoanITR findTopByApplicantIdAndWiNumAndClientTxnIdAndDelFlgOrderByTimestampDesc(Long applicantId, String wiNum, String clientTxnId, String delFlg);
}
