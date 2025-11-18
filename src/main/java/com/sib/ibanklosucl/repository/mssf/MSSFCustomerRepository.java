package com.sib.ibanklosucl.repository.mssf;

import com.sib.ibanklosucl.dto.mssf.MSSFCustomerSummaryDTO;
import com.sib.ibanklosucl.model.mssf.MSSFCustomerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MSSFCustomerRepository extends RevisionRepository<MSSFCustomerData, Long, Long>, JpaRepository<MSSFCustomerData, Long> {

    @Query("SELECT m, COALESCE(l.lockFlg, 'N') as lockFlg, COALESCE(l.lockedBy, '') as lockedBy " +
            "FROM MSSFCustomerData m " +
            "LEFT JOIN MSSFLock l ON m.refNo = l.refNo " +
            "WHERE (m.status is null or m.status = 'PENDING') " +
            "AND EXISTS (SELECT 1 FROM MSSFDealerSol d WHERE d.dlrCode = m.dlrCode AND d.solId = :solId AND d.delFlg = 'N')")
    List<Object[]> findPendingApplicationsBySolId(@Param("solId") String solId);

    @Query("SELECT m, COALESCE(l.lockFlg, 'N') as lockFlg, COALESCE(l.lockedBy, '') as lockedBy " +
            "FROM MSSFCustomerData m " +
            "LEFT JOIN MSSFLock l ON m.refNo = l.refNo " +
            "WHERE (m.status is null or m.status = 'PENDING') " +
            "AND EXISTS (SELECT 1 FROM MSSFDealerSol d WHERE d.dlrCode = m.dlrCode AND d.solId IN :solId AND d.delFlg = 'N')" +
            "AND NOT EXISTS (SELECT 1 FROM VehicleLoanMaster v WHERE v.refNo = m.refNo)")
    List<Object[]> findPendingApplicationsBySol(@Param("solId") List<String> solId);


    @Query("SELECT m FROM MSSFCustomerData m WHERE m.refNo = :refNo")
    Optional<MSSFCustomerData> findByRefNo(@Param("refNo") String refNo);

    @Modifying
    @Transactional
    @Query("UPDATE MSSFCustomerData e SET e.status = 'I' WHERE e.refNo = :refNo")
    void updateMSSFStatusToActive(String refNo);

    @Query(value = "SELECT m.*, " +
            "COALESCE(l.LOCK_FLG, 'N') as lockFlg, " +
            "COALESCE(l.LOCKED_BY, '') as lockedBy, " +
            "d.SOL_ID as solId, " +
            "d.DEALER_NAME_MSSF as dealerName, " +
            "COALESCE(v.WI_NUM, '') as workItemNumber, " +
            "COALESCE(q.CODEDESC, v.QUEUE) as workItemStatus, " +
            "b.BR_NAME as bankName " +
            "FROM MSSF_CUSTOMER_DATA m " +
            "LEFT JOIN MSSF_LOCK l ON m.REF_NO = l.REF_NO " +
            "LEFT JOIN MSSF_DEALER_SOL d ON m.DLR_CODE = d.DLR_CODE AND d.DEL_FLG = 'N' " +
            "LEFT JOIN VEHICLE_LOAN_MASTER v ON m.REF_NO = v.REF_NO " +
            "LEFT JOIN misbmt@mybank b ON d.SOL_ID = b.SOL_ID " +
            "LEFT JOIN MIS_REFERENCE_CODE q ON v.QUEUE = q.CODEVALUE AND q.CODETYPE = 'QT' AND q.DELFLAG = 'N' " +
            "ORDER BY m.CREATED_DATE DESC",
            nativeQuery = true)
    List<Object[]> findAllMSSFCustomersWithLockDetails();


    // New queries for email alerts - using basic 6-parameter constructor
    @Query("SELECT new com.sib.ibanklosucl.dto.mssf.MSSFCustomerSummaryDTO(" +
            "m.refNo, " +
            "CONCAT(COALESCE(m.pdSalutation, ''), ' ', COALESCE(m.pdFirstName, ''), ' ', COALESCE(m.pdLastName, '')), " +
            "m.pdMobile, m.laLoanAmt, m.createdDate, m.dlrCode,d.dealerNameCode) " +
            "FROM MSSFCustomerData m " +
            "LEFT JOIN MSSFDealerSol d ON m.dlrCode = d.dlrCode AND d.delFlg = 'N' " +
            "WHERE d.solId = :solId " +
            "AND m.createdDate >= :fromDate " +
            "ORDER BY m.createdDate DESC")
    List<MSSFCustomerSummaryDTO> findNewLeadsBySolAndDateold(@Param("solId") String solId,
                                                             @Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT new com.sib.ibanklosucl.dto.mssf.MSSFCustomerSummaryDTO(" +
            "m.refNo, " +
            "CONCAT(COALESCE(m.pdSalutation, ''), ' ', COALESCE(m.pdFirstName, ''), ' ', COALESCE(m.pdLastName, '')), " +
            "m.pdMobile, m.laLoanAmt, m.createdDate, m.dlrCode) " +
            "FROM MSSFCustomerData m " +
            "LEFT JOIN MSSFDealerSol d ON m.dlrCode = d.dlrCode AND d.delFlg = 'N' " +
            "WHERE d.solId = :solId " +
            "AND (m.status IS NULL OR m.status = '' OR m.status = 'PENDING') " +
            "AND m.createdDate >= :fromDate " +
            "ORDER BY m.createdDate DESC")
    List<MSSFCustomerSummaryDTO> findPendingApplicationsBySolAndDate(@Param("solId") String solId,
                                                                     @Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT new com.sib.ibanklosucl.dto.mssf.MSSFCustomerSummaryDTO(" +
            "m.refNo, " +
            "CONCAT(COALESCE(m.pdSalutation, ''), ' ', COALESCE(m.pdFirstName, ''), ' ', COALESCE(m.pdLastName, '')), " +
            "m.pdMobile, m.laLoanAmt, m.createdDate, m.dlrCode) " +
            "FROM MSSFCustomerData m " +
            "LEFT JOIN MSSFDealerSol d ON m.dlrCode = d.dlrCode AND d.delFlg = 'N' " +
            "WHERE d.solId = :solId " +
            "AND (m.status IS NULL OR m.status = '' OR m.status = 'PENDING') " +
            "AND m.createdDate <= :beforeDate " +
            "ORDER BY m.createdDate ASC")
    List<MSSFCustomerSummaryDTO> findPendingApplicationsBySolOlderThanold(@Param("solId") String solId,
                                                                          @Param("beforeDate") LocalDateTime beforeDate);

    // Alternative queries returning Object[] for manual mapping
    @Query("SELECT m.refNo, " +
            "CONCAT(COALESCE(m.pdSalutation, ''), ' ', COALESCE(m.pdFirstName, ''), ' ', COALESCE(m.pdLastName, '')), " +
            "m.pdMobile, m.pdEmail, m.laLoanAmt, m.createdDate, m.dlrCode, d.dealerNameMssf " +
            "FROM MSSFCustomerData m " +
            "LEFT JOIN MSSFDealerSol d ON m.dlrCode = d.dlrCode AND d.delFlg = 'N' " +
            "WHERE d.solId = :solId " +
            "AND m.createdDate >= :fromDate " +
            "ORDER BY m.createdDate DESC")
    List<Object[]> findNewLeadsBySolAndDateRaw(@Param("solId") String solId,
                                               @Param("fromDate") LocalDateTime fromDate);

    // Summary queries for reporting
    @Query("SELECT d.solId, COUNT(m.refNo) " +
            "FROM MSSFCustomerData m " +
            "LEFT JOIN MSSFDealerSol d ON m.dlrCode = d.dlrCode AND d.delFlg = 'N' " +
            "WHERE m.createdDate >= :fromDate " +
            "GROUP BY d.solId")
    List<Object[]> findNewLeadsCountBySolSince(@Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT d.solId, COUNT(m.refNo) " +
            "FROM MSSFCustomerData m " +
            "LEFT JOIN MSSFDealerSol d ON m.dlrCode = d.dlrCode AND d.delFlg = 'N' " +
            "WHERE (m.status IS NULL OR m.status = '' OR m.status = 'PENDING') " +
            "AND m.createdDate <= :beforeDate " +
            "GROUP BY d.solId")
    List<Object[]> findPendingCountsBySolOlderThan(@Param("beforeDate") LocalDateTime beforeDate);


    // Get active SOL IDs from MSSFDealerSol table
    @Query("SELECT DISTINCT d.solId FROM MSSFDealerSol d WHERE d.delFlg = 'N' ORDER BY d.solId")
    List<String> findActiveSolIds();

    @Query("SELECT new com.sib.ibanklosucl.dto.mssf.MSSFCustomerSummaryDTO(" +
            "m.refNo, " +
            "CONCAT(COALESCE(m.pdSalutation, ''), ' ', COALESCE(m.pdFirstName, ''), ' ', COALESCE(m.pdLastName, '')), " +
            "m.pdMobile, m.laLoanAmt, m.createdDate, m.dlrCode, " +
            "CONCAT(COALESCE(d.dealerNameMssf, 'Unknown Dealer'), ' - ', COALESCE(d.dealerLocation, 'Unknown Location'))) " +
            "FROM MSSFCustomerData m " +
            "LEFT JOIN MSSFDealerSol d ON m.dlrCode = d.dlrCode AND d.delFlg = 'N' " +
            "WHERE d.solId = :solId " +
            "AND (m.status IS NULL OR m.status = '' OR m.status = 'PENDING') " +
            "AND m.createdDate >= :fromDate " +
            "ORDER BY m.createdDate DESC")
    List<MSSFCustomerSummaryDTO> findNewLeadsBySolAndDate(@Param("solId") String solId,
                                                          @Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT new com.sib.ibanklosucl.dto.mssf.MSSFCustomerSummaryDTO(" +
            "m.refNo, " +
            "CONCAT(COALESCE(m.pdSalutation, ''), ' ', COALESCE(m.pdFirstName, ''), ' ', COALESCE(m.pdLastName, '')), " +
            "m.pdMobile, m.laLoanAmt, m.createdDate, m.dlrCode, " +
            "CONCAT(COALESCE(d.dealerNameMssf, 'Unknown Dealer'), ' - ', COALESCE(d.dealerLocation, 'Unknown Location'))) " +
            "FROM MSSFCustomerData m " +
            "LEFT JOIN MSSFDealerSol d ON m.dlrCode = d.dlrCode AND d.delFlg = 'N' " +
            "WHERE d.solId = :solId " +
            "AND (m.status IS NULL OR m.status = '' OR m.status = 'PENDING') " +
            "AND m.createdDate <= :beforeDate " +
            "ORDER BY m.createdDate ASC")
    List<MSSFCustomerSummaryDTO> findPendingApplicationsBySolOlderThan(@Param("solId") String solId,
                                                                       @Param("beforeDate") LocalDateTime beforeDate);

    @Modifying
@Transactional
@Query("UPDATE MSSFCustomerData e SET e.status = 'C', e.modifiedDate = CURRENT_TIMESTAMP, e.modifiedBy = :modifiedBy WHERE e.refNo = :refNo")
void updateMSSFStatusToComplete(@Param("refNo") String refNo, @Param("modifiedBy") String modifiedBy);

// Alternative method without modifiedBy parameter if you don't want to track who made the change
@Modifying
@Transactional
@Query("UPDATE MSSFCustomerData e SET e.status = 'C', e.modifiedDate = CURRENT_TIMESTAMP WHERE e.refNo = :refNo")
void updateMSSFStatusToComplete(@Param("refNo") String refNo);


}
