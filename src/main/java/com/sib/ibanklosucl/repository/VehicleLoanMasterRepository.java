package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleLoanMasterRepository  extends RevisionRepository<VehicleLoanMaster, Long,Long>, JpaRepository<VehicleLoanMaster, Long> {

    @Query(nativeQuery = true, value = "select IBANKLOSUCL.GENERATE_WI_NUM(:loan_type) from dual")
    String  generateWiNum(@Param("loan_type") String loan_type);
    @Query("SELECT m FROM VehicleLoanMaster m LEFT JOIN FETCH m.applicants a WHERE m.slno = :slno AND a.delFlg = 'N'")
    VehicleLoanMaster findBySlnoWithApplicants11(@Param("slno") Long slno);
//    @Query("SELECT m FROM VehicleLoanMaster m LEFT JOIN FETCH m.applicants a " +
//            "LEFT JOIN FETCH a.kycapplicants k LEFT JOIN FETCH a.basicapplicants b LEFT JOIN FETCH a.vlProgram p   LEFT JOIN FETCH a.vlEmployment emp LEFT JOIN FETCH a.vlcredit cr "+
//            "WHERE m.slno = :slno AND m.activeFlg='Y' AND (a.delFlg = 'N' or a.delFlg is null ) AND  (k.delFlg = 'N' or k.delFlg is null ) AND  (b.delFlg = 'N' or b.delFlg is null ) AND  (p.delFlg = 'N' or p.delFlg is null ) AND  (emp.delFlg = 'N' or emp.delFlg is null ) AND  (cr.delFlg = 'N' or cr.delFlg is null ) "+
//            "AND (a.genComplete is null or (a.genComplete='Y' OR a.kycComplete = 'Y' OR a.basicComplete = 'Y' OR a.employmentComplete = 'Y' " +
//            "OR a.incomeComplete = 'Y' OR a.creditComplete = 'Y'  " +
//            "OR a.vehicleComplete = 'Y' OR a.insuranceComplete = 'Y' OR a.loanComplete = 'Y')) "+
//            " order by a.applicantId")
//    VehicleLoanMaster findBySlnoWithApplicants(@Param("slno") Long slno);
// test
    @Query("SELECT DISTINCT m FROM VehicleLoanMaster m " +
            "LEFT JOIN FETCH m.applicants a " +
            "LEFT JOIN FETCH a.kycapplicants k " +
            "LEFT JOIN FETCH a.basicapplicants b " +
            "LEFT JOIN FETCH a.vlEmployment emp " +
            "LEFT JOIN FETCH a.vlcredit cr " +
            "LEFT JOIN FETCH a.vlProgram p " +
            "WHERE m.slno = :slno AND m.activeFlg = 'Y' " +
            "AND (a.delFlg = 'N' OR a.delFlg IS NULL) " +
            "AND (k.delFlg = 'N' OR k.delFlg IS NULL) " +
            "AND (b.delFlg = 'N' OR b.delFlg IS NULL) " +
            "AND (emp.delFlg = 'N' OR emp.delFlg IS NULL) " +
            "AND (cr.delFlg = 'N' OR cr.delFlg IS NULL) " +
            "AND (p.delFlg = 'N' OR p.delFlg IS NULL) " +
            "ORDER BY a.applicantId")
    VehicleLoanMaster findBySlnoWithApplicants(@Param("slno") Long slno);


    @Query("SELECT m FROM VehicleLoanMaster m LEFT JOIN FETCH m.applicants a " +
            "WHERE m.slno = :slno AND (a.delFlg = 'N' or a.delFlg is null ) "+
            " order by a.applicantId")
    VehicleLoanMaster findBySlnoWithApplicantsOnly(@Param("slno") Long slno);

    Optional<VehicleLoanMaster> findByWiNumAndQueue(String winum,String Queue);

    Optional<VehicleLoanMaster> findByWiNumAndActiveFlg(String winum,String activeFlg);



    @Query("SELECT qs.queue, SUM(qs.count) " +
            "FROM QueueStat qs " +
            "WHERE qs.solId = :solId " +
            "GROUP BY qs.queue")
    List<Object[]> getStatusCountsBySolId(@Param("solId") String solId);

    @Query("SELECT qs.queue, SUM(qs.count) " +
            "FROM QueueStat qs " +
            "GROUP BY qs.queue")
    List<Object[]> getStatusCountsBySolId();

    //  @Query("SELECT m.slno,m.wiNum,m.custName,m.solId,m.queue,m.status,m.riRcreDate,m.channel,l.lockFlg,l.lockedBy from VehicleLoanMaster m,VehicleLoanLock l where m.wiNum=l.wiNum and m.queue=:queue and m.activeFlg='Y' AND m.solId=:solId and m.custName is not null")
  @Query("SELECT m, l FROM VehicleLoanMaster m LEFT OUTER JOIN VehicleLoanLock l ON m.wiNum = l.wiNum  and l.delFlg='N' and l.lockFlg='Y' and l.releasedOn is null WHERE   m.queue = :queue AND m.activeFlg = 'Y' AND m.solId IN :solId AND m.custName IS NOT NULL")
    List<Object[]> findByQueueAndSol(String queue,List<String> solId);

    @Query("SELECT m, l FROM VehicleLoanMaster m LEFT OUTER JOIN VehicleLoanLock l ON m.wiNum = l.wiNum  and l.delFlg='N' and l.lockFlg='Y' and l.releasedOn is null WHERE   m.queue = :queue AND m.activeFlg = 'Y'  AND m.custName IS NOT NULL")
    List<Object[]> findByQueueforCRT(String queue);
    @Query("SELECT m, l FROM VehicleLoanMaster m LEFT OUTER JOIN VehicleLoanLock l ON m.wiNum = l.wiNum  and l.delFlg='N' and l.lockFlg='Y' and l.releasedOn is null WHERE   m.queue = :queue AND m.activeFlg = 'Y'  AND m.custName IS NOT NULL")
    List<Object[]> findByHunterQueue(String queue);

  /*
    //@Query("WITH TAT AS (SELECT wiNum,SLNO,QUEUE,queueExitUser ,TO_CHAR(queueExitDate,'DD-MM-YYYY HH24:MI:SS') queueExitDate FROM (SELECT wiNum,SLNO, QUEUE,queueExitUser,queueExitDate, ROW_NUMBER() OVER (PARTITION BY wiNum,SLNO ORDER BY INO DESC) AS RN FROM VehicleLoanTat) SUB WHERE RN=2)SELECT m,l,s FROM VehicleLoanMaster m LEFT OUTER JOIN VehicleLoanLock l ON m.wiNum = l.wiNum  and l.delFlg='N' and l.lockFlg='Y' and l.releasedOn is null LEFT OUTER JOIN TAT S ON M.wiNum=S.wiNum AND M.SLNO=S.SLNO WHERE   m.queue = :queue AND m.activeFlg = 'Y' AND m.sol_Id = :solId AND m.cust_Name IS NOT NULL")
    //@Query("SELECT m,l,s FROM VehicleLoanMaster m LEFT OUTER JOIN VehicleLoanLock l ON m.wiNum = l.wiNum  and l.delFlg='N' and l.lockFlg='Y' and l.releasedOn is null LEFT OUTER JOIN (SELECT wiNum,SLNO,QUEUE,queueExitUser ,TO_CHAR(queueExitDate,'DD-MM-YYYY HH24:MI:SS') queueExitDate FROM (SELECT wiNum,SLNO, QUEUE,queueExitUser,queueExitDate, ROW_NUMBER() OVER (PARTITION BY wiNum,SLNO ORDER BY INO DESC) AS RN FROM VehicleLoanTat) SUB WHERE RN=2)TAT S ON M.wiNum=S.wiNum AND M.SLNO=S.SLNO WHERE   m.queue = :queue AND m.activeFlg = 'Y' AND m.sol_Id = :solId AND m.cust_Name IS NOT NULL")
    //@Query("SELECT m,l,s FROM VehicleLoanMaster m LEFT OUTER JOIN VehicleLoanLock l ON m.wiNum = l.wiNum  and l.delFlg='N' and l.lockFlg='Y' and l.releasedOn is null LEFT OUTER JOIN (select 'a' winum, 'b' slno from dual )TAT S ON M.wiNum=S.wiNum AND M.SLNO=S.SLNO WHERE   m.queue = :queue AND m.activeFlg = 'Y' AND m.sol_Id = :solId AND m.cust_Name IS NOT NULL")
    @Query("SELECT m,l,s FROM VehicleLoanMaster m LEFT OUTER JOIN VehicleLoanLock l ON m.wiNum = l.wiNum  and l.delFlg='N' and l.lockFlg='Y' and l.releasedOn is null LEFT OUTER JOIN (SELECT wiNum,SLNO,QUEUE,queueExitUser ,TO_CHAR(queueExitDate,'DD-MM-YYYY HH24:MI:SS') queueExitDate FROM (SELECT wiNum,SLNO, QUEUE,queueExitUser,queueExitDate, ROW_NUMBER() OVER (PARTITION BY wiNum,SLNO ORDER BY INO DESC) AS RN FROM VehicleLoanTat) SUB WHERE RN=2)TAT S ON M.wiNum=S.wiNum AND M.SLNO=S.SLNO WHERE   m.queue = :queue AND m.activeFlg = 'Y' AND m.sol_Id = :solId AND m.cust_Name IS NOT NULL")
    List<Object[]> findByBSQueueAndSol(String queue,String solId);
*/

    @Query(nativeQuery = true,value = "SELECT m.WI_NUM,m.SLNO,m.CUST_NAME,m.RI_RCRE_DATE,m.CHANNEL,l.LOCK_FLG,l.LOCKED_BY FROM VEHICLE_LOAN_MASTER m JOIN  vlrbcpccheckerlevel@mybank ch on  m.QUEUE=ch.LEVEL_NAME  LEFT OUTER JOIN VEHICLE_LOAN_LOCK l ON m.WI_NUM = l.WI_NUM  and l.DEL_FLG='N' and l.LOCK_FLG='Y' and l.RELEASED_ON is null WHERE   m.ACTIVE_FLG = 'Y'  AND m.CUST_NAME IS NOT NULL AND ch.DEL_FLAG='N' and ch.ppc_no=:ppcNo and m.RBCPC_CHECKER_USER=:ppcNo")
    List<Object[]> findByRBCQueue(String ppcNo);


    @Query("SELECT m FROM VehicleLoanMaster m where m.queue=:queue and m.activeFlg='Y' order by m.queueDate")
    List<Object[]> findByQueue(@Param("queue") String queue);

    @Query("SELECT m, l FROM VehicleLoanMaster m LEFT OUTER JOIN VehicleLoanLock l ON m.wiNum = l.wiNum  and l.delFlg='N' and l.lockFlg='Y' and l.releasedOn is null WHERE   m.queue = :queue AND m.activeFlg = 'Y' AND m.custName IS NOT NULL")
    List<Object[]> findByQueueWithLock(@Param("queue") String queue);

    @Query("SELECT m, l FROM VehicleLoanMaster m JOIN VehicleLoanDetails e on e.wiNum=m.wiNum LEFT OUTER JOIN VehicleLoanLock l ON m.wiNum = l.wiNum  and l.delFlg='N' and l.lockFlg='Y' and l.releasedOn is null WHERE   m.queue='BD' and  m.solId = :solId  and e.insVal='Y' AND m.docMode is null  AND m.activeFlg = 'Y' AND m.custName IS NOT NULL")
    List<Object[]> findByInsList(@Param("solId") String solId);


    @Modifying
    @Transactional
    @Query("UPDATE VehicleLoanMaster v SET v.dsaSancDoc = :dsasacdoc WHERE v.wiNum = :wiNum and v.slno = :slno")
    void updateDsasacdocBywiNumAndSlno(String wiNum, Long slno,String dsasacdoc);


    @Query("SELECT m FROM VehicleLoanMaster m  WHERE   m.queue = :queue AND m.activeFlg = 'Y' AND m.custName IS NOT NULL and exists(select 1 from VehicleLoanSubqueueTask sq where sq.slno=m.slno  and sq.taskType=:subQueue and sq.status='PENDING')")
    List<Object[]> findByQueueWithSubqueue(@Param("queue") String queue, @Param("subQueue") String subQueue);

    @Query(nativeQuery = true, value =
        "SELECT DISTINCT 'ROI_WAIVER' as WAIVER_TYPE FROM vlroiwaiverlevel@mybank WHERE PPC_NO = :ppcNo AND DEL_FLAG = 'N' " +
        "UNION " +
        "SELECT DISTINCT 'CHARGE_WAIVER' as WAIVER_TYPE FROM vlchargewaiverlevel@mybank WHERE PPC_NO = :ppcNo AND DEL_FLAG = 'N'")
    List<Object[]> findWaiverAccess(@Param("ppcNo") String ppcNo);

   
   @Query(nativeQuery = true, value =
        "SELECT DISTINCT m.WI_NUM, m.SLNO, m.CUST_NAME, m.RI_RCRE_DATE, m.CHANNEL, " +
        "COALESCE(l.LOCK_FLG, 'N') as LOCK_FLG, l.LOCKED_BY, t.STATUS, " +
        "CASE " +
        "    WHEN :hasRoiAccess = 'Y' AND :hasChargeAccess = 'Y' THEN " +
        "        CASE " +
        "            WHEN EXISTS (SELECT 1 FROM VEHICLE_LOAN_SUBQUEUE_TASK t2 " +
        "                         WHERE t2.SLNO = m.SLNO AND t2.TASK_TYPE = 'ROI_WAIVER' AND t2.STATUS = 'PENDING') " +
        "                 AND EXISTS (SELECT 1 FROM VEHICLE_LOAN_SUBQUEUE_TASK t3 " +
        "                             WHERE t3.SLNO = m.SLNO AND t3.TASK_TYPE = 'CHARGE_WAIVER' AND t3.STATUS = 'PENDING') " +
        "            THEN 'BOTH' " +
        "            ELSE t.TASK_TYPE " +
        "        END " +
        "    ELSE t.TASK_TYPE " +
        "END AS TASK_TYPE " +
        "FROM VEHICLE_LOAN_MASTER m " +
        "JOIN VEHICLE_LOAN_SUBQUEUE_TASK t ON m.SLNO = t.SLNO " +
        "LEFT OUTER JOIN (SELECT * FROM VEHICLE_LOAN_LOCK WHERE LOCK_FLG = 'Y' AND DEL_FLG = 'N' AND RELEASED_ON IS NULL) l ON m.WI_NUM = l.WI_NUM " +
        "WHERE m.ACTIVE_FLG = 'Y' AND m.CUST_NAME IS NOT NULL " +
        "AND t.STATUS = 'PENDING' " +
        "AND ((:hasRoiAccess = 'Y' AND t.TASK_TYPE = 'ROI_WAIVER' AND EXISTS " +
        "     (SELECT 1 FROM vlroiwaiverlevel@mybank r WHERE r.LEVEL_NAME = t.DECISION AND r.PPC_NO = :ppcNo AND r.DEL_FLAG = 'N')) " +
        "  OR (:hasChargeAccess = 'Y' AND t.TASK_TYPE = 'CHARGE_WAIVER' AND EXISTS " +
        "     (SELECT 1 FROM vlchargewaiverlevel@mybank c WHERE c.LEVEL_NAME = t.DECISION AND c.PPC_NO = :ppcNo AND c.DEL_FLAG = 'N')))")
    List<Object[]> findWaiverSubtasks1(@Param("ppcNo") String ppcNo, @Param("hasRoiAccess") String hasRoiAccess, @Param("hasChargeAccess") String hasChargeAccess);

@Query(nativeQuery = true, value =
    "SELECT DISTINCT m.WI_NUM, m.SLNO, m.CUST_NAME, m.RI_RCRE_DATE, m.CHANNEL, " +
    " t.STATUS, " +
    "CASE " +
    "    WHEN :hasRoiAccess = 'Y' AND :hasChargeAccess = 'Y' THEN " +
    "        CASE " +
    "            WHEN EXISTS (SELECT 1 FROM VEHICLE_LOAN_SUBQUEUE_TASK t2 " +
    "                         WHERE t2.SLNO = m.SLNO AND t2.TASK_TYPE = 'ROI_WAIVER' AND t2.STATUS = 'PENDING') " +
    "                 AND EXISTS (SELECT 1 FROM VEHICLE_LOAN_SUBQUEUE_TASK t3 " +
    "                             WHERE t3.SLNO = m.SLNO AND t3.TASK_TYPE = 'CHARGE_WAIVER' AND t3.STATUS = 'PENDING') " +
    "            THEN 'BOTH' " +
    "            ELSE t.TASK_TYPE " +
    "        END " +
    "    ELSE t.TASK_TYPE " +
    "END AS TASK_TYPE " +
    "FROM VEHICLE_LOAN_MASTER m " +
    "JOIN VEHICLE_LOAN_SUBQUEUE_TASK t ON m.SLNO = t.SLNO " +
    "WHERE m.ACTIVE_FLG = 'Y' AND m.CUST_NAME IS NOT NULL " +
    "AND t.STATUS = 'PENDING' " +
    "AND ((:hasRoiAccess = 'Y' AND t.TASK_TYPE = 'ROI_WAIVER' AND EXISTS " +
    "     (SELECT 1 FROM vlroiwaiverlevel@mybank r WHERE r.LEVEL_NAME = t.DECISION AND r.PPC_NO = :ppcNo AND r.DEL_FLAG = 'N')) " +
    "  OR (:hasChargeAccess = 'Y' AND t.TASK_TYPE = 'CHARGE_WAIVER' AND EXISTS " +
    "     (SELECT 1 FROM vlchargewaiverlevel@mybank c WHERE c.LEVEL_NAME = t.DECISION AND c.PPC_NO = :ppcNo AND c.DEL_FLAG = 'N')))")
List<Object[]> findWaiverSubtasks(@Param("ppcNo") String ppcNo, @Param("hasRoiAccess") String hasRoiAccess, @Param("hasChargeAccess") String hasChargeAccess);

    @Query(value = """
      SELECT MAX(REV) 
        FROM VEHICLE_LOAN_MASTER_AUD 
       WHERE WI_NUM = :wiNum 
         AND QUEUE = :queue
    """, nativeQuery = true)
    Long findLastRevisionForQueue(@Param("wiNum") String wiNum,
                                  @Param("queue") String queue);

    @Modifying
    @Query(value = """
      UPDATE VEHICLE_LOAN_MASTER M
         SET ( WI_NUM,
               REQ_IP_ADDR,
               RI_RCRE_DATE,
               CHANNEL,
               QUEUE,
               QUEUE_DATE,
               STATUS,
               SOL_ID,
               CUST_NAME,
               STP,
               CMUSER,
               CMDATE,
               HOME_SOL,
               BUSUNIT_ID,
               NUM_COAPPLICANTS,
               NUM_GUARANTORS,
               SAN_FLG,
               SAN_DATE,
               SAN_USER,
               BR_VUSER,
               BR_VDATE,
               RBCPC_CMUSER,
               RBCPC_CMDATE,
               RBCPC_HOME_SOL,
               RBCPC_VUSER,
               RBCPC_VDATE,
               CRT_CMUSER,
               CRT_CMDATE,
               BR_DOC_CMUSER,
               BR_DOC_CMDATE,
               ACC_OPENED,
               ACC_NUMBER,
               ACC_OPEN_DATE,
               DISB_FLG,
               DISB_DATE,
               DISB_AMT,
               RESUBMIT_FLG,
               ACTIVE_FLG,
               CURRENTTAB,
               DSA_SANC_DOC,
               FIRST_TIME_BUYER,
               OWNER_APPLICANT_ID,
               REJ_DATE,
               REJ_FLG,
               REJ_QUEUE,
               REJ_USER,
               RBCPC_CHECKER_DECISION,
               RBCPC_MAKER_DECISION,
               PRE_DISB_CONDITION,
               CHARGE_WAIVER_REQUESTED,
               DOC_QUEUE_OVERALL_STATUS,
               ROI_REQUESTED,
               SANC_MOD_REQUIRED,
               REPAYMENT_STATUS,
               DOC_MODE,
               DOC_COMP_DATE,
               DOC_UPLOAD_DATE,
               DOC_UPLOAD_USER,
               MARGIN_RECEIPT,
               MODE_OPER,
               STAMP_AMT,
               PD_USER,
               PD_DATE,
               SAN_INIT_DATE,
               REF_NO
             )
             =
             (
               SELECT A.WI_NUM,
                      A.REQ_IP_ADDR,
                      A.RI_RCRE_DATE,
                      A.CHANNEL,
                      A.QUEUE,
                      A.QUEUE_DATE,
                      A.STATUS,
                      A.SOL_ID,
                      A.CUST_NAME,
                      A.STP,
                      A.CMUSER,
                      A.CMDATE,
                      A.HOME_SOL,
                      A.BUSUNIT_ID,
                      A.NUM_COAPPLICANTS,
                      A.NUM_GUARANTORS,
                      A.SAN_FLG,
                      A.SAN_DATE,
                      A.SAN_USER,
                      A.BR_VUSER,
                      A.BR_VDATE,
                      A.RBCPC_CMUSER,
                      A.RBCPC_CMDATE,
                      A.RBCPC_HOME_SOL,
                      A.RBCPC_VUSER,
                      A.RBCPC_VDATE,
                      A.CRT_CMUSER,
                      A.CRT_CMDATE,
                      A.BR_DOC_CMUSER,
                      A.BR_DOC_CMDATE,
                      A.ACC_OPENED,
                      A.ACC_NUMBER,
                      A.ACC_OPEN_DATE,
                      A.DISB_FLG,
                      A.DISB_DATE,
                      A.DISB_AMT,
                      A.RESUBMIT_FLG,
                      A.ACTIVE_FLG,
                      A.CURRENTTAB,
                      A.DSA_SANC_DOC,
                      A.FIRST_TIME_BUYER,
                      A.OWNER_APPLICANT_ID,
                      A.REJ_DATE,
                      A.REJ_FLG,
                      A.REJ_QUEUE,
                      A.REJ_USER,
                      A.RBCPC_CHECKER_DECISION,
                      A.RBCPC_MAKER_DECISION,
                      A.PRE_DISB_CONDITION,
                      A.CHARGE_WAIVER_REQUESTED,
                      A.DOC_QUEUE_OVERALL_STATUS,
                      A.ROI_REQUESTED,
                      A.SANC_MOD_REQUIRED,
                      A.REPAYMENT_STATUS,
                      A.DOC_MODE,
                      A.DOC_COMP_DATE,
                      A.DOC_UPLOAD_DATE,
                      A.DOC_UPLOAD_USER,
                      A.MARGIN_RECEIPT,
                      A.MODE_OPER,
                      A.STAMP_AMT,
                      A.PD_USER,
                      A.PD_DATE,
                      A.SAN_INIT_DATE,
                      A.REF_NO
                 FROM VEHICLE_LOAN_MASTER_AUD A
                WHERE A.SLNO = M.SLNO
                  AND A.REV  = :rev
             )
       WHERE M.SLNO = :slno
         AND EXISTS (
             SELECT 1
               FROM VEHICLE_LOAN_MASTER_AUD A2
              WHERE A2.SLNO = M.SLNO
                AND A2.REV  = :rev
         )
      """, nativeQuery = true)
    void revertMasterToRevision(@Param("slno") Long slno,
                                @Param("rev") Long rev);



}
