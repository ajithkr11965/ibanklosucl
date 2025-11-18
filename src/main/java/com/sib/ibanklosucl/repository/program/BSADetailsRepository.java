package com.sib.ibanklosucl.repository.program;

import com.sib.ibanklosucl.model.VehicleLoanBSA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BSADetailsRepository extends RevisionRepository<VehicleLoanBSA, Long, Long>, JpaRepository<VehicleLoanBSA, Long> {
    List<VehicleLoanBSA> findByApplicantIdAndWiNumAndDelFlg(Long applicantId, String wiNum, String delFlg);

    @Query("SELECT v FROM VehicleLoanBSA v WHERE v.applicantId = :applicantId AND v.wiNum = :wiNum AND v.perfiosTransactionId = :perfiosTransactionId AND v.delFlg = 'N'")
    VehicleLoanBSA findByApplicantIdAndWiNumAndPerfiosTransactionId(@Param("applicantId") Long applicantId, @Param("wiNum") String wiNum, @Param("perfiosTransactionId") String perfiosTransactionId);

    @Query("SELECT v FROM VehicleLoanBSA v WHERE v.applicantId = :applicantId AND v.wiNum = :wiNum AND v.delFlg = 'N'")
    List<VehicleLoanBSA> findByApplicantIdAndWiNum(@Param("applicantId") Long applicantId, @Param("wiNum") String wiNum);

    @Query("SELECT v FROM VehicleLoanBSA v WHERE v.applicantId = :applicantId AND v.wiNum = :wiNum ")
    List<VehicleLoanBSA> findALLBSAByApplicantIdAndWiNum(@Param("applicantId") Long applicantId,  @Param("wiNum") String wiNum);


    void deleteByApplicantIdAndWiNum(Long aLong, String wiNum);
    @Query("SELECT v FROM VehicleLoanBSA v WHERE v.applicantId = :applicantId AND v.wiNum = :wiNum AND v.perfiosTransactionId IS NULL AND v.delFlg = 'N' ORDER BY v.timestamp DESC")
    List<VehicleLoanBSA> findByApplicantIdAndWiNumWithNullPerfiosTransactionId(@Param("applicantId") Long applicantId,  @Param("wiNum") String wiNum);

    VehicleLoanBSA findTopByApplicantIdAndWiNumAndPerfiosTransactionIdIsNotNullAndDelFlgOrderByTimestampDesc(
        Long applicantId, String wiNum, String delFlg);


    @Query("SELECT v FROM VehicleLoanBSA v " +
           "WHERE v.applicantId = :applicantId " +
           "AND v.wiNum = :wiNum " +
           "AND v.perfiosTransactionId IS NOT NULL " +
           "AND v.delFlg = 'N' " +
           "ORDER BY v.timestamp DESC")
    List<VehicleLoanBSA> findAllCompletedByApplicantIdAndWiNumOrderByTimestampDesc(
        @Param("applicantId") Long applicantId,
        @Param("wiNum") String wiNum
    );
    @Query("SELECT v FROM VehicleLoanBSA v " +
           "WHERE v.applicantId = :applicantId " +
           "AND v.wiNum = :wiNum " +
           "AND v.perfiosTransactionId IS NOT NULL " +
           "AND v.delFlg = 'N' " +
           "AND v.perfiosTransactionId != :latestPerfiosTransactionId")
    List<VehicleLoanBSA> findOlderCompletedRecords(
        @Param("applicantId") Long applicantId,
        @Param("wiNum") String wiNum,
        @Param("latestPerfiosTransactionId") String latestPerfiosTransactionId
    );
        List<VehicleLoanBSA> findByApplicantIdAndWiNumAndPerfiosTransactionIdIsNullAndDelFlgOrderByTimestampDesc(
        Long applicantId, String wiNum, String delFlg);

    boolean existsByApplicantIdAndWiNumAndDelFlg(Long applicantId, String wiNum, String delFlg);

        Optional<VehicleLoanBSA> findTopByApplicantIdAndWiNumAndDelFlgOrderByTimestampDesc(Long applicantId, String wiNum, String n);

    @Query(value = "SELECT * FROM VEHICLE_LOAN_BSA_DETAILS WHERE APPLICANT_ID = :applicantId AND WI_NUM = :wiNum " +
               "AND DEL_FLG = :delFlg AND STATEMENT_TYPE IN :statementTypes " +
               "AND PROGRAM_INO IS NULL", nativeQuery = true)
List<VehicleLoanBSA> findRecordsWithNullProgramIno(
    @Param("applicantId") Long applicantId,
    @Param("wiNum") String wiNum,
    @Param("delFlg") String delFlg,
    @Param("statementTypes") List<String> statementTypes
);


}
