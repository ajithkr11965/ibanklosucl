package com.sib.ibanklosucl.service.reports;

import com.sib.ibanklosucl.dto.mssf.MSSFCustomerDTO;
import com.sib.ibanklosucl.dto.mssf.MSSFLockDTO;
import com.sib.ibanklosucl.model.mssf.MSSFCustomerData;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.mssf.MSSFCustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MSSFCustomerRepository mssfCustomerRepository;
    @Autowired
    private FetchRepository fetchRepository;

    public List<Map<String, String>> getPpcCheckerLevelDetails(String delFlag) {
        String sql = "SELECT 'L'|| replace(level_name,'RCL','') level_name, c.ppc_no, ppc_name, DESIGNATION, h.OFFICENAME, h.SOL_ID " +
                "FROM vlrbcpccheckerlevel@mybank c " +
                "JOIN HRMSEMPDTLS@mybank h ON h.ppcno = c.ppc_no " +
                "WHERE del_flag = ? " +
                "ORDER BY level_name desc,category DESC, desig";

        try {
            return jdbcTemplate.query(sql, new Object[]{delFlag}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("level_name", rs.getString("level_name"));
                map.put("ppc_no", rs.getString("ppc_no"));
                map.put("ppc_name", rs.getString("ppc_name"));
                map.put("designation", rs.getString("DESIGNATION"));
                map.put("officename", rs.getString("OFFICENAME"));
                map.put("sol_id", rs.getString("SOL_ID"));
                return map;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<Map<String, String>> getChargeWaiverLevelDetails(String delFlag) {
        String sql = "SELECT 'L'|| replace(level_name,'PC','') level_name, ppc_no, ppc_name, DESIGNATION, h.OFFICENAME, h.SOL_ID " +
                "FROM vlchargewaiverlevel@mybank c " +
                "JOIN HRMSEMPDTLS@mybank h ON h.ppcno = c.ppc_no " +
                "WHERE del_flag = ? " +
                "ORDER BY level_name desc,category DESC, desig";

        try {
            return jdbcTemplate.query(sql, new Object[]{delFlag}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("level_name", rs.getString("level_name"));
                map.put("ppc_no", rs.getString("ppc_no"));
                map.put("ppc_name", rs.getString("ppc_name"));
                map.put("designation", rs.getString("DESIGNATION"));
                map.put("officename", rs.getString("OFFICENAME"));
                map.put("sol_id", rs.getString("SOL_ID"));
                return map;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, String>> getRoiWaiverLevelDetails(String delFlag) {
        String sql = "SELECT 'L'|| replace(level_name,'RI','') level_name, ppc_no, ppc_name, DESIGNATION, h.OFFICENAME, h.SOL_ID " +
                "FROM vlroiwaiverlevel@mybank c " +
                "JOIN HRMSEMPDTLS@mybank h ON h.ppcno = c.ppc_no " +
                "WHERE del_flag = ? " +
                "ORDER BY level_name desc,category DESC, desig";

        try {
            return jdbcTemplate.query(sql, new Object[]{delFlag}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("level_name", rs.getString("level_name"));
                map.put("ppc_no", rs.getString("ppc_no"));
                map.put("ppc_name", rs.getString("ppc_name"));
                map.put("designation", rs.getString("DESIGNATION"));
                map.put("officename", rs.getString("OFFICENAME"));
                map.put("sol_id", rs.getString("SOL_ID"));
                return map;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<Map<String, String>> getDetailReport(String home_sol) {

        String apnd = "";

        if (home_sol.startsWith("8")) {
            apnd = "";
        } else if (home_sol.startsWith("9")) {
            apnd = "and (vehicle_loan_master.SOL_ID in (select sol_id from misbmt@mybank where reg_code='" + home_sol + "' and close_date is null)) ";
        } else {
            apnd = "and vehicle_loan_master.SOL_ID ='" + home_sol + "' ";
        }

        String sql = "select vehicle_loan_master.SOL_ID,MISBMT.BR_NAME,vehicle_loan_master.WI_NUM,vehicle_loan_master.CUST_NAME NAMEOFAPPLICANT,nvl(MISRCT.CODEDESC,' ') queue,vehicle_loan_master.channel, nvl(vehicle_loan_master.STP,' ') STP," +
                "decode(vehicle_loan_master.SAN_FLG,'Y','Sanctioned','Pending') SANCTIONED, nvl(vehicle_loan_master.REJ_FLG,' ') AS REJECTED," +
                "nvl(vehicle_loan_PROGRAM.LOAN_PROGRAM,' ') LOAN_PROGRAM, nvl(vehicle_loan_ELIGIBILITY.LOAN_AMT,'0') REQUESTEDAMT, nvl(NVL(vehicle_loan_ELIGIBILITY.LOAN_AMOUNT_RECOMMENDED_CPC,vehicle_loan_ELIGIBILITY.ELIGIBLE_LOAN_AMT),'0') ELIGIBILEAMT , vehicle_loan_allotment.do_ppc do_ppc, nvl(hrmsempdtls.ppc_name,' ') do_name " +
                "from vehicle_loan_master " +
                "join misbmt@mybank on vehicle_loan_master.sol_id=misbmt.sol_id " +
                "left outer join misrct on vehicle_loan_master.QUEUE=misrct.codevalue and misrct.CODETYPE='QT' " +
                "LEFT OUTER JOIN vehicle_loan_APPLICANTS MAINAPPLICANT ON vehicle_loan_master.SLNO=MAINAPPLICANT.SLNO AND MAINAPPLICANT.APPLICANT_TYPE='A'  AND NVL(MAINAPPLICANT.DEL_FLG,'N')='N' " +
                "LEFT OUTER JOIN vehicle_loan_PROGRAM ON MAINAPPLICANT.APPLICANT_ID=vehicle_loan_PROGRAM.APPLICANT_ID AND NVL(vehicle_loan_PROGRAM.DEL_FLG,'N')='N' " +
                "LEFT OUTER JOIN vehicle_loan_ELIGIBILITY ON vehicle_loan_master.SLNO=vehicle_loan_ELIGIBILITY.SLNO AND NVL(vehicle_loan_ELIGIBILITY.del_flg,'N')='N' " +
                "left outer join vehicle_loan_allotment on vehicle_loan_master.slno=vehicle_loan_allotment.slno and vehicle_loan_allotment.active_flg='Y' and NVL(vehicle_loan_allotment.del_flg,'N')='N' " +
                "left outer join hrmsempdtls@mybank on vehicle_loan_allotment.DO_PPC=hrmsempdtls.ppcno " +
                "where vehicle_loan_master.CUST_NAME is not null and nvl(vehicle_loan_master.REJ_FLG,'N')='N' " + apnd + " " +
                "ORDER BY 1";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("sol_id", rs.getString("SOL_ID"));
                map.put("br_name", rs.getString("BR_NAME").toUpperCase().trim());
                map.put("wi_num", rs.getString("WI_NUM"));
                map.put("cust_name", rs.getString("NAMEOFAPPLICANT"));
                map.put("queue", rs.getString("queue"));
                map.put("channel", rs.getString("channel"));
                map.put("stp", rs.getString("STP"));
                map.put("sanctioned", rs.getString("SANCTIONED"));
                map.put("rejected", rs.getString("REJECTED"));
                map.put("loan_program", rs.getString("LOAN_PROGRAM"));
                map.put("requestedamt", rs.getString("REQUESTEDAMT"));
                map.put("eligibleamt", rs.getString("ELIGIBILEAMT"));
                map.put("do_name", rs.getString("do_name"));
                map.put("do_ppc", rs.getString("do_ppc"));
                return map;
            });
        } catch (Exception e) {
            // Handle the case where no category is found
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, String>> getConsolReport(String home_sol) {

        String apnd = "";

        if (home_sol.startsWith("8")) {
            apnd = "";
        } else if (home_sol.startsWith("9")) {
            apnd = "and (A.SOL_ID in (select sol_id from misbmt@mybank where reg_code='" + home_sol + "' and close_date is null)) ";
        } else {
            apnd = "and A.SOL_ID ='" + home_sol + "' ";
        }

        String sql = "select count(*) RECCNT,QUEUE,A.SOL_ID,m.CODEDESC QUEUE_NAME,B.BR_NAME,sum(nvl(C.LOAN_AMT,'0')) LOAN_AMT,sum(nvl(E.SANC_AMOUNT_RECOMMENDED,'0')) SANC_AMT from VEHICLE_LOAN_MASTER A,misrct m,MISBMT@MYBANK B,VEHICLE_LOAN_DETAILS C,VEHICLE_LOAN_APPLICANTS D,VEHICLE_LOAN_ELIGIBILITY E where A.SLNO=C.SLNO(+) and A.SLNO=E.SLNO(+) and A.SLNO = D.SLNO(+) AND C.APPLICANT_ID=D.APPLICANT_ID(+) " + apnd + " AND a.SOL_ID not in ('8028','0817') AND A.SOL_ID=B.SOL_ID and a.QUEUE=m.CODEVALUE and m.CODETYPE='QT' group by a.QUEUE, a.SOL_ID,m.CODEDESC,B.BR_NAME  order by 3,2";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("sol_id", rs.getString("SOL_ID"));
                map.put("br_name", rs.getString("BR_NAME").toUpperCase().trim());
                map.put("reccnt", rs.getString("RECCNT"));
                map.put("qname", rs.getString("QUEUE_NAME"));
                map.put("loanamt", rs.getString("LOAN_AMT"));
                map.put("sancamt", rs.getString("SANC_AMT"));
                return map;
            });
        } catch (Exception e) {
            // Handle the case where no category is found
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, String>> WaiverReport(String fromdt, String todt, String type) {

        String cond = "";
        if ("R".equalsIgnoreCase(type)) {
            cond = " WHERE TRUNC(ROI_COMPLETED_DATE)>=to_date(?,'yyyy-mm-dd') and TRUNC(ROI_COMPLETED_DATE)<=to_date(?,'yyyy-mm-dd') ";
        } else {
            cond = " WHERE  TRUNC(PR_COMPLETED_DATE)>=to_date(?,'yyyy-mm-dd') and  TRUNC(PR_COMPLETED_DATE)<=to_date(?,'yyyy-mm-dd') ";
        }

        String sql = "SELECT * FROM WAIVER_REPORT " + cond + " order by QUEUE,REG_CODE,SOL_ID,SAN_DATE";

        try {
            return jdbcTemplate.query(sql, new Object[]{fromdt, todt}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();

                // Mapping all fields from the result set
                map.put("SOL_ID", rs.getString("SOL_ID"));
                map.put("QUEUE", rs.getString("QUEUE"));
                map.put("REG_CODE", rs.getString("REG_CODE"));
                map.put("REG_NAME", rs.getString("REG_NAME"));
                map.put("BR_NAME", rs.getString("BR_NAME"));
                map.put("CUST_ID", rs.getString("CUST_ID"));
                map.put("ACC_NUMBER", rs.getString("ACC_NUMBER"));
                map.put("ACCT_NAME", rs.getString("ACCT_NAME"));
                map.put("WI_NUM", rs.getString("WI_NUM"));
                map.put("ELIGIBLE_LOAN_AMT", rs.getString("ELIGIBLE_LOAN_AMT"));
                map.put("SAN_DATE", rs.getString("SAN_DATE"));
                map.put("SANC_AMOUNT_RECOMMENDED", rs.getString("SANC_AMOUNT_RECOMMENDED"));
                map.put("SANC_TENOR", rs.getString("SANC_TENOR"));
                map.put("SANC_EMI", rs.getString("SANC_EMI"));
                map.put("INITIAL_ROI", rs.getString("INITIAL_ROI"));
                map.put("REVISED_ROI", rs.getString("REVISED_ROI"));
                map.put("ROI_COMPLETED_DATE", rs.getString("ROI_COMPLETED_DATE"));
                map.put("ROI_COMPLETED_USER", rs.getString("ROI_COMPLETED_USER"));
                map.put("INITFEE", rs.getString("INITFEE"));
                map.put("FINALFEE", rs.getString("FINALFEE"));
                map.put("PR_COMPLETED_DATE", rs.getString("PR_COMPLETED_DATE"));
                map.put("PR_COMPLETED_USER", rs.getString("PR_COMPLETED_USER"));
                map.put("APPL_SCORE", rs.getString("APPL_SCORE"));

                return map;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, String>> dkexperianFailReport(String fromdt, String todt) {

        String sql = "select * from DK_REPORT WHERE  TRUNC(CMDATE)>=to_date(?,'yyyy-mm-dd') and  TRUNC(CMDATE)<=to_date(?,'yyyy-mm-dd')";
        try {
            return jdbcTemplate.query(sql, new Object[]{fromdt, todt}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("WI_NUM", rs.getString("WI_NUM"));
                map.put("CMDATE", rs.getString("CMDATE"));
                map.put("QUEUE", rs.getString("QUEUE"));
                map.put("APPTYPE", rs.getString("APPTYPE"));
                map.put("SOL_ID", rs.getString("SOL_ID"));
                map.put("BR_NAME", rs.getString("BR_NAME"));
                map.put("REG_CODE", rs.getString("REG_CODE"));
                map.put("REG_NAME", rs.getString("REG_NAME"));
                map.put("LOAN_PROGRAM", rs.getString("LOAN_PROGRAM"));
                map.put("DK_SCORE", rs.getString("DK_SCORE"));
                map.put("DK_DATE", rs.getString("DK_DATE"));
                map.put("DK_RED_SLAB", rs.getString("DK_RED_SLAB"));
                map.put("BUREAU_SCORE", rs.getString("BUREAU_SCORE"));
                map.put("BUREAU_RED_SLAB", rs.getString("BUREAU_RED_SLAB"));
                map.put("APPLICANT_ID", rs.getString("APPLICANT_ID"));
                map.put("CURRENTTAB", rs.getString("CURRENTTAB"));
                return map;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<Map<String, String>> getCurrentReport(String fromdt, String todt) {
        String sql = "select * from CURRENT_WI_STATUS where  TRUNC(CMDATE)>=to_date(?,'yyyy-mm-dd') and  TRUNC(CMDATE)<=to_date(?,'yyyy-mm-dd')  order by WI_NUM";
        try {
            return jdbcTemplate.query(sql, new Object[]{fromdt, todt}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("SOL_ID", rs.getString("SOL_ID"));
                map.put("BR_NAME", rs.getString("BR_NAME"));
                map.put("REG_CODE", rs.getString("REG_CODE"));
                map.put("REG_NAME", rs.getString("REG_NAME"));
                map.put("WI_NUM", rs.getString("WI_NUM"));
                map.put("CMDATE", rs.getString("CMDATE"));
                map.put("CUST_NAME", rs.getString("CUST_NAME"));
                map.put("QUEUE_ENTRY_DATE", rs.getString("QUEUE_ENTRY_DATE"));
                map.put("QUEUE_DESC", rs.getString("QUEUE_DESC"));
                map.put("QUEUE", rs.getString("QUEUE"));
                map.put("CHANNEL", rs.getString("CHANNEL"));
                map.put("STP", rs.getString("STP"));
                map.put("SAN_DATE", rs.getString("SAN_DATE"));
                map.put("Program", rs.getString("Program"));
                map.put("LOAN_AMT", rs.getString("LOAN_AMT"));
                map.put("ELIGIBLE_LOAN_AMT", rs.getString("ELIGIBLE_LOAN_AMT"));
                map.put("DO_PPC", rs.getString("DO_PPC"));
                return map;
            });
        } catch (Exception e) {
            // Handle the case where no category is found
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, String>> getVehicleLoanCifReport() {
        String sql = """
                    
                    with task as (
                                select COMPLETED_DATE,SLNO,APPLICANT_ID,TASK_TYPE,STATUS, task_id, created_date
                                from vehicle_loan_subqueue_task
                                where TASK_ID IN (
                                    SELECT MAX(TASK_ID)
                                    FROM vehicle_loan_subqueue_task
                                    WHERE task_type in ('VKYC','CIF_CREATION')
                                    group by applicant_id
                                )
                            )
                            SELECT
                                VEHICLE_LOAN_APPLICANTS.WI_NUM,
                                VEHICLE_LOAN_APPLICANTS.APPL_NAME,
                                MR.REG_CODE||'-'||MR.REG_NAME REGION,
                                case when VEHICLE_LOAN_APPLICANTS.cif_id is null then 'No' else 'Yes' end CIF_CREATED,
                                TASK.COMPLETED_DATE STATUS_DATE,
                                CASE WHEN (VEHICLE_LOAN_APPLICANTS.CIF_ID IS NULL AND task.STATUS='COMPLETED')
                                    THEN 'SEND BACK' when VEHICLE_LOAN_APPLICANTS.cif_id is not null then 'COMPLETED.' ELSE task.STATUS END AS STATUS,
                                VEHICLE_LOAN_APPLICANTS.CIF_ID,
                                decode(TASK.TASK_TYPE,'CIF_CREATION','MANUAL',TASK.TASK_TYPE) TASK_TYPE,
                                VEHICLE_LOAN_APPLICANTS.applicant_id,
                                task.task_id,
                                created_date,
                                queue
                            FROM VEHICLE_LOAN_APPLICANTS
                            JOIN TASK ON VEHICLE_LOAN_APPLICANTS.SLNO=TASK.SLNO
                                and VEHICLE_LOAN_APPLICANTS.applicant_id=task.applicant_id
                            JOIN VEHICLE_LOAN_MASTER ON VEHICLE_LOAN_APPLICANTS.SLNO=VEHICLE_LOAN_MASTER.SLNO
                            JOIN MISBMT@MYBANK MB ON VEHICLE_LOAN_MASTER.SOL_ID=MB.SOL_ID
                            JOIN MISREG@MYBANK MR ON MB.REG_CODE=MR.REG_CODE
                    		WHERE VEHICLE_LOAN_MASTER.QUEUE!='NIL'
                        
                """;

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("WI_NUM", rs.getString("WI_NUM"));
                map.put("APPL_NAME", rs.getString("APPL_NAME"));
                map.put("REGION", rs.getString("REGION"));
                map.put("CIF_CREATED", rs.getString("CIF_CREATED"));
                map.put("STATUS_DATE", rs.getString("STATUS_DATE"));
                map.put("STATUS", rs.getString("STATUS"));
                map.put("CIF_ID", rs.getString("CIF_ID"));
                map.put("TASK_TYPE", rs.getString("TASK_TYPE"));
                map.put("APPLICANT_ID", rs.getString("APPLICANT_ID"));
                map.put("TASK_ID", rs.getString("TASK_ID"));
                map.put("CREATED_DATE", rs.getString("CREATED_DATE"));
                map.put("QUEUE", rs.getString("QUEUE"));
                return map;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, String>> getMisReport(String fromdt, String todt) {
        String sql = "select * from MIS_REPORT where  TRUNC(CREATEDATE)>=to_date(?,'yyyy-mm-dd') and  TRUNC(CREATEDATE)<=to_date(?,'yyyy-mm-dd')  order by SLNO";
        try {
            return jdbcTemplate.query(sql, new Object[]{fromdt, todt}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("SLNO", rs.getString("SLNO") != null ? rs.getString("SLNO") : "");
                map.put("WI_NUM", rs.getString("WI_NUM") != null ? rs.getString("WI_NUM") : "");
                map.put("Current_Workstep_Name", rs.getString("Current_Workstep_Name") != null ? rs.getString("Current_Workstep_Name") : "");
                map.put("Previous_Workstep_Name", rs.getString("Previous_Workstep_Name") != null ? rs.getString("Previous_Workstep_Name") : "");
                map.put("SOL_ID", rs.getString("SOL_ID") != null ? rs.getString("SOL_ID") : "");
                map.put("BR_NAME", rs.getString("BR_NAME") != null ? rs.getString("BR_NAME") : "");
                map.put("CLUSTER_NAME", rs.getString("CLUSTER_NAME") != null ? rs.getString("CLUSTER_NAME") : "");
                map.put("REG_CODE", rs.getString("REG_CODE") != null ? rs.getString("REG_CODE") : "");
                map.put("REG_NAME", rs.getString("REG_NAME") != null ? rs.getString("REG_NAME") : "");
                map.put("CUST_NAME", rs.getString("CUST_NAME") != null ? rs.getString("CUST_NAME") : "");
                map.put("LOAN_AMT", rs.getString("LOAN_AMT") != null ? rs.getString("LOAN_AMT") : "");
                map.put("PROGRAM", rs.getString("PROGRAM") != null ? rs.getString("PROGRAM") : "");
                map.put("MAKER_TAT", rs.getString("MAKER_TAT") != null ? rs.getString("MAKER_TAT") : "");
                map.put("MAKER_ENTRY_DATE", rs.getString("MAKER_ENTRY_DATE") != null ? rs.getString("MAKER_ENTRY_DATE") : "");
                map.put("MAKER_EXIT_DATE", rs.getString("MAKER_EXIT_DATE") != null ? rs.getString("MAKER_EXIT_DATE") : "");
                map.put("CHECKER_TAT", rs.getString("CHECKER_TAT") != null ? rs.getString("CHECKER_TAT") : "");
                map.put("SENDBACK_TAT", rs.getString("SENDBACK_TAT") != null ? rs.getString("SENDBACK_TAT") : "");
                map.put("RBCMAKER_TAT", rs.getString("RBCMAKER_TAT") != null ? rs.getString("RBCMAKER_TAT") : "");
                map.put("RBCMAKER_ENTRY_DATE", rs.getString("RBCMAKER_ENTRY_DATE") != null ? rs.getString("RBCMAKER_ENTRY_DATE") : "");
                map.put("RBCCHEKER_TAT", rs.getString("RBCCHEKER_TAT") != null ? rs.getString("RBCCHEKER_TAT") : "");
                map.put("DO_PPC", rs.getString("DO_PPC") != null ? rs.getString("DO_PPC") : "");
                map.put("Name_DO", rs.getString("Name_DO") != null ? rs.getString("Name_DO") : "");
                map.put("SAN_USER", rs.getString("SAN_USER") != null ? rs.getString("SAN_USER") : "");
                map.put("Name_SAN", rs.getString("Name_SAN") != null ? rs.getString("Name_SAN") : "");
                map.put("SAN_DATE", rs.getString("SAN_DATE") != null ? rs.getString("SAN_DATE") : "");
                map.put("SANC_AMT", rs.getString("SANC_AMT") != null ? rs.getString("SANC_AMT") : "");
                map.put("CRT_AMBER_TAT", rs.getString("CRT_AMBER_TAT") != null ? rs.getString("CRT_AMBER_TAT") : "");
                map.put("CRT_GREEN_TAT", rs.getString("CRT_GREEN_TAT") != null ? rs.getString("CRT_GREEN_TAT") : "");
                map.put("STATUS", rs.getString("STATUS") != null ? rs.getString("STATUS") : "");
                map.put("SANC_AMT", rs.getString("SANC_AMT") != null ? rs.getString("SANC_AMT") : "");
                map.put("CIF_ID", rs.getString("CIF_ID") != null ? rs.getString("CIF_ID") : "");
                map.put("ACC_OPEN_DATE", rs.getString("ACC_OPEN_DATE") != null ? rs.getString("ACC_OPEN_DATE") : "");
                map.put("DISBURSEDAMOUNT", rs.getString("DISBURSEDAMOUNT") != null ? rs.getString("DISBURSEDAMOUNT") : "");
                map.put("FUEL_TYPE", rs.getString("FUEL_TYPE") != null ? rs.getString("FUEL_TYPE") : "");
                map.put("Mobile", rs.getString("Mobile") != null ? rs.getString("Mobile") : "");
                map.put("LOAN_CHARGES_INCOME_IN_RS", rs.getString("LOAN_CHARGES_INCOME_IN_RS") != null ? rs.getString("LOAN_CHARGES_INCOME_IN_RS") : "");
                map.put("CHANNEL", rs.getString("CHANNEL") != null ? rs.getString("CHANNEL") : "");
                map.put("SENDBACKTOBRANCH_COUNT", rs.getString("SENDBACKTOBRANCH_COUNT") != null ? rs.getString("SENDBACKTOBRANCH_COUNT") : "");
                map.put("LATEST_QUEUE_USER", rs.getString("LATEST_QUEUE_USER") != null ? rs.getString("LATEST_QUEUE_USER") : "");
                map.put("DST_CODE", rs.getString("DST_CODE") != null ? rs.getString("DST_CODE") : "");
                map.put("DST_NAME", rs.getString("DST_NAME") != null ? rs.getString("DST_NAME") : "");
                map.put("DSA_CODE", rs.getString("DSA_CODE") != null ? rs.getString("DSA_CODE") : "");
                map.put("DEALER_CODE", rs.getString("DEALER_CODE") != null ? rs.getString("DEALER_CODE") : "");
                map.put("DEALER_SUB_CODE", rs.getString("DEALER_SUB_CODE") != null ? rs.getString("DEALER_SUB_CODE") : "");
                map.put("MAKE_NAME", rs.getString("MAKE_NAME") != null ? rs.getString("MAKE_NAME") : "");
                map.put("DEALER_NAME", rs.getString("DEALER_NAME") != null ? rs.getString("DEALER_NAME") : "");
                map.put("DEALER_CITY_NAME", rs.getString("DEALER_CITY_NAME") != null ? rs.getString("DEALER_CITY_NAME") : "");
                map.put("AUTODEALER_SOURCED", rs.getString("AUTODEALER_SOURCED") != null ? rs.getString("AUTODEALER_SOURCED") : "");
                map.put("DO_LATEST_SUBMISSION_DATE", rs.getString("DO_LATEST_SUBMISSION_DATE") != null ? rs.getString("DO_LATEST_SUBMISSION_DATE") : "");
                map.put("CREATEDBYPPC", rs.getString("CREATEDBYPPC") != null ? rs.getString("CREATEDBYPPC") : "");
                map.put("CREATEDBYNAME", rs.getString("CREATEDBYNAME") != null ? rs.getString("CREATEDBYNAME") : "");
                map.put("CREATEDATE", rs.getString("CREATEDATE") != null ? rs.getString("CREATEDATE") : "");
                map.put("LTV_PER", rs.getString("LTV_PER") != null ? rs.getString("LTV_PER") : "");
                map.put("FOIR_TYPE", rs.getString("FOIR_TYPE") != null ? rs.getString("FOIR_TYPE") : "");
                map.put("STP", rs.getString("STP") != null ? rs.getString("STP") : "");
                map.put("MODIFICATION_INITIATED", rs.getString("MODIFICATION_INITIATED") != null ? rs.getString("MODIFICATION_INITIATED") : "");
                map.put("ACC_NUMBER", rs.getString("ACC_NUMBER") != null ? rs.getString("ACC_NUMBER") : "");
                map.put("ASSET_VALUE_LAKHS", rs.getString("ASSET_VALUE_LAKHS") != null ? rs.getString("ASSET_VALUE_LAKHS") : "");
                return map;
            });
        } catch (Exception e) {
            // Handle the case where no category is found
            e.printStackTrace();
            return null;
        }
    }


    public List<Map<String, String>> vehicleLoanDeviationsReport() {
        String sql = """
                    with vla as (
                    SELECT 
                        wi_num,
                        RTRIM(XMLAGG(XMLELEMENT(e, amber_desc || ', ').EXTRACT('//text()') ORDER BY NULL).GETCLOBVAL(), ', ') AS all_deviations
                    FROM 
                        vlamber where nvl(vlamber.active_flg,'N')='Y' and nvl(vlamber.DEL_flg,'N')='N' AND vlamber.appr_auth_action='APPROVE' 
                    GROUP BY 
                        wi_num
                    )
                    select mr.reg_code||'-'||mr.reg_name region, mb.sol_id||'-'||mb.br_name branch,vlm.wi_num application_no,vlm.cust_name customer_name, 'Vehicle Loan - Car' Loan_Facility_Type, vle.loan_amt Requested_loan_Amt,
                    vle.sanc_amount_recommended sanctioned_amount,vlm.san_date sanction_date,vlm.san_user||'-'||hrp.ppc_name Sanction_authority,'Fresh' Product_category, vla.all_deviations
                    from vehicle_loan_master vlm
                    join misbmt@mybank mb on vlm.sol_id=mb.sol_id
                    join misreg@mybank mr on mb.reg_code=mr.reg_code
                    join vla on vlm.wi_num=vla.WI_NUM
                    join vehicle_loan_eligibility vle on vlm.wi_num=vle.wi_num
                    join mishrp@mybank hrp on vlm.san_user=hrp.ppcno
                    where vlm.san_flg='Y' and vlm.stp='NONSTP' 
                    and vlm.cust_name is not null and nvl(vlm.REJ_FLG,'N')='N'
                    order by vlm.san_date
                    
                """;

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("REGION", rs.getString("REGION"));
                map.put("BRANCH", rs.getString("BRANCH"));
                map.put("APPLICATION_NO", rs.getString("APPLICATION_NO"));
                map.put("CUSTOMER_NAME", rs.getString("CUSTOMER_NAME"));
                map.put("LOAN_FACILITY_TYPE", rs.getString("LOAN_FACILITY_TYPE"));
                map.put("REQUESTED_LOAN_AMT", rs.getString("REQUESTED_LOAN_AMT"));
                map.put("SANCTIONED_AMOUNT", rs.getString("SANCTIONED_AMOUNT"));
                map.put("SANCTION_DATE", rs.getString("SANCTION_DATE"));
                map.put("SANCTION_AUTHORITY", rs.getString("SANCTION_AUTHORITY"));
                map.put("PRODUCT_CATEGORY", rs.getString("PRODUCT_CATEGORY"));
                map.put("ALL_DEVIATIONS", rs.getString("ALL_DEVIATIONS"));
                return map;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, String>> vehicleLoanLiveReport(String statVal) {
        String sql = "";
        if (statVal.equals("CONCESSION_QUEUE")) {
            sql = "select vehicle_loan_master.SOL_ID,MISBMT.BR_NAME BRANCH,vehicle_loan_master.WI_NUM APPLICATION_NO,vehicle_loan_master.CUST_NAME CUSTOMER_NAME,nvl(MISRCT.CODEDESC,' ') queue,vehicle_loan_master.channel, nvl(vehicle_loan_master.STP,' ') STP,decode(vehicle_loan_master.SAN_FLG,'Y','Sanctioned','Pending') SANCTIONED, nvl(vehicle_loan_master.REJ_FLG,' ') AS REJECTED,nvl(vehicle_loan_PROGRAM.LOAN_PROGRAM,' ') LOAN_PROGRAM, nvl(vehicle_loan_ELIGIBILITY.LOAN_AMT,'0') REQUESTEDAMT, nvl(NVL(vehicle_loan_ELIGIBILITY.LOAN_AMOUNT_RECOMMENDED_CPC,vehicle_loan_ELIGIBILITY.ELIGIBLE_LOAN_AMT),'0') ELIGIBILEAMT , vehicle_loan_allotment.do_ppc do_ppc, nvl(hrmsempdtls.ppc_name,' ') do_name,filtered_task.TASK_TYPE,filtered_task.CREATED_DATE,filtered_task.STATUS from vehicle_loan_master join misbmt@mybank on vehicle_loan_master.sol_id=misbmt.sol_id left outer join misrct on vehicle_loan_master.QUEUE=misrct.codevalue and misrct.CODETYPE='QT' LEFT OUTER JOIN vehicle_loan_APPLICANTS MAINAPPLICANT ON vehicle_loan_master.SLNO=MAINAPPLICANT.SLNO AND MAINAPPLICANT.APPLICANT_TYPE='A'  AND NVL(MAINAPPLICANT.DEL_FLG,'N')='N' LEFT OUTER JOIN vehicle_loan_PROGRAM ON MAINAPPLICANT.APPLICANT_ID=vehicle_loan_PROGRAM.APPLICANT_ID AND NVL(vehicle_loan_PROGRAM.DEL_FLG,'N')='N' LEFT OUTER JOIN vehicle_loan_ELIGIBILITY ON vehicle_loan_master.SLNO=vehicle_loan_ELIGIBILITY.SLNO AND NVL(vehicle_loan_ELIGIBILITY.del_flg,'N')='N' left outer join (SELECT SLNO,TASK_TYPE,CREATED_DATE,STATUS FROM VEHICLE_LOAN_SUBQUEUE_TASK WHERE  TASK_TYPE IN('ROI_WAIVER','CHARGE_WAIVER')) filtered_task on  vehicle_loan_master.slno=filtered_task.slno left outer join vehicle_loan_allotment on vehicle_loan_master.slno=vehicle_loan_allotment.slno and vehicle_loan_allotment.active_flg='Y' and NVL(vehicle_loan_allotment.del_flg,'N')='N' left outer join hrmsempdtls@mybank on vehicle_loan_allotment.DO_PPC=hrmsempdtls.ppcno where vehicle_loan_master.CUST_NAME is not null and nvl(vehicle_loan_master.REJ_FLG,'N')='N' AND vehicle_loan_master.queue='BD' AND TASK_TYPE IS NOT NULL AND filtered_task.STATUS='PENDING' ORDER BY 1";
        } else if (statVal.equals("CIF_CREATION")) {
            sql = "select vehicle_loan_master.SOL_ID,MISBMT.BR_NAME BRANCH,vehicle_loan_master.WI_NUM APPLICATION_NO,vehicle_loan_master.CUST_NAME CUSTOMER_NAME,nvl(MISRCT.CODEDESC,' ') queue,vehicle_loan_master.channel, nvl(vehicle_loan_master.STP,' ') STP,decode(vehicle_loan_master.SAN_FLG,'Y','Sanctioned','Pending') SANCTIONED, nvl(vehicle_loan_master.REJ_FLG,' ') AS REJECTED,nvl(vehicle_loan_PROGRAM.LOAN_PROGRAM,' ') LOAN_PROGRAM, nvl(vehicle_loan_ELIGIBILITY.LOAN_AMT,'0') REQUESTEDAMT, nvl(NVL(vehicle_loan_ELIGIBILITY.LOAN_AMOUNT_RECOMMENDED_CPC,vehicle_loan_ELIGIBILITY.ELIGIBLE_LOAN_AMT),'0') ELIGIBILEAMT , vehicle_loan_allotment.do_ppc do_ppc, nvl(hrmsempdtls.ppc_name,' ') do_name,filtered_task.TASK_TYPE,filtered_task.CREATED_DATE,filtered_task.STATUS from vehicle_loan_master join misbmt@mybank on vehicle_loan_master.sol_id=misbmt.sol_id left outer join misrct on vehicle_loan_master.QUEUE=misrct.codevalue and misrct.CODETYPE='QT' LEFT OUTER JOIN vehicle_loan_APPLICANTS MAINAPPLICANT ON vehicle_loan_master.SLNO=MAINAPPLICANT.SLNO AND MAINAPPLICANT.APPLICANT_TYPE='A'  AND NVL(MAINAPPLICANT.DEL_FLG,'N')='N' LEFT OUTER JOIN vehicle_loan_PROGRAM ON MAINAPPLICANT.APPLICANT_ID=vehicle_loan_PROGRAM.APPLICANT_ID AND NVL(vehicle_loan_PROGRAM.DEL_FLG,'N')='N' LEFT OUTER JOIN vehicle_loan_ELIGIBILITY ON vehicle_loan_master.SLNO=vehicle_loan_ELIGIBILITY.SLNO AND NVL(vehicle_loan_ELIGIBILITY.del_flg,'N')='N' left outer join (SELECT SLNO,TASK_TYPE,CREATED_DATE,STATUS FROM VEHICLE_LOAN_SUBQUEUE_TASK WHERE  TASK_TYPE IN('CIF_CREATION')) filtered_task on  vehicle_loan_master.slno=filtered_task.slno left outer join vehicle_loan_allotment on vehicle_loan_master.slno=vehicle_loan_allotment.slno and vehicle_loan_allotment.active_flg='Y' and NVL(vehicle_loan_allotment.del_flg,'N')='N' left outer join hrmsempdtls@mybank on vehicle_loan_allotment.DO_PPC=hrmsempdtls.ppcno where vehicle_loan_master.CUST_NAME is not null and nvl(vehicle_loan_master.REJ_FLG,'N')='N' AND vehicle_loan_master.queue='BD' AND TASK_TYPE IS NOT NULL AND filtered_task.STATUS='PENDING' ORDER BY 1";
        } else {
            sql = "select vehicle_loan_master.SOL_ID,MISBMT.BR_NAME BRANCH,vehicle_loan_master.WI_NUM APPLICATION_NO,vehicle_loan_master.CUST_NAME CUSTOMER_NAME,nvl(MISRCT.CODEDESC,' ') queue,vehicle_loan_master.channel, nvl(vehicle_loan_master.STP,' ') STP," +
                    "decode(vehicle_loan_master.SAN_FLG,'Y','Sanctioned','Pending') SANCTIONED, nvl(vehicle_loan_master.REJ_FLG,' ') AS REJECTED,nvl(vehicle_loan_master.QUEUE,' ') AS QUEUE, QUEUE_DATE," +
                    "nvl(vehicle_loan_PROGRAM.LOAN_PROGRAM,' ') LOAN_PROGRAM, nvl(vehicle_loan_ELIGIBILITY.LOAN_AMT,'0') REQUESTEDAMT, nvl(NVL(vehicle_loan_ELIGIBILITY.LOAN_AMOUNT_RECOMMENDED_CPC,vehicle_loan_ELIGIBILITY.ELIGIBLE_LOAN_AMT),'0') ELIGIBILEAMT , vehicle_loan_allotment.do_ppc do_ppc, nvl(hrmsempdtls.ppc_name,' ') do_name " +
                    "from vehicle_loan_master " +
                    "join misbmt@mybank on vehicle_loan_master.sol_id=misbmt.sol_id " +
                    "left outer join misrct on vehicle_loan_master.QUEUE=misrct.codevalue and misrct.CODETYPE='QT' " +
                    "LEFT OUTER JOIN vehicle_loan_APPLICANTS MAINAPPLICANT ON vehicle_loan_master.SLNO=MAINAPPLICANT.SLNO AND MAINAPPLICANT.APPLICANT_TYPE='A'  AND NVL(MAINAPPLICANT.DEL_FLG,'N')='N' " +
                    "LEFT OUTER JOIN vehicle_loan_PROGRAM ON MAINAPPLICANT.APPLICANT_ID=vehicle_loan_PROGRAM.APPLICANT_ID AND NVL(vehicle_loan_PROGRAM.DEL_FLG,'N')='N' " +
                    "LEFT OUTER JOIN vehicle_loan_ELIGIBILITY ON vehicle_loan_master.SLNO=vehicle_loan_ELIGIBILITY.SLNO AND NVL(vehicle_loan_ELIGIBILITY.del_flg,'N')='N' " +
                    "left outer join vehicle_loan_allotment on vehicle_loan_master.slno=vehicle_loan_allotment.slno and vehicle_loan_allotment.active_flg='Y' and NVL(vehicle_loan_allotment.del_flg,'N')='N' " +
                    "left outer join hrmsempdtls@mybank on vehicle_loan_allotment.DO_PPC=hrmsempdtls.ppcno " +
                    "where vehicle_loan_master.CUST_NAME is not null and nvl(vehicle_loan_master.REJ_FLG,'N')='N' and vehicle_loan_master.queue='" + statVal + "' " +
                    "ORDER BY 1";
        }

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("BRANCH", rs.getString("BRANCH"));
                map.put("APPLICATION_NO", rs.getString("APPLICATION_NO"));
                map.put("CUSTOMER_NAME", rs.getString("CUSTOMER_NAME"));
                map.put("LOAN_FACILITY_TYPE", rs.getString("LOAN_PROGRAM"));
                map.put("CHANNEL", rs.getString("CHANNEL"));
                map.put("REQUESTED_LOAN_AMT", rs.getString("REQUESTEDAMT"));
                map.put("SANCTIONED_AMOUNT", rs.getString("ELIGIBILEAMT"));
                if (statVal.equals("CONCESSION_QUEUE") || statVal.equals("CIF_CREATION")) {
                    map.put("QUEUE", rs.getString("TASK_TYPE"));
                    map.put("QUEUE_DATE", rs.getString("CREATED_DATE"));
                } else {
                    map.put("QUEUE", rs.getString("QUEUE"));
                    map.put("QUEUE_DATE", rs.getString("QUEUE_DATE"));
                }
                return map;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, String>> getManualNachReport() {
        String sql = """
                    SELECT 
                        a.WI_NUM,
                        a.CUST_NAME,
                        ' '||a.ACC_NUMBER ACC_NUMBER,
                        A.SOL_ID,
                        BM.BR_NAME,
                        b.BANK_NAME,
                        c.MANDATE_MODE,
                        c.COLLECTION_AMOUNT,
                        c.INSTALMENT_START_DATE,
                        c.TENOR,
                        a.ACC_OPEN_DATE 
                    FROM 
                        VEHICLE_LOAN_MASTER a,
                        VEHICLE_LOAN_REPAYMENT b,
                        VEHICLE_LOAN_NACH_MANDATE c,
                        MISBMT@MYBANK BM 
                    WHERE 
                        a.WI_NUM = b.WI_NUM 
                        AND a.WI_NUM = c.WI_NUM 
                        AND C.MANDATE_MODE = 'MANUAL' 
                        AND BM.SOL_ID = A.SOL_ID 
                        AND B.ACCOUNT_NUMBER NOT IN (
                            SELECT REFERENCE_ACCOUNT_NUMBER 
                            FROM CUSTOM.ENACH_MANDATE@finacle10 
                            WHERE CHANNEL_ID = 'IBANKLOS'
                        )
                    ORDER BY c.INSTALMENT_START_DATE DESC
                """;

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("WI_NUM", rs.getString("WI_NUM"));
                map.put("CUST_NAME", rs.getString("CUST_NAME"));
                map.put("ACC_NUMBER", rs.getString("ACC_NUMBER"));
                map.put("SOL_ID", rs.getString("SOL_ID"));
                map.put("BR_NAME", rs.getString("BR_NAME"));
                map.put("BANK_NAME", rs.getString("BANK_NAME"));
                map.put("MANDATE_MODE", rs.getString("MANDATE_MODE"));
                map.put("COLLECTION_AMOUNT", rs.getString("COLLECTION_AMOUNT"));
                map.put("INSTALMENT_START_DATE", rs.getString("INSTALMENT_START_DATE"));
                map.put("TENOR", rs.getString("TENOR"));
                map.put("ACC_OPEN_DATE", rs.getString("ACC_OPEN_DATE"));
                return map;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<MSSFCustomerDTO> getMSSFReport() {
        List<Object[]> results = mssfCustomerRepository.findAllMSSFCustomersWithLockDetails();
        return results.stream().map(this::mapToMSSFCustomerDTO).collect(Collectors.toList());

    }

    private MSSFCustomerDTO mapToMSSFCustomerDTO(Object[] result) {
    // With native query, we get individual column values instead of entity objects
    // Based on your actual query result column order (total 91 columns)

    MSSFCustomerDTO dto = new MSSFCustomerDTO();

    // Map MSSFCustomerData fields based on actual query result order:
    String refNo = (String) result[0];                    // REF_NO (index 0)
    String pdSalutation = (String) result[1];             // PD_SALUTATION (index 1)
    String pdFirstName = (String) result[2];              // PD_FIRST_NAME (index 2)
    String pdMiddleName = (String) result[3];             // PD_MIDDLE_NAME (index 3)
    String pdLastName = (String) result[4];               // PD_LAST_NAME (index 4)

    // Handle mobile number - it's NUMBER in DB, might come as BigDecimal
    String pdMobile = "";
    Object mobileObj = result[29]; // PD_MOBILE (index 29)
    if (mobileObj != null) {
        if (mobileObj instanceof BigDecimal) {
            pdMobile = ((BigDecimal) mobileObj).toString();
        } else {
            pdMobile = mobileObj.toString();
        }
    }

    String pdEmail = (String) result[30];                 // PD_EMAIL (index 30)
    String dlrCode = (String) result[64];                 // DLR_CODE (index 66)

    // Handle loan amount - it's NUMBER in DB, might come as BigDecimal
    BigDecimal laLoanAmt = null;
    Object loanAmtObj = result[66]; // LA_LOAN_AMT (index 68)
    if (loanAmtObj != null) {
        if (loanAmtObj instanceof BigDecimal) {
            laLoanAmt = (BigDecimal) loanAmtObj;
        } else if (loanAmtObj instanceof Number) {
            laLoanAmt = new BigDecimal(loanAmtObj.toString());
        }
    }

    // Handle created date - TIMESTAMP(6) from database
    LocalDateTime createdDate = null;
    Object dateObj = result[72]; // CREATED_DATE (index 80)
    if (dateObj instanceof Timestamp) {
        createdDate = ((Timestamp) dateObj).toLocalDateTime();
    } else if (dateObj instanceof Date) {
        createdDate = ((Date) dateObj).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    String status = (String) result[88];                  // STATUS (should be around index 89)

    // Set basic customer fields
    dto.setRefNo(refNo);
    dto.setCustomerName(buildCustomerName(pdSalutation, pdFirstName, pdMiddleName, pdLastName));
    dto.setMobile(Long.valueOf(pdMobile));
    dto.setEmail(pdEmail);
    dto.setDealerCode(dlrCode);
    dto.setLoanAmount(laLoanAmt.doubleValue());
    dto.setCreatedDate(createdDate);

    // Map the additional fields from the joins (last 7 columns)
    String lockFlg = (String) result[89];           // LOCKFLG
    String lockedBy = (String) result[90];          // LOCKEDBY
    String solId = (String) result[91];             // SOLID
    String dealerName = (String) result[92];        // DEALERNAME
    String workItemNumber = (String) result[93];    // WORKITEMNUMBER
    String workItemStatus = (String) result[94];    // WORKITEMSTATUS
    String bankName = (String) result[95];          // BANKNAME (array index 95 = Excel column 96)

    // Set the mapped fields
    dto.setSolId(bankName+" - "+solId);
    dto.setDealerName(dealerName);
    //dto.setBankName(bankName); // Make sure to add this field to your MSSFCustomerDTO
    dto.setWorkItemNumber(workItemNumber);
    dto.setWorkItemStatus(workItemStatus);

    // Set lock details
    MSSFLockDTO lockDTO = new MSSFLockDTO();
    lockDTO.setLockedBy(lockedBy);
    dto.setLockDetails(lockDTO);

    // Handle status mapping
    if (status == null || status.trim().isEmpty() || "PENDING".equals(status)) {
        dto.setStatus("PENDING");
    } else if ("I".equals(status)) {
        dto.setStatus("INITIATED");
    } else {
        dto.setStatus(status);
    }

    return dto;
}

// Helper method to build customer name (updated to include middle name)
private String buildCustomerName(String salutation, String firstName, String middleName, String lastName) {
    StringBuilder name = new StringBuilder();
    if (salutation != null && !salutation.trim().isEmpty()) {
        name.append(salutation.trim()).append(" ");
    }
    if (firstName != null && !firstName.trim().isEmpty()) {
        name.append(firstName.trim()).append(" ");
    }
    if (middleName != null && !middleName.trim().isEmpty()) {
        name.append(middleName.trim()).append(" ");
    }
    if (lastName != null && !lastName.trim().isEmpty()) {
        name.append(lastName.trim());
    }
    return name.toString().trim();
}


    private MSSFCustomerDTO mapToMSSFCustomerDTOx(Object[] result) {
        MSSFCustomerData customer = (MSSFCustomerData) result[0];
        String lockFlg = (String) result[1];
        String lockedBy = (String) result[2];
        String solId = (String) result[3];
        String dealerName = (String) result[4];
        String workItemNumber = (String) result[5];
        String workItemStatus = (String) result[6];
        String bankName = (String) result[result.length - 1]; // bankName
        log.info("THE BANK NAME IS {}", bankName);
        //String solName = fetchRepository.getSolName(solId) +"-"+solId;

        MSSFCustomerDTO dto = new MSSFCustomerDTO();
        dto.setRefNo(customer.getRefNo());
        dto.setCustomerName(buildCustomerName(customer));
        dto.setMobile(customer.getPdMobile());
        dto.setEmail(customer.getPdEmail());
        dto.setDealerCode(customer.getDlrCode());
        dto.setSolId(solId);
        dto.setDealerName(dealerName);
        dto.setLoanAmount(customer.getLaLoanAmt());
        dto.setWorkItemNumber(workItemNumber);
        dto.setWorkItemStatus(workItemStatus);
        dto.setCreatedDate(customer.getCreatedDate());

        // Set lock details
        MSSFLockDTO lockDTO = new MSSFLockDTO();
        lockDTO.setLockedBy(lockedBy);
        dto.setLockDetails(lockDTO);
        String status = customer.getStatus();
        if (status == null || status.trim().isEmpty() || "PENDING".equals(status)) {
            dto.setStatus("PENDING");
        } else if ("I".equals(status)) {
            dto.setStatus("INITIATED");
        } else {
            dto.setStatus(status);
        }

        return dto;
    }

    private String buildCustomerName(MSSFCustomerData customer) {
        StringBuilder name = new StringBuilder();
        if (customer.getPdSalutation() != null) {
            name.append(customer.getPdSalutation()).append(" ");
        }
        if (customer.getPdFirstName() != null) {
            name.append(customer.getPdFirstName()).append(" ");
        }
        if (customer.getPdMiddleName() != null) {
            name.append(customer.getPdMiddleName()).append(" ");
        }
        if (customer.getPdLastName() != null) {
            name.append(customer.getPdLastName());
        }
        return name.toString().trim();
    }


}
