package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.dto.LoanVehicleDto;
import com.sib.ibanklosucl.dto.VLDocMas;
import com.sib.ibanklosucl.dto.VehicleEmpProgram;
import com.sib.ibanklosucl.dto.VkycSlnoAppidDto;
import com.sib.ibanklosucl.dto.doc.AgreementDetailsDTO;
import com.sib.ibanklosucl.dto.doc.LegalityFetchDTO;
import com.sib.ibanklosucl.dto.acopn.ROIProcFeeDTO;
import com.sib.ibanklosucl.dto.acopn.RepayAcctDTO;
import com.sib.ibanklosucl.dto.acopn.SanctionDetailsDTO;
import com.sib.ibanklosucl.dto.doc.WaiverDto;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.Misrct;
import com.sib.ibanklosucl.utilies.CurrencyFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.math.BigDecimal;

@Service
@Slf4j
public class FetchRepository {

    @Value("${BogPoolAcc}")
    private String bogPoolAcc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Map<String, String>> getPPCDetails(String ppcno) {
        String sql = "SELECT PPCNO,PPC_NAME FROM HRMSEMPDTLS@MYBANK WHERE PPCNO = ?";
        return jdbcTemplate.query(sql, new Object[]{ppcno}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("codedesc", rs.getString("PPC_NAME") + "(" + rs.getString("PPCNO") + ")");
            map.put("codevalue", rs.getString("PPCNO"));
            return map;
        });
    }

    public String getUserSessionID(String ppcno) {
        String sql = "select SESSION_ID from LOSUSERS@MYBANK where ppcno=:ppcno";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ppcno", ppcno);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    public String getDocument(String slno, String docType) {
        try {
            String sql = " select DOCUMENT from VEHICLE_LOAN_DOCUMENTS where slno=:slno and DEL_FLG='N' and DOCUMENT_TYPE=:docType and rownum=1 order by cmdate desc ";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("slno", slno)
                    .addValue("docType", docType);
            return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String getDocumentByApplicantId(String docType, String applicantId) {

        try {
            String sql = " select DOCUMENT from VEHICLE_LOAN_DOCUMENTS where  DEL_FLG='N' and DOCUMENT_TYPE=:docType and APPLICANT_ID=:applicantId ";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("docType", docType)
                    .addValue("applicantId", applicantId);
            return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String getUserRole(String ppcno) {
        String sql = "select LEVEL_NAME from  vlrbcpccheckerlevel@mybank where del_flag='N' and ppc_no=:ppcno";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ppcno", ppcno);
        return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
    }

    public String getDODetails(String wiNum) {
        String sql = "select DO_PPC from VEHICLE_LOAN_ALLOTMENT where DEL_FLG='N' AND ACTIVE_FLG='Y' AND WI_NUM=:wiNum";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("wiNum", wiNum);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
        } catch (EmptyResultDataAccessException e) {
            return "N/A";
        }
    }

    public String getCustName(String cust_id) {
        String sql = "select cust_last_name FROM CRMUSER.accounts@finacle10 WHERE orgkey=:cust_id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("cust_id", cust_id);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String fetchName(String ip) {
        String sql = "select ppcno from ConSolUpdMast@MYBANK where ip=:ip and delflag='N' ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ip", ip);
        try {
            //return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
            return "11965";
        } catch (EmptyResultDataAccessException e) {
            return "";
        }
    }

    public String getPPCName(String ppcno) {
        String sql = "SELECT PPC_NAME FROM HRMSEMPDTLS@MYBANK WHERE PPCNO = :ppcno";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ppcno", ppcno);
        return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
    }

    public String getSolName(String sol_id) {
        String sql = "SELECT trim(BR_NAME) BR_NAME FROM MISBMT@MYBANK WHERE close_date is NULL and sol_id = :sol_id UNION SELECT trim(REG_NAME) BR_NAME FROM MISREG@MYBANK WHERE CLOSE_DATE IS null and reg_code = :sol_id ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("sol_id", sol_id);
        return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
    }

    public String getROName(String sol_id) {
        String sql = "SELECT trim(REG_NAME) BR_NAME FROM MISREG@MYBANK WHERE CLOSE_DATE IS null and reg_code = ( select reg_code from misbmt@mybank where sol_id = :sol_id )";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("sol_id", sol_id);
        //return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
        } catch (EmptyResultDataAccessException ex) {
            return "";
        }
    }

    public Optional<String> getprocesseduser(String wi_num, String queue) {
        String sql = "SELECT locked_by||'~'||locked_on from vehicle_loan_lock where ino=(select max(ino) FROM vehicle_loan_lock WHERE wi_num = :wi_num and queue=:queue AND DEL_FLG='N')  ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("wi_num", wi_num)
                .addValue("queue", queue);
        //return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, String.class));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Map<String, String>> getRsmPPC(String ppcno) {
        String sql = "SELECT PPCNO,PPC_NAME FROM HRMSEMPDTLS@MYBANK WHERE  (REGEXP_REPLACE(jobrole,'[^0-9A-Za-z&,() ]', '') ='REGIONAL SALES MANAGER ALBG' OR REGEXP_REPLACE(jobrole,'[^0-9A-Za-z&,() ]', '') ='SALES SUPPORT OFFICER RAH(HLBG)') and  PPCNO = ?";
        return jdbcTemplate.query(sql, new Object[]{ppcno}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("codedesc", rs.getString("PPC_NAME") + "(" + rs.getString("PPCNO") + ")");
            map.put("codevalue", rs.getString("PPCNO"));
            return map;
        });
    }

    public List<Map<String, String>> getRsmSol(String sol_id, String ppcno) {
        String sql = " select p.sol_id,br_name from ppcbussolset@mybank p join misbmt@mybank m on p.sol_id=m.sol_id  where m.close_date is null and  p.home_sol in ('8032','8063') and trunc(sysdate) between p.start_date and p.end_date and p.sol_id=:sol_id and  ppcno=:ppcno";
        return jdbcTemplate.query(sql, new Object[]{sol_id, ppcno}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("codedesc", rs.getString("br_name").toUpperCase().trim() + "(" + rs.getString("sol_id") + ")");
            map.put("codevalue", rs.getString("sol_id"));
            return map;
        });
    }

    public List<Map<String, String>> getRSMEmails(String sol_id) {
        String sql = "select emailid from hrmsempdtls@mybank where ppcno in (select distinct ppcno from ppcbussolset@mybank p  where  p.home_sol='8032' and p.sol_id = :sol_id  and status_flag ='Y'  and trunc(sysdate) between p.start_date and p.end_date) and emailid is not null and emailid != '-'";
        return jdbcTemplate.query(sql, new Object[]{sol_id}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            String email = rs.getString("emailid");
            map.put("email", email != null ? email.toLowerCase().trim() : null);
            return map;
        });
    }


    public List<Map<String, String>> findMatches(String searchText) {
        String sql = "SELECT ppcno,ppc_name, designation, officename " +
                "FROM hrmsempdtls@mybank " +
                "WHERE SEARCH_INDEX LIKE '%' || ? || '%' " +
                "OR officename LIKE '%' || ? || '%'  order by category desc,desig";

        return jdbcTemplate.query(sql, new Object[]{searchText, searchText}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("codedesc", rs.getString("ppc_name").toUpperCase().trim() + " (" + rs.getString("ppcno") + ")");
            map.put("codevalue", rs.getString("ppcno"));
            map.put("name", rs.getString("ppc_name").toUpperCase().trim());
            map.put("ppc", rs.getString("ppcno"));
            map.put("designation", rs.getString("designation"));
            map.put("office", rs.getString("officename"));
            map.put("office", rs.getString("officename"));
            return map;
        });
    }


    public List<String> getRsmSols(String ppcno) {
        String sql = " select p.sol_id from ppcbussolset@mybank p join misbmt@mybank m on p.sol_id=m.sol_id  where m.close_date is null and  p.home_sol='8032' and trunc(sysdate) between p.start_date and p.end_date and  ppcno=:ppcno";
        return jdbcTemplate.query(sql, new Object[]{ppcno}, (rs, rowNum) -> {
            return rs.getString("sol_id");
        });
    }

    public List<VLDocMas> getDocMas(String slno) {
        String sql = "SELECT v.LABEL_CODE, v.LABEL_NAME, v.FILE_NAME, v.MANDATORY, v.GENERIC, v.APPLICANT, v.COAPPLICANT, v.GURANTOR FROM VLDOCMAS@MYBANK v join vldoc@mybank l on v.LABEL_CODE=l.LABEL_CODE and v.LABEL_NAME=l.LABEL_NAME WHERE l.DEL_FLAG='N' and v.DEL_FLAG='N' AND (v.PROGRAM_NAME,v.EMPLOYMENT_NAME) in ( select LOAN_PROGRAM,employment_type from VEHICLE_LOAN_PROGRAM  v LEFT OUTER JOIN  VEHICLE_LOAN_EMPLOYMENT e on v.applicant_id=e.applicant_id where v.del_flg='N' and e.slno=?  union select LOAN_PROGRAM,'All' from VEHICLE_LOAN_PROGRAM  v LEFT OUTER JOIN  VEHICLE_LOAN_EMPLOYMENT e on v.applicant_id=e.applicant_id where v.del_flg='N' and e.slno=?)";
        return jdbcTemplate.query(sql, new Object[]{slno, slno}, new VLDocMasRowMapper());
    }

    public List<String> getMobCodeMaster() {
        String sql = "SELECT distinct(isdn_cd) code from custom.sibmobisdn@finacle10 where isdn_cd is not null order by isdn_cd*1";
        List<String> code = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("code")
        );

        return code;
    }

    public List<Misrct> getBankCode() {
        return jdbcTemplate.query(
                "select BANK_ID ,BANK_NAME from NPCILIVEBANKDTLS@enach where NETBANK_FLAG='Active' or debitcard_flag='Active' order by 2",
                new Object[]{},
                new RowMapper<Misrct>() {
                    @Override
                    public Misrct mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Misrct dto = new Misrct();
                        dto.setCodevalue(rs.getString("BANK_ID"));
                        dto.setCodedesc(rs.getString("BANK_NAME"));
                        return dto;
                    }
                }
        );
    }

    public List<VLDocMas> getcountDocMas(String program, String employment) {
        String sql = "SELECT count(*) FROM VLDOCMAS@MYBANK WHERE DEL_FLAG='N' AND MANDATORY AND PROGRAM_NAME=? AND EMPLOYMENT_NAME=?";
        return jdbcTemplate.query(sql, new Object[]{program, employment}, new VLDocMasRowMapper());
    }

    public String getWIProgram(Long slno) {
        try {
            String sql = "SELECT loan_program " +
                    "FROM VEHICLE_LOAN_applicants a " +
                    "JOIN VEHICLE_LOAN_PROGRAM l ON l.applicant_id = a.applicant_id " +
                    "WHERE l.del_flg = 'N' and a.del_flg='N' AND l.loan_program!='NONE' and rownum=1 AND a.slno = ?";

            return jdbcTemplate.queryForObject(sql, new Object[]{slno}, String.class);
        } catch (EmptyResultDataAccessException ex) {
            return "";
        }
    }

    public String getWIQueue(Long slno) {
        try {
            String sql = "select queue from vehicle_loan_master WHERE  slno = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{slno}, String.class);
        } catch (EmptyResultDataAccessException ex) {
            return "";
        }
    }


    public List<VehicleEmpProgram> getEmpProgram(Long slno) {
        String sql = "SELECT employment_type, loan_program, a.applicant_id, applicant_type, sib_customer, resident_flg , appl_name ,bureau_score,avg_sal,abb,dep_amt,to_char(a.appl_dob,'yyyy-mm-dd') appl_dob " +
                "FROM VEHICLE_LOAN_applicants a " +
                "JOIN VEHICLE_LOAN_EMPLOYMENT e ON a.applicant_id = e.applicant_id " +
                "JOIN VEHICLE_LOAN_PROGRAM l ON l.applicant_id = e.applicant_id " +
                "JOIN VEHICLE_LOAN_CREDIT c ON c.applicant_id = l.applicant_id " +
                "WHERE e.del_flg = 'N' AND l.del_flg = 'N' and c.del_flg='N' and a.del_flg='N' AND e.slno = ?";

        return jdbcTemplate.query(sql, new Object[]{slno}, new VehicleEmpProgramRowMapper());
    }

    public VehicleEmpProgram getEmpProgramforApplicant(Long appid) {
        String sql = "SELECT employment_type, loan_program, a.applicant_id, applicant_type, sib_customer, resident_flg , appl_name ,bureau_score,avg_sal,abb,dep_amt,to_char(a.appl_dob,'yyyy-mm-dd') appl_dob " +
                "FROM VEHICLE_LOAN_applicants a " +
                "JOIN VEHICLE_LOAN_EMPLOYMENT e ON a.applicant_id = e.applicant_id " +
                "JOIN VEHICLE_LOAN_PROGRAM l ON l.applicant_id = e.applicant_id " +
                "JOIN VEHICLE_LOAN_CREDIT c ON c.applicant_id = l.applicant_id " +
                "WHERE e.del_flg = 'N' AND l.del_flg = 'N' and c.del_flg='N' and a.del_flg='N' AND a.applicant_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{appid}, new VehicleEmpProgramRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Map<String, String>> getIFSCAccountDtls(String dealer_code, String dealer_sub_code, String oem_id, String city_id) {
        String sql = "select FORACID,BANK_NAME, IFSC from dealersubmas@mybank a inner join dealermas@mybank b on a.slno=b.slno where b.dealer_code=? and b.dealer_sub_code = ? and oem_id=? and city_id=? and a.del_flg='N' and b.del_flg='N'";
        try {
            return jdbcTemplate.query(sql, new Object[]{dealer_code, dealer_sub_code, oem_id, city_id}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("FORACID", rs.getString("FORACID"));
                map.put("BANK_NAME", rs.getString("BANK_NAME"));
                map.put("IFSC", rs.getString("IFSC"));
                map.put("VALUE", rs.getString("FORACID") + "|" + rs.getString("IFSC") + "|" + rs.getString("BANK_NAME"));
                return map;
            });
        } catch (Exception e) {
            return null; // Return an empty map if no result is found
        }
    }

    public Map<String, String> getEmpProgramforApp(Long appid) {
        String sql = "SELECT employment_type, loan_program " +
                "FROM VEHICLE_LOAN_EMPLOYMENT e  " +
                "JOIN VEHICLE_LOAN_PROGRAM l ON l.applicant_id = e.applicant_id " +
                "WHERE e.del_flg = 'N' AND l.del_flg = 'N'   AND e.applicant_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{appid}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("employment_type", rs.getString("employment_type"));
                map.put("loan_program", rs.getString("loan_program"));
                return map;
            });
        } catch (EmptyResultDataAccessException e) {
            return null; // Return an empty map if no result is found
        }
    }

    public VehicleEmpProgram getEmpProgramForApplicant(Long appid) {
        String sql = "SELECT employment_type, loan_program, a.applicant_id, applicant_type, sib_customer, resident_flg , appl_name ,'' bureau_score,avg_sal,abb,dep_amt,to_char(a.appl_dob,'yyyy-mm-dd') appl_dob " +
                "FROM VEHICLE_LOAN_applicants a " +
                "JOIN VEHICLE_LOAN_EMPLOYMENT e ON a.applicant_id = e.applicant_id " +
                "JOIN VEHICLE_LOAN_PROGRAM l ON l.applicant_id = e.applicant_id " +
                "WHERE e.del_flg = 'N' AND l.del_flg = 'N'  and a.del_flg='N' AND a.applicant_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{appid}, new VehicleEmpProgramRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public LoanVehicleDto getLoanAndVehicle(Long slno) {
        String sql = "select MAKE_NAME,MODEL_NAME,l.VEHICLE_AMT,TENOR,LOAN_AMT,l.INS_AMT,l.INS_TYPE,l.INS_VAL from VEHICLE_LOAN_VEHICLE v join VEHICLE_LOAN_DETAILS l on l.SLNO=v.SLNO where v.DEL_FLG='N' and l.DEL_FLG='N' and l.SLNO=?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{slno}, new LoanVehicleDtoRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            throw new ValidationException(ValidationError.COM001, "Kindly Complete Vehicle & Loan Details");
        }
    }

    public LoanVehicleDto getLoanAndVehicle(Long slno, Boolean validator) {
        String sql = "select MAKE_NAME,MODEL_NAME,l.VEHICLE_AMT,TENOR,LOAN_AMT from VEHICLE_LOAN_VEHICLE v join VEHICLE_LOAN_DETAILS l on l.SLNO=v.SLNO where v.DEL_FLG='N' and l.DEL_FLG='N' and l.SLNO=?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{slno}, new LoanVehicleDtoRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            if (validator)
                throw new ValidationException(ValidationError.COM001, "Kindly Complete Vehicle & Loan Details");
            else
                return null;
        }
    }

    public RepayAcctDTO getRepaymentAcctDetails(Long slno) {
        String sql = "select l.bank_name,l.bank_id,ACCOUNT_NUMBER,IFSC_CODE,BORROWER_NAME from VEHICLE_LOAN_REPAYMENT v join NPCILIVEBANKDTLS@enach l on l.bank_id=v.BANK_NAME where v.DEL_FLG='N' and (l.NETBANK_FLAG='Active' or l.debitcard_flag='Active')  and v.SLNO=?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{slno}, new RepayAcctDtoRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            throw new ValidationException(ValidationError.COM001, "Kindly Complete Repayment account Details");
        }
    }

    public RepayAcctDTO getRepaymentAcctDetails(Long slno, Boolean validation) {
        String sql = "select l.bank_name,l.bank_id,ACCOUNT_NUMBER,IFSC_CODE,BORROWER_NAME from VEHICLE_LOAN_REPAYMENT v join NPCILIVEBANKDTLS@enach l on l.bank_id=v.BANK_NAME where v.DEL_FLG='N' and (l.NETBANK_FLAG='Active' or l.debitcard_flag='Active')  and v.SLNO=?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{slno}, new RepayAcctDtoRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            if (!validation) {
                return null;
            } else
                throw new ValidationException(ValidationError.COM001, "Kindly Complete Repayment account Details");
        }
    }

    private static class VehicleEmpProgramRowMapper implements RowMapper<VehicleEmpProgram> {
        @Override
        public VehicleEmpProgram mapRow(ResultSet rs, int rowNum) throws SQLException {
            VehicleEmpProgram dto = null;
            dto = new VehicleEmpProgram();
            dto.setEmploymentType(rs.getString("employment_type"));
            dto.setLoanProgram(rs.getString("loan_program"));
            dto.setApplicantId(rs.getString("applicant_id"));
            dto.setApplicantType(rs.getString("applicant_type"));
            dto.setSibCustomer(rs.getString("sib_customer"));
            dto.setApplName(rs.getString("appl_name"));
            dto.setResidentFlg(rs.getString("resident_flg"));
            dto.setBureauScore(rs.getString("BUREAU_SCORE"));
            dto.setAvgSal(rs.getString("avg_sal"));
            dto.setAbb(rs.getString("abb"));
            dto.setDepAmt(rs.getString("dep_amt"));
            dto.setApplDob(rs.getString("appl_dob"));
            return dto;
        }
    }

    private static class LoanVehicleDtoRowMapper implements RowMapper<LoanVehicleDto> {
        @Override
        public LoanVehicleDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            LoanVehicleDto dto = new LoanVehicleDto();
            dto.setTenor(rs.getString("TENOR"));
            dto.setVehicleAmt(rs.getString("VEHICLE_AMT"));
            dto.setModelName(rs.getString("MODEL_NAME"));
            dto.setMakeName(rs.getString("MAKE_NAME"));
            dto.setLoanAmt(rs.getString("LOAN_AMT"));
            dto.setInsVal(rs.getString("INS_VAL"));
            dto.setInsType(rs.getString("INS_TYPE"));
            dto.setInsAmt(rs.getString("INS_AMT"));
            return dto;
        }
    }

    private static class SanctionDetailsDtoRowMapper implements RowMapper<SanctionDetailsDTO> {
        @Override
        public SanctionDetailsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            SanctionDetailsDTO dto = new SanctionDetailsDTO();
            dto.setSancAmountRecommended(rs.getString("SANC_AMOUNT_RECOMMENDED"));
            dto.setSancCardRate(rs.getString("SANC_CARD_RATE"));
            dto.setSancEmi(rs.getString("SANC_EMI"));
            dto.setSancTenor(rs.getString("SANC_TENOR"));
            dto.setLtvAmount(rs.getString("LTV_AMT"));
            dto.setLtvPercentage(rs.getString("LTV_PER"));
            dto.setLoanAmt(rs.getString("LOAN_AMT"));
            dto.setEligibleLoanAmt(rs.getString("ELIGIBLE_LOAN_AMT"));
            return dto;
        }
    }

    public Map<String, String> getInstalmentDates(Long slno) {
        try {
            String sql = "SELECT TO_CHAR(ACC_OPEN_DATE, 'DD-MM-YYYY') AS accOpenDt, ACC_OPENED AS acOpenFlg FROM VEHICLE_LOAN_MASTER WHERE SLNO = :slno";
            MapSqlParameterSource params = new MapSqlParameterSource().addValue("slno", slno);
            Map<String, Object> resultVal = namedParameterJdbcTemplate.queryForMap(sql, params);
            String accOpenDt = (String) resultVal.get("accOpenDt");
            String ac_open_flg = (String) resultVal.get("acOpenFlg");

            if (accOpenDt == null) {
                throw new ValidationException(ValidationError.COM001, "Account opening is not completed. Please complete the account opening process.");
            }

            // Get the instalment start date
            String instalmentStartDate = next5th();

            // Fetch the sanction tenor
            SanctionDetailsDTO sanctionDetails = fetchSanctionDetailsFinal(slno);
            int sanctionTenor = Integer.parseInt(sanctionDetails.getSancTenor());
            String emi = sanctionDetails.getSancEmi();
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


            // Calculate the instalment end date
            LocalDate startDate = LocalDate.parse(instalmentStartDate, inputFormatter);
            LocalDate endDate = startDate.plusMonths(sanctionTenor).minusDays(1);

            String formattedStartDate = startDate.format(outputFormatter);
            String formattedEndDate = endDate.format(outputFormatter);


//            String instalmentEndDate = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            Map<String, String> result = new HashMap<>();
            result.put("instalmentStartDate", formattedStartDate);
            result.put("instalmentEndDate", formattedEndDate);
            result.put("tenor", sanctionDetails.getSancTenor());
            result.put("emi", emi);
            result.put("ac_open_flg", ac_open_flg);

            return result;
        } catch (EmptyResultDataAccessException ex) {
            throw new ValidationException(ValidationError.COM001, "Unable to fetch instalment dates. Please ensure all required data is available.");
        }
    }

    public List<Map<String, String>> getSubquePendingInfo(String wiNum) {
        if (wiNum == null || wiNum.isEmpty()) {
            return Collections.emptyList();
        }

        String sql = """
                    WITH LatestTasks AS (
                    SELECT TASK_TYPE, MAX(TASK_ID) as LATEST_TASK_ID
                    FROM VEHICLE_LOAN_SUBQUEUE_TASK
                    WHERE WI_NUM = :wiNum
                    AND TASK_TYPE IN ('CHARGE_WAIVER', 'ROI_WAIVER')
                    GROUP BY TASK_TYPE
                ),
                ChargeWaiverLevels AS (
                    SELECT DISTINCT level_name, 'Forward to L'||SUBSTR(LEVEL_NAME,3) AS DISPLAY_NAME
                    FROM vlchargewaiverlevel@mybank
                    WHERE del_flag != 'Y'
                ),
                RoiWaiverLevels AS (
                    SELECT DISTINCT level_name, 'Forward to L'||SUBSTR(LEVEL_NAME,3) AS DISPLAY_NAME
                    FROM vlroiwaiverlevel@mybank
                    WHERE del_flag != 'Y'
                )
                SELECT
                    sq.LOCK_FLG,
                    sq.LOCKED_ON,
                    sq.LOCKED_BY,
                    sq.TASK_TYPE,
                    sq.STATUS,
                    sq.DECISION,
                    COALESCE(
                        CASE
                            WHEN sq.TASK_TYPE = 'CHARGE_WAIVER' THEN cwl.DISPLAY_NAME
                            WHEN sq.TASK_TYPE = 'ROI_WAIVER' THEN rwl.DISPLAY_NAME
                        END,
                        sq.DECISION
                    ) as DECISION_DESC,
                    sq.REMARKS,
                    sq.COMPLETED_DATE,
                    sq.COMPLETED_USER
                FROM VEHICLE_LOAN_SUBQUEUE_TASK sq
                INNER JOIN LatestTasks lt
                    ON sq.TASK_TYPE = lt.TASK_TYPE
                    AND sq.TASK_ID = lt.LATEST_TASK_ID
                LEFT JOIN ChargeWaiverLevels cwl
                    ON sq.TASK_TYPE = 'CHARGE_WAIVER'
                    AND cwl.level_name = sq.DECISION
                LEFT JOIN RoiWaiverLevels rwl
                    ON sq.TASK_TYPE = 'ROI_WAIVER'
                    AND rwl.level_name = sq.DECISION
                WHERE sq.WI_NUM = :wiNum
                ORDER BY sq.
                    CREATED_DATE DESC
                """;

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("wiNum", wiNum);

        try {
            List<Map<String, Object>> resultList = namedParameterJdbcTemplate.queryForList(sql, params);

            if (resultList.isEmpty()) {
                return Collections.emptyList();
            }

            List<Map<String, String>> result = new ArrayList<>();
            SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            SimpleDateFormat outputFormatter = new SimpleDateFormat("dd MMM yyyy, HH:mm a");

            for (Map<String, Object> row : resultList) {
                Map<String, String> stringMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if ((key.equals("LOCKED_ON") || key.equals("COMPLETED_DATE"))
                            && value != null && !value.toString().isEmpty()) {
                        try {
                            Date date = inputFormatter.parse(value.toString());
                            stringMap.put(key, outputFormatter.format(date));
                        } catch (ParseException e) {
                            stringMap.put(key, value != null ? value.toString() : "");
                        }
                    } else {
                        stringMap.put(key, value != null ? value.toString() : "");
                    }
                }
                result.add(stringMap);
            }

            return result;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }


    public List<Map<String, String>> getSubquePendingInfo1(String wiNum) {
        if (wiNum == null || wiNum.isEmpty()) {
            return Collections.emptyList();
        }

        String sql = "SELECT LOCK_FLG, LOCKED_ON, LOCKED_BY, TASK_TYPE, STATUS, DECISION, REMARKS " +
                "FROM VEHICLE_LOAN_SUBQUEUE_TASK " +
                "WHERE STATUS = 'PENDING' AND WI_NUM = :wiNum " +
                "AND TASK_TYPE IN ('CHARGE_WAIVER', 'ROI_WAIVER') " +
                "ORDER BY CREATED_DATE DESC";

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("wiNum", wiNum);

        try {
            List<Map<String, Object>> resultList = namedParameterJdbcTemplate.queryForList(sql, params);

            if (resultList.isEmpty()) {
                return Collections.emptyList();
            }

            List<Map<String, String>> result = new ArrayList<>();
            for (Map<String, Object> row : resultList) {
                Map<String, String> stringMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    stringMap.put(key, (value != null) ? value.toString() : "");
                }

                // Handle locked_on separately due to date formatting
                String lockedOn = stringMap.get("LOCKED_ON");
                if (lockedOn != null && !lockedOn.isEmpty()) {
                    try {
                        SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
                        SimpleDateFormat outputFormatter = new SimpleDateFormat("dd MMM yyyy, HH:mm a");
                        Date date = inputFormatter.parse(lockedOn);
                        stringMap.put("LOCKED_ON", outputFormatter.format(date));
                    } catch (ParseException e) {
                        // Use original string if parsing fails
                    }
                }

                result.add(stringMap);
            }

            return result;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    public Map<String, String> getDisbursementDetails(String wiNum) {
        if (wiNum == null || wiNum.isEmpty()) {
            return Collections.emptyMap();
        }

        String sql = """
                    SELECT 
                        DISBFLAG,
                        DISBDATE,
                        NEFTCMDATEDEALER,
                        UTRNODEALER,
                        NEFTFLAGDEALER,
                        NEFTAMOUNTDEALER,
                        DEALERACCNUM,
                        DEALERIFSC,
                        DEALERNAME
                    FROM VEHICLE_LOAN_ACCOUNT
                    WHERE WI_NUM = :wiNum
                    AND DELFLAG = 'N'
                """;

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("wiNum", wiNum);

        try {
            Map<String, Object> result = namedParameterJdbcTemplate.queryForMap(sql, params);
            Map<String, String> stringMap = new HashMap<>();

            for (Map.Entry<String, Object> entry : result.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof Date) {
                    SimpleDateFormat outputFormatter = new SimpleDateFormat("dd MMM yyyy, HH:mm a");
                    stringMap.put(key, outputFormatter.format((Date) value));
                } else if (value instanceof BigDecimal) {
                    stringMap.put(key, CurrencyFormatter.formatCurrency((BigDecimal) value));
                } else {
                    stringMap.put(key, (value != null) ? value.toString() : "");
                }
            }

            return stringMap;
        } catch (DataAccessException e) {
            return Collections.emptyMap();
        }
    }

    public List<Map<String, String>> getSubqueInfo(String wiNum) {
        if (wiNum == null || wiNum.isEmpty()) {
            return Collections.emptyList();
        }

        String sql = """
                            WITH LatestTasks AS (
                    SELECT TASK_TYPE, MAX(TASK_ID) as LATEST_TASK_ID
                    FROM VEHICLE_LOAN_SUBQUEUE_TASK
                    WHERE WI_NUM = :wiNum
                    AND TASK_TYPE IN ('CHARGE_WAIVER', 'ROI_WAIVER')
                    GROUP BY TASK_TYPE
                ),
                ChargeWaiverLevels AS (
                    SELECT level_name, 'Forward to L'||SUBSTR(LEVEL_NAME,3) AS DISPLAY_NAME\s
                    FROM vlchargewaiverlevel@mybank\s
                    WHERE del_flag != 'Y'
                ),
                RoiWaiverLevels AS (
                    SELECT level_name, 'Forward to L'||SUBSTR(LEVEL_NAME,3) AS DISPLAY_NAME\s
                    FROM vlroiwaiverlevel@mybank\s
                    WHERE del_flag != 'Y'
                )
                SELECT\s
                    sq.LOCK_FLG,
                    sq.LOCKED_ON,
                    sq.LOCKED_BY,
                    sq.TASK_TYPE,
                    sq.STATUS,
                    sq.DECISION,
                    CASE\s
                        WHEN sq.TASK_TYPE = 'CHARGE_WAIVER' THEN\s
                            COALESCE((SELECT cwl.DISPLAY_NAME\s
                                      FROM ChargeWaiverLevels cwl\s
                                      WHERE cwl.level_name = sq.DECISION), sq.DECISION)
                        WHEN sq.TASK_TYPE = 'ROI_WAIVER' THEN\s
                            COALESCE((SELECT rwl.DISPLAY_NAME\s
                                      FROM RoiWaiverLevels rwl\s
                                      WHERE rwl.level_name = sq.DECISION), sq.DECISION)
                        ELSE sq.DECISION
                    END as DECISION_DESC,
                    sq.REMARKS,
                    sq.COMPLETED_DATE,
                    sq.COMPLETED_USER
                FROM VEHICLE_LOAN_SUBQUEUE_TASK sq
                INNER JOIN LatestTasks lt
                    ON sq.TASK_TYPE = lt.TASK_TYPE
                    AND sq.TASK_ID = lt.LATEST_TASK_ID
                WHERE sq.WI_NUM = :wiNum
                ORDER BY sq.CREATED_DATE DESC

                            """;

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("wiNum", wiNum);

        try {
            List<Map<String, Object>> resultList = namedParameterJdbcTemplate.queryForList(sql, params);

            if (resultList.isEmpty()) {
                return Collections.emptyList();
            }

            List<Map<String, String>> result = new ArrayList<>();
            for (Map<String, Object> row : resultList) {
                Map<String, String> stringMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    stringMap.put(key, (value != null) ? value.toString() : "");
                }

                // Handle locked_on separately due to date formatting
                String lockedOn = stringMap.get("LOCKED_ON");
                if (lockedOn != null && !lockedOn.isEmpty()) {
                    try {
                        SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
                        SimpleDateFormat outputFormatter = new SimpleDateFormat("dd MMM yyyy, HH:mm a");
                        Date date = inputFormatter.parse(lockedOn);
                        stringMap.put("LOCKED_ON", outputFormatter.format(date));
                    } catch (ParseException e) {
                        // Use original string if parsing fails
                    }
                }

                result.add(stringMap);
            }

            return result;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    public List<Map<String, String>> getCIFCreationDetails(String wiNum) {
        if (wiNum == null || wiNum.isEmpty()) {
            return Collections.emptyList();
        }

        String sql = """
                    WITH LatestTasks AS (
                        SELECT APPLICANT_ID, TASK_ID as LATEST_TASK_ID
                        FROM VEHICLE_LOAN_SUBQUEUE_TASK
                        WHERE WI_NUM = :wiNum
                        AND TASK_TYPE = 'CIF_CREATION'
                    )
                    SELECT
                        sq.LOCK_FLG, sq.LOCKED_ON, sq.LOCKED_BY, sq.TASK_TYPE,
                        sq.STATUS, sq.DECISION, sq.REMARKS, sq.COMPLETED_USER,
                        sq.COMPLETED_DATE,
                        c.CIFID, c.CIFDATE, c.CIFUSER, c.CIFFLAG,
                        c.CKYCDATE, c.CKYCUSER, c.CKYCFLAG,
                        c.BLDATE, c.BLUSER, c.BLFLAG,
                        c.DECISION as CIF_DECISION,
                        c.DECISIONUSER as CIF_DECISION_USER,
                        c.DECISIONDATE as CIF_DECISION_DATE,
                        c.WORKITEMNUMBER as CPC_WORKITEM,
                        sq.APPLICANT_ID
                    FROM VEHICLE_LOAN_SUBQUEUE_TASK sq
                    INNER JOIN LatestTasks lt
                        ON sq.APPLICANT_ID = lt.APPLICANT_ID
                        AND sq.TASK_ID = lt.LATEST_TASK_ID
                    LEFT JOIN VEHICLE_LOAN_CIF c
                        ON sq.WI_NUM = c.WI_NUM
                        AND sq.APPLICANT_ID = c.APPLICANT_ID
                        AND sq.TASK_ID = c.TASK_ID
                        AND c.DELFLAG = 'N'
                    ORDER BY sq.APPLICANT_ID
                """;

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("wiNum", wiNum);

        try {
            List<Map<String, Object>> resultList = namedParameterJdbcTemplate.queryForList(sql, params);
            log.info("Result details {}", resultList.size());

            if (resultList.isEmpty()) {
                return Collections.emptyList();
            }

            List<Map<String, String>> result = new ArrayList<>();
            for (Map<String, Object> row : resultList) {
                Map<String, String> stringMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    stringMap.put(key, (value != null) ? value.toString() : "");
                }

                // Format dates
                String[] dateFields = {"LOCKED_ON", "COMPLETED_DATE", "CIFDATE", "CKYCDATE",
                        "BLDATE", "CIF_DECISION_DATE"};

                for (String dateField : dateFields) {
                    String dateStr = stringMap.get(dateField);
                    if (dateStr != null && !dateStr.isEmpty()) {
                        try {
                            SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
                            SimpleDateFormat outputFormatter = new SimpleDateFormat("dd MMM yyyy, HH:mm a");
                            Date date = inputFormatter.parse(dateStr);
                            stringMap.put(dateField, outputFormatter.format(date));
                        } catch (ParseException e) {
                            // Use original string if parsing fails
                        }
                    }
                }

                result.add(stringMap);
            }

            return result;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }


    private static class RepayAcctDtoRowMapper implements RowMapper<RepayAcctDTO> {
        @Override
        public RepayAcctDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            RepayAcctDTO dto = new RepayAcctDTO();
            dto.setBankName(rs.getString("BANK_NAME"));
            dto.setBankId(rs.getString("BANK_ID"));
            dto.setAccountNumber(rs.getString("ACCOUNT_NUMBER"));
            dto.setIfscCode(rs.getString("IFSC_CODE"));
            dto.setBorrowerName(rs.getString("BORROWER_NAME"));
            return dto;
        }
    }

    public List<Map<String, Object>> findAllActiveDeviationLevel() {
        String sql = " select distinct level_name,'Forward to L'||SUBSTR(LEVEL_NAME,4)AS DISPLAY_NAME from vlrbcpccheckerlevel@mybank where del_flag !='Y' ";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> findGroupedPPCsByLevel() {
        String sql = """
                    SELECT DISTINCT cl.LEVEL_NAME, 
                        'Level ' || SUBSTR(cl.LEVEL_NAME, 4) as DISPLAY_NAME,
                        cl.PPC_NO,
                        p.PPC_NAME
                    FROM vlrbcpccheckerlevel@mybank cl
                    JOIN mishrp@mybank p ON cl.PPC_NO = p.PPCNO
                    WHERE cl.DEL_FLAG != 'Y'
                    ORDER BY cl.LEVEL_NAME, p.PPC_NAME
                """;

        List<Map<String, Object>> rawData = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> groupedData = new ArrayList<>();

        String currentLevel = null;
        Map<String, Object> currentGroup = null;
        List<Map<String, Object>> currentChildren = null;

        for (Map<String, Object> row : rawData) {
            String levelName = (String) row.get("LEVEL_NAME");

            if (!levelName.equals(currentLevel)) {
                if (currentGroup != null) {
                    currentGroup.put("children", currentChildren);
                    groupedData.add(currentGroup);
                }

                currentLevel = levelName;
                currentGroup = new HashMap<>();
                currentGroup.put("id", levelName);
                currentGroup.put("text", row.get("DISPLAY_NAME"));
                currentChildren = new ArrayList<>();
            }

            Map<String, Object> child = new HashMap<>();
            child.put("id", row.get("PPC_NO"));
            child.put("text", row.get("PPC_NAME"));
            currentChildren.add(child);
        }

        // Add the last group
        if (currentGroup != null) {
            currentGroup.put("children", currentChildren);
            groupedData.add(currentGroup);
        }

        return groupedData;
    }

    public List<Map<String, Object>> getGroupedPPCsByLevel(String levelName) {
    String sql = """
        SELECT DISTINCT cl.LEVEL_NAME,
            'Level ' || SUBSTR(cl.LEVEL_NAME, 4) as DISPLAY_NAME,
            cl.PPC_NO,
            p.PPC_NAME
        FROM vlrbcpccheckerlevel@mybank cl
        JOIN mishrp@mybank p ON cl.PPC_NO = p.PPCNO
        WHERE cl.DEL_FLAG != 'Y' and cl.LEVEL_NAME = ?
        ORDER BY cl.LEVEL_NAME, p.PPC_NAME
    """;

    // Pass the levelName parameter to the query
    List<Map<String, Object>> rawData = jdbcTemplate.queryForList(sql, levelName);

    List<Map<String, Object>> groupedData = new ArrayList<>();
    String currentLevel = null;
    Map<String, Object> currentGroup = null;
    List<Map<String, Object>> currentChildren = null;

    for (Map<String, Object> row : rawData) {
        String rclevelName = (String) row.get("LEVEL_NAME");
        if (!rclevelName.equals(currentLevel)) {
            if (currentGroup != null) {
                currentGroup.put("children", currentChildren);
                groupedData.add(currentGroup);
            }
            currentLevel = rclevelName;
            currentGroup = new HashMap<>();
            currentGroup.put("id", rclevelName);
            currentGroup.put("text", row.get("DISPLAY_NAME"));
            currentChildren = new ArrayList<>();
        }

        Map<String, Object> child = new HashMap<>();
        child.put("id", row.get("PPC_NO"));
        child.put("text", row.get("PPC_NAME"));
        currentChildren.add(child);
    }

    // Add the last group
    if (currentGroup != null) {
        currentGroup.put("children", currentChildren);
        groupedData.add(currentGroup);
    }

    return groupedData;
}


    public List<Map<String, Object>> getGroupedPPCsByLevelold(String levelName) {
        String sql = """
                    SELECT DISTINCT cl.LEVEL_NAME, 
                        'Level ' || SUBSTR(cl.LEVEL_NAME, 4) as DISPLAY_NAME,
                        cl.PPC_NO,
                        p.PPC_NAME
                    FROM vlrbcpccheckerlevel@mybank cl
                    JOIN mishrp@mybank p ON cl.PPC_NO = p.PPCNO
                    WHERE cl.DEL_FLAG != 'Y' and cl.LEVEL_NAME = ? 
                    ORDER BY cl.LEVEL_NAME, p.PPC_NAME
                """;

        List<Map<String, Object>> rawData = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> groupedData = new ArrayList<>();

        String currentLevel = null;
        Map<String, Object> currentGroup = null;
        List<Map<String, Object>> currentChildren = null;

        for (Map<String, Object> row : rawData) {
            String rclevelName = (String) row.get("LEVEL_NAME");

            if (!rclevelName.equals(currentLevel)) {
                if (currentGroup != null) {
                    currentGroup.put("children", currentChildren);
                    groupedData.add(currentGroup);
                }

                currentLevel = levelName;
                currentGroup = new HashMap<>();
                currentGroup.put("id", levelName);
                currentGroup.put("text", row.get("DISPLAY_NAME"));
                currentChildren = new ArrayList<>();
            }

            Map<String, Object> child = new HashMap<>();
            child.put("id", row.get("PPC_NO"));
            child.put("text", row.get("PPC_NAME"));
            currentChildren.add(child);
        }

        // Add the last group
        if (currentGroup != null) {
            currentGroup.put("children", currentChildren);
            groupedData.add(currentGroup);
        }

        return groupedData;
    }

    public List<Map<String, Object>> findRoiLevel() {
        String sql = "select distinct level_name,'Forward to L'||SUBSTR(LEVEL_NAME,3)AS DISPLAY_NAME from vlroiwaiverlevel@mybank where del_flag !='Y' ";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> findProceessLevel() {
        String sql = "select distinct level_name,'Forward to L'||SUBSTR(LEVEL_NAME,3)AS DISPLAY_NAME from vlchargewaiverlevel@mybank where del_flag !='Y'";
        return jdbcTemplate.queryForList(sql);
    }


    public List<WaiverDto.ProcessFeeWaiverDto> fetchProccessingFee(String slNo) {
        return jdbcTemplate.query(
                "select c.CHARGE_CODE,c.CHARGE_NAME,c.STATIC_FIXED,c.PERCANTAGE,LOAN_AMOUNT_PERCANTAGE,VEHICLE_PRICE_PERCANTAGE,OTHERS_PERCANTAGE,VALUE,MAXIMUM_LIMIT,MAXIMUM_VALUE,FEE_VALUE_REC,DECISION,FEE_REMARKS,c.WAIVER,fee_sanc_value, c.FREQUENCY,w.fee_value from vlchargemas@mybank c join vlcharge@mybank m on m.del_flag='N' and m.CHARGE_CODE=c.CHARGE_CODE AND m.PROGRAM_NAME=c.PROGRAM_NAME   LEFT OUTER JOIN VEHICLE_LOAN_CHARGE_WAIVER  w on  c.CHARGE_CODE=w.FEE_CODE AND w.SLNO=? and w.del_flag='N'   WHERE  c.DEL_FLAG='N' and m.DEL_FLAG='N'  AND c.PROGRAM_NAME IN (select DISTINCT LOAN_PROGRAM from VEHICLE_LOAN_PROGRAM where DEL_FLG='N' and LOAN_PROGRAM<>'NONE' AND SLNO=?)",
                new Object[]{slNo, slNo},
                new RowMapper<WaiverDto.ProcessFeeWaiverDto>() {
                    @Override
                    public WaiverDto.ProcessFeeWaiverDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                        WaiverDto.ProcessFeeWaiverDto feeWaiverDto = new WaiverDto.ProcessFeeWaiverDto();
                        feeWaiverDto.setChargeCode(rs.getString("CHARGE_CODE"));
                        feeWaiverDto.setChargeName(rs.getString("CHARGE_NAME"));
                        feeWaiverDto.setStaticFixed(rs.getString("STATIC_FIXED"));
                        feeWaiverDto.setPercantage(rs.getString("PERCANTAGE"));
                        feeWaiverDto.setLoanAmountPercantage(rs.getString("LOAN_AMOUNT_PERCANTAGE"));
                        feeWaiverDto.setVehiclePricePercantage(rs.getString("VEHICLE_PRICE_PERCANTAGE"));
                        feeWaiverDto.setOthersPercantage(rs.getString("OTHERS_PERCANTAGE"));
                        feeWaiverDto.setValue(rs.getBigDecimal("VALUE"));
                        feeWaiverDto.setMaximumLimit(rs.getString("MAXIMUM_LIMIT"));
                        feeWaiverDto.setMaximumValue(rs.getBigDecimal("MAXIMUM_VALUE"));
                        feeWaiverDto.setChargeName(rs.getString("CHARGE_NAME"));
                        feeWaiverDto.setFeewaiverRemarks(rs.getString("FEE_REMARKS"));
                        feeWaiverDto.setFeeWaive(rs.getString("FEE_VALUE_REC"));
                        feeWaiverDto.setFeeValue(rs.getString("FEE_VALUE"));
                        feeWaiverDto.setDecision(rs.getString("DECISION"));
                        feeWaiverDto.setWaiver(rs.getString("WAIVER"));
                        feeWaiverDto.setFeeSancValue(rs.getString("FEE_SANC_VALUE"));
                        feeWaiverDto.setFrequency(rs.getString("FREQUENCY"));
                        return feeWaiverDto;
                    }
                }
        );
    }

    public SanctionDetailsDTO fetchSanctionDetailsFinal(Long slno) {
        String sql = "select SANC_AMOUNT_RECOMMENDED,SANC_CARD_RATE,SANC_EMI,SANC_TENOR,LTV_AMT,LTV_PER,LOAN_AMT,ELIGIBLE_LOAN_AMT from VEHICLE_LOAN_ELIGIBILITY where DEL_FLG='N' AND SLNO=?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{slno}, new SanctionDetailsDtoRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            throw new ValidationException(ValidationError.COM001, "Kindly Complete Vehcile & Loan Details");
        }
    }

    public SanctionDetailsDTO fetchSanctionDetailsFinal(Long slno, Boolean validator) {
        String sql = "select SANC_AMOUNT_RECOMMENDED,SANC_CARD_RATE,SANC_EMI,SANC_TENOR,LTV_AMT,LTV_PER,LOAN_AMT,ELIGIBLE_LOAN_AMT from VEHICLE_LOAN_ELIGIBILITY where DEL_FLG='N' AND SLNO=?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{slno}, new SanctionDetailsDtoRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            if (validator)
                throw new ValidationException(ValidationError.COM001, "Kindly Complete Vehcile & Loan Details");
            else
                return null;
        }
    }

    public SanctionDetailsDTO fetchFinalSanctionDetails(Long slno) {
        String sql = "SELECT SANC_AMOUNT_RECOMMENDED, SANC_CARD_RATE, SANC_EMI, SANC_TENOR, LTV_AMT, LTV_PER, LOAN_AMT, ELIGIBLE_LOAN_AMT " +
                "FROM VEHICLE_LOAN_ELIGIBILITY WHERE DEL_FLG='N' AND SLNO=?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{slno}, new SanctionDetailsDtoRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return new SanctionDetailsDTO(); // Return an empty DTO instead of null
        }
    }


    public Optional<Integer> getBureauDays() {
        String sql = "select days from VLBUREAUCHECKMAS@mybank where sysdate between from_date and to_date and del_flag ='N'";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, Integer.class));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }


    public VkycSlnoAppidDto VkycGetSlnoAndAppid(String data) {
        try {
            return jdbcTemplate.queryForObject(
                    "select S2.APPID,S2.SLNO from VEHICLE_LOAN_SUBQUEUE_TASK T JOIN SMSEMAIL S2 on T.WI_NUM = S2.WI_NUM AND T.APPLICANT_ID=S2.APPID AND REQ_TYPE='S'\n" +
                            "and TASK_TYPE='VKYC' AND STATUS not in ('COMPLETED') AND HASH_CODE=? AND SENT_DATE<SYSDATE+3  ORDER BY T.TASK_ID FETCH FIRST ROW ONLY",
                    new Object[]{data},
                    new RowMapper<VkycSlnoAppidDto>() {
                        @Override
                        public VkycSlnoAppidDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                            VkycSlnoAppidDto slnoappid = new VkycSlnoAppidDto();
                            slnoappid.setAppid(rs.getObject("APPID") != null ? (long) rs.getInt("APPID") : null);
                            slnoappid.setSlno(rs.getObject("SLNO") != null ? (long) rs.getInt("SLNO") : null);
                            return slnoappid;
                        }
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            // If no records are found, return a DTO with null fields
            VkycSlnoAppidDto slnoappid = new VkycSlnoAppidDto();
            slnoappid.setAppid(null);
            slnoappid.setSlno(null);
            return slnoappid;
        }

    }


    public List<LegalityFetchDTO> findAllLegalityData(Long slNo) {
        String QUERY = "select APPLICANT_NAME,A.APPLICANT_ID,APPLICANT_TYPE,MOBILE_CNTRY_CODE,MOBILE_NO,EMAIL_ID,TO_CHAR(APPLICANT_DOB,'YYYY-MM-DD') APPLICANT_DOB ,PAN_NO,ADDR1 || ',' || ADDR2 || ',' || ADDR3 || ',' ||STATE_DESC || ',' ||COUNTRY_DESC  as ADDR1,PIN,COM_ADDR1 || ',' || COM_ADDR2 || ',' ||COM_ADDR3 || ',' ||COM_STATE_DESC ||',' || COM_COUNTRY_DESC  as COM_ADDR1,COM_PIN,RESIDENT_FLG,STATE,STATE_DESC,COUNTRY,COM_COUNTRY  from VEHICLE_LOAN_APPLICANTS A JOIN VEHICLE_LOAN_BASIC B on A.APPLICANT_ID = B.APPLICANT_ID JOIN VEHICLE_LOAN_KYC K ON A.APPLICANT_ID = K.APPLICANT_ID   where a.DEL_FLG='N' and B.DEL_FLG='N' and A.SLNO=? order by A.APPLICANT_ID,APPLICANT_TYPE ASC";

        return jdbcTemplate.query(QUERY, new Object[]{slNo}, new VehicleLoanApplicantRowMapper());
    }

    private static class VehicleLoanApplicantRowMapper implements RowMapper<LegalityFetchDTO> {

        @Override
        public LegalityFetchDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            LegalityFetchDTO applicant = new LegalityFetchDTO();
            applicant.setApplicantName(rs.getString("APPLICANT_NAME"));
            applicant.setApplicantId(rs.getString("APPLICANT_ID"));
            applicant.setApplicantType(rs.getString("APPLICANT_TYPE"));
            applicant.setMobileCountryCode(rs.getString("MOBILE_CNTRY_CODE"));
            applicant.setMobileNo(rs.getString("MOBILE_NO"));
            applicant.setEmailId(rs.getString("EMAIL_ID"));
            applicant.setApplicantDob(rs.getString("APPLICANT_DOB"));
            applicant.setPanNo(rs.getString("PAN_NO"));
            applicant.setAddr1(rs.getString("ADDR1"));
            applicant.setPin(rs.getString("PIN"));
            applicant.setComAddr1(rs.getString("COM_ADDR1"));
            applicant.setComPin(rs.getString("COM_PIN"));
            applicant.setResidentFlag(rs.getString("RESIDENT_FLG"));
            applicant.setPerState(rs.getString("STATE"));
            applicant.setPerStateDesc(rs.getString("STATE_DESC"));
            applicant.setPerCountry(rs.getString("COUNTRY"));
            applicant.setComCountry(rs.getString("COM_COUNTRY"));

            return applicant;
        }
    }


    public Map<String, String> getBranchDetails(Long slno) {
        String sql = "SELECT BR_NAME, " +
                "BLDG_NAME || ',' || ROAD_NAME || ',' || POST_NAME || ',' || PIN_CODE AS BR_ADR, " +
                "TO_CHAR(SAN_DATE, 'YYYY-MM-DD') AS SAN_DATE,PIN_CODE " +
                "FROM misbmt@mybank b " +
                "JOIN VEHICLE_LOAN_MASTER M ON M.SOL_ID = b.sol_id " +
                "WHERE M.SLNO = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{slno}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("BR_NAME", rs.getString("BR_NAME").toUpperCase().trim());
                map.put("BR_ADR", rs.getString("BR_ADR"));
                map.put("SAN_DATE", rs.getString("SAN_DATE"));
                map.put("PIN_CODE", rs.getString("PIN_CODE"));
                return map;
            });
        } catch (EmptyResultDataAccessException e) {
            return new HashMap<>(); // Return an empty map if no result is found
        }
    }

    public Map<String, String> fetchStaff(Long slNo) {
        String sql = "SELECT NVL(AADHAR_NAME,PPC_NAME) as PPC_NAME, MOBNO, EMAILID,JOBROLE FROM hrmsempdtls@mybank h JOIN PRINOFF@MYBANK P on p.ppcno=h.ppcno WHERE prin_flag='Y' and trunc(sysdate) between start_date and end_date and SOL_ID in (SELECT SOL_ID from VEHICLE_LOAN_MASTER where SLNO=?)";
        //   String sql = "SELECT CASE WHEN AADHAR_NAME is null or trim(AADHAR_NAME) ='' then PPC_NAME else AADHAR_NAME end as PPC_NAME, MOBNO, EMAILID,JOBROLE FROM hrmsempdtls@mybank h JOIN PRINOFF@MYBANK P on p.ppcno=h.ppcno WHERE prin_flag='Y' and trunc(sysdate) between start_date and end_date and SOL_ID in (SELECT SOL_ID from VEHICLE_LOAN_MASTER where SLNO=?)";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{slNo}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("PPC_NAME", rs.getString("PPC_NAME").toUpperCase());
                map.put("MOBNO", rs.getString("MOBNO"));
                map.put("EMAILID", rs.getString("EMAILID"));
                return map;
            });
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationException(ValidationError.COM001, "Unable to Obtain Branch Head Details!");
        }
    }


    public List<ROIProcFeeDTO.ProcessFeeFinalDto> fetchProccessingFeeAcOpen(String slNo) {
        return jdbcTemplate.query(
                "select FEE_CODE,FEE_NAME,FEE_VALUE,FINAL_FEE from VEHICLE_LOAN_CHARGE_WAIVER where slno=? and DEL_FLAG='N'",
                new Object[]{slNo},
                new RowMapper<ROIProcFeeDTO.ProcessFeeFinalDto>() {
                    @Override
                    public ROIProcFeeDTO.ProcessFeeFinalDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                        ROIProcFeeDTO.ProcessFeeFinalDto feeFinalDto = new ROIProcFeeDTO.ProcessFeeFinalDto();
                        feeFinalDto.setFeeCode(rs.getString("FEE_CODE"));
                        feeFinalDto.setFeeName(rs.getString("FEE_NAME"));
                        feeFinalDto.setFeeInitialValue(rs.getString("FEE_VALUE"));
                        feeFinalDto.setFeeFinalValue(rs.getString("FINAL_FEE"));
                        return feeFinalDto;
                    }
                }
        );
    }

    public Map<String, Object> getNotifyData(String solID, String ppcno) {
        String sql = "SELECT LISTAGG(WI_NUM, ',') WITHIN GROUP (ORDER BY WI_NUM) AS WI_NUM_LIST " +
                "FROM VEHICLE_LOAN_MASTER " +
                "WHERE QUEUE = 'PD' AND SOL_ID = ? " +
                "AND  CMDATE > (SELECT NVL(MAX(CREATED_AT),SYSDATE-365) FROM EMPLOYEE_NOTIFICATIONS WHERE SEEN='1' AND PPCNO=?) " +
                "UNION ALL " +
                "SELECT TO_CHAR(COUNT(*)) AS WI_NUM_LIST " +
                "FROM VEHICLE_LOAN_MASTER " +
                "WHERE QUEUE = 'PD' AND SOL_ID = ?";

        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, new Object[]{solID, ppcno, solID});

        // Extract the results
        String wiNumList = (String) resultList.get(0).get("WI_NUM_LIST");
        String count = (String) resultList.get(1).get("WI_NUM_LIST");

        // Prepare the result to return
        Map<String, Object> result = new HashMap<>();
        result.put("wiNumList", wiNumList);
        result.put("count", count);

        return result;
    }


    public Map<String, String> fetchVlaccountmas(String program, String foirtype, String residentcust) {
        String sql = "SELECT SLNO,PROGRAM_NAME,RESIDENT_STATUS,FOIR_TYPE,GL_CODE,SCHM_CODE,SECTOR_CODE,SUB_SECTOR_CODE,OCCUPATION_CODE,BORROWER_CATEGORY,RISK_CATEGORY_CODE,MODE_ADVANCE,ADVANCE_TYPE,NATURE_ADVANCE,GUARANTEE_COVER_CODE,INDUSTRY_TYPE,BANKING_ARNGMT_CODE,DRI_SCHEME_CODE,AC_RELATION_CODE,NABARD_CODE,SSI_CODE,AGRI_CODE,OCCUPATION_SUB_CODE,EXTERNAL_RATING,SPL_CLS_CODE,OTH_CR_FCLTY,COM_REAL_EST_CODE,DEL_FLAG,CM_USER,CM_DATE,DEL_USER,DEL_DATE,ACCT_RATE_SCORE " +
                "FROM vlaccountmas@mybank  " +
                "WHERE program_name = ?   AND foir_type = ? AND resident_status=? and nvl(del_flag,'N')='N'";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{program, foirtype, residentcust}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("slno", rs.getString("slno"));
                map.put("program_name", rs.getString("program_name"));
                map.put("resident_status", rs.getString("resident_status"));
                map.put("foir_type", rs.getString("foir_type"));
                map.put("gl_code", rs.getString("gl_code"));
                map.put("schm_code", rs.getString("schm_code"));
                map.put("sector_code", rs.getString("sector_code"));
                map.put("sub_sector_code", rs.getString("sub_sector_code"));
                map.put("occupation_code", rs.getString("occupation_code"));
                map.put("borrower_category", rs.getString("borrower_category"));
                map.put("risk_category_code", rs.getString("risk_category_code"));
                map.put("mode_advance", rs.getString("mode_advance"));
                map.put("advance_type", rs.getString("advance_type"));
                map.put("nature_advance", rs.getString("nature_advance"));
                map.put("guarantee_cover_code", rs.getString("guarantee_cover_code"));
                map.put("industry_type", rs.getString("industry_type"));
                map.put("banking_arngmt_code", rs.getString("banking_arngmt_code"));
                map.put("dri_scheme_code", rs.getString("dri_scheme_code"));
                map.put("ac_relation_code", rs.getString("ac_relation_code"));
                map.put("nabard_code", rs.getString("nabard_code"));
                map.put("ssi_code", rs.getString("ssi_code"));
                map.put("agri_code", rs.getString("agri_code"));
                map.put("occupation_sub_code", rs.getString("occupation_sub_code"));
                map.put("external_rating", rs.getString("external_rating"));
                map.put("spl_cls_code", rs.getString("spl_cls_code"));
                map.put("oth_cr_fclty", rs.getString("oth_cr_fclty"));
                map.put("com_real_est_code", rs.getString("com_real_est_code"));
                map.put("del_flag", rs.getString("del_flag"));
                map.put("cm_user", rs.getString("cm_user"));
                map.put("cm_date", rs.getString("cm_date"));
                map.put("del_user", rs.getString("del_user"));
                map.put("del_date", rs.getString("del_date"));
                map.put("acct_rate_score", rs.getString("acct_rate_score"));
                return map;
            });
        } catch (EmptyResultDataAccessException e) {
            return null; // Return an empty map if no result is found
        }
    }

    public String next5th() {
        String sql = "select case when TO_NUMBER(to_char(sysdate,'DD'))<=4 then '05-'||to_char(sysdate,'MM-YYYY') else to_char(last_day(sysdate)+5,'DD-MM-YYYY') END AS NEXT_5TH FROM DUAL";
        MapSqlParameterSource params = new MapSqlParameterSource();
        return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
    }

    /*
    public String instalmentstartdate(String accOpenDt){
        String sql="select case when TO_NUMBER(to_char(to_date(:accOpenDt,'DD-MM-YYYY'),'DD')) between 5 and 15 then to_char(last_day(to_date(:accOpenDt,'DD-MM-YYYY'))+5,'DD-MM-YYYY') when TO_NUMBER(to_char(to_date(:accOpenDt,'DD-MM-YYYY'),'DD'))<=4  then TO_CHAR(to_date('05'||to_char(to_date(:accOpenDt,'DD-MM-YYYY'),'MM-YYYY'),'DD-MM-YYYY')+30,'DD-MM-YYYY') else to_char(add_months(last_day(to_date(:accOpenDt,'DD-MM-YYYY'))+5,1),'DD-MM-YYYY') end as inststartdate from dual";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("accOpenDt", accOpenDt);
        return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
    }
    */

    /*
    // Method to calculate installment start date based on the account opening date in dd-MM-yyyy format
    public  String instalmentstartdate(String accountOpeningDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Parse the input string to LocalDate
        LocalDate accountOpeningDate;
        try {
            accountOpeningDate = LocalDate.parse(accountOpeningDateStr, formatter);
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please provide the date in dd-MM-yyyy format.");
        }

        int dayOfMonth = accountOpeningDate.getDayOfMonth();
        LocalDate nextMonth5th;

        // If the account opening date is between 5th and 15th, return 5th of the next month
        if (dayOfMonth >= 5 && dayOfMonth <= 15) {
            nextMonth5th = accountOpeningDate.with(java.time.temporal.TemporalAdjusters.firstDayOfNextMonth()).withDayOfMonth(5);
        } else if(dayOfMonth >= 1 && dayOfMonth <= 4){
            LocalDate thirtyDaysLater = accountOpeningDate.plusDays(30);
            if(thirtyDaysLater.getDayOfMonth() >=1 &&  thirtyDaysLater.getDayOfMonth() <=4){
                nextMonth5th = thirtyDaysLater.withDayOfMonth(5);
            }else{
                nextMonth5th = thirtyDaysLater.with(java.time.temporal.TemporalAdjusters.firstDayOfNextMonth()).withDayOfMonth(5);
            }
        }else {
            // Else, return 5th of the month after 30 days from account opening
            LocalDate thirtyDaysLater = accountOpeningDate.plusDays(30);
            nextMonth5th = thirtyDaysLater.with(java.time.temporal.TemporalAdjusters.firstDayOfNextMonth()).withDayOfMonth(5);
        }

        String nextMonth5thStr = nextMonth5th.format(formatter);
        return nextMonth5thStr;
    }
*/
    public static String instalmentstartdate(String accountOpeningDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Parse the input string to LocalDate
        LocalDate accountOpeningDate;
        try {
            accountOpeningDate = LocalDate.parse(accountOpeningDateStr, formatter);
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please provide the date in dd-MM-yyyy format.");
        }

        int dayOfMonth = accountOpeningDate.getDayOfMonth();
        LocalDate nextMonth5th;

        // If the account opening date is between 5th and 15th, return 5th of the next month
        if (dayOfMonth >= 5 && dayOfMonth <= 15) {
            nextMonth5th = accountOpeningDate.with(java.time.temporal.TemporalAdjusters.firstDayOfNextMonth()).withDayOfMonth(5);
        } else if (dayOfMonth >= 1 && dayOfMonth <= 4) {
            LocalDate oneMonthLater = accountOpeningDate.plusMonths(1);

            if (oneMonthLater.getDayOfMonth() <= 4) {
                nextMonth5th = oneMonthLater.withDayOfMonth(5);
            } else {
                nextMonth5th = oneMonthLater.with(java.time.temporal.TemporalAdjusters.firstDayOfNextMonth()).withDayOfMonth(5);
            }
        } else {
            // Else, return 5th of the month after 30 days from account opening
            LocalDate oneMonthLater = accountOpeningDate.plusMonths(1);
            nextMonth5th = oneMonthLater.with(java.time.temporal.TemporalAdjusters.firstDayOfNextMonth()).withDayOfMonth(5);
        }

        String nextMonth5thStr = nextMonth5th.format(formatter);
        return nextMonth5thStr;
    }


    public int accountIsNre(String foracid) {
        String sql = " select count(*) from tbaadm.gam@finacle10 where foracid=:foracid and schm_code like '%NRE'  ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("foracid", foracid);
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public boolean isLMPPC(String ppcno) {
        String sql = "SELECT count(*) FROM ASSIGNSOL@mybank A JOIN HRMSEMPDTLS@mybank H ON H.PPCNO=A.PPCNO AND H.JOBROLE='LOCATION HUB MANAGER' AND trunc(A.END_DATE)=to_date('31-12-2099','dd-mm-yyyy') AND A.PPCNO=:ppcno ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ppcno", ppcno);
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class) > 0;
    }

    public boolean isRAHPPC(String ppcno) {
        String sql = "SELECT count(*) FROM ASSIGNSOL@mybank A JOIN HRMSEMPDTLS@mybank H ON H.PPCNO=A.PPCNO AND H.JOBROLE='SALES SUPPORT OFFICER RAH(HLBG)' AND trunc(A.END_DATE)=to_date('31-12-2099','dd-mm-yyyy') AND A.PPCNO=:ppcno ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ppcno", ppcno);
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class) > 0;
    }


    public List<String> getLHSols(String ppcno) {
        String sql = " SELECT B.SOL_ID FROM ASSIGNSOL@MYBANK A JOIN MISBMT@MYBANK B ON B.REG_CODE=A.SOL_ID JOIN HRMSEMPDTLS@MYBANK H ON H.PPCNO=A.PPCNO AND H.JOBROLE='LOCATION HUB MANAGER' AND trunc(A.END_DATE)=to_date('31-12-2099','dd-mm-yyyy') AND A.PPCNO=:ppcno union  all SELECT B.SOL_ID FROM ASSIGNSOL@MYBANK A JOIN MISBMT@MYBANK B ON B.REG_CODE=(SELECT  REG_CODE from misbmt@mybank where close_date is null and sol_id=a.sol_id  ) JOIN HRMSEMPDTLS@MYBANK H ON H.PPCNO=A.PPCNO AND H.JOBROLE='LOCATION HUB MANAGER' AND trunc(A.END_DATE)=to_date('31-12-2099','dd-mm-yyyy') AND A.PPCNO=:ppcno ";
        return jdbcTemplate.query(sql, new Object[]{ppcno, ppcno}, (rs, rowNum) -> {
            return rs.getString("sol_id");
        });
    }

    public List<String> getRAHSols(String ppcno) {
        String sql = "select p.SOL_ID from ppcbussolset@mybank p join misbmt@mybank m on p.sol_id=m.sol_id  where m.close_date is null and trunc(sysdate) between p.start_date and p.end_date and  ppcno=:ppcno order by 1 ";
        return jdbcTemplate.query(sql, new Object[]{ppcno}, (rs, rowNum) -> {
            return rs.getString("sol_id");
        });
    }

    //        return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
    public List<Map<String, String>> getMSSFSol() {
        String sql = "SELECT B.sol_id, TRIM(B.br_name) AS br_name FROM MISBMT@MYBANK B WHERE CLOSE_DATE IS NULL";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("codedesc", rs.getString("br_name").trim().toUpperCase() + "(" + rs.getString("sol_id").trim() + ")");
            map.put("codevalue", rs.getString("sol_id").trim());
            return map;
        });
    }

    // Search by `sol_id`
    public List<Map<String, String>> getMSSFSolById(String solId) {
        String sql = "SELECT B.sol_id, TRIM(B.br_name) AS br_name FROM MISBMT@MYBANK B WHERE CLOSE_DATE IS NULL AND B.sol_id = ?";
        return jdbcTemplate.query(sql, new Object[]{solId}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("codedesc", rs.getString("br_name").trim().toUpperCase() + "(" + rs.getString("sol_id").trim() + ")");
            map.put("codevalue", rs.getString("sol_id").trim());
            return map;
        });
    }

    // Search by `br_name`
    public List<Map<String, String>> getMSSFSolByName(String name) {
        String sql = "SELECT B.sol_id, TRIM(B.br_name) AS br_name FROM MISBMT@MYBANK B WHERE CLOSE_DATE IS NULL AND LOWER(TRIM(B.br_name)) LIKE ?";
        return jdbcTemplate.query(sql, new Object[]{"%" + name.toLowerCase() + "%"}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("codedesc", rs.getString("br_name").trim().toUpperCase() + "(" + rs.getString("sol_id").trim() + ")");
            map.put("codevalue", rs.getString("sol_id").trim());
            return map;
        });
    }


    public List<Map<String, String>> getLHSol(String sol_id, String ppcno) {
        String sql = " select B.sol_id,B.br_name FROM ASSIGNSOL@MYBANK A JOIN MISBMT@MYBANK B ON B.REG_CODE=A.SOL_ID JOIN HRMSEMPDTLS@MYBANK H ON H.PPCNO=A.PPCNO AND H.JOBROLE='LOCATION HUB MANAGER' AND trunc(A.END_DATE)=to_date('31-12-2099','dd-mm-yyyy') AND B.CLOSE_DATE IS NULL AND A.PPCNO=:ppcno AND B.SOL_ID=:sol_id union all select B.sol_id,B.br_name FROM ASSIGNSOL@MYBANK A JOIN MISBMT@MYBANK B ON B.REG_CODE=(SELECT  REG_CODE from misbmt@mybank where close_date is null and sol_id=a.sol_id  ) JOIN HRMSEMPDTLS@MYBANK H ON H.PPCNO=A.PPCNO AND H.JOBROLE='LOCATION HUB MANAGER' AND trunc(A.END_DATE)=to_date('31-12-2099','dd-mm-yyyy') AND A.PPCNO=:ppcno AND B.SOL_ID=:sol_id";
        return jdbcTemplate.query(sql, new Object[]{ppcno, sol_id, ppcno, sol_id}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("codedesc", rs.getString("br_name").toUpperCase().trim() + "(" + rs.getString("sol_id") + ")");
            map.put("codevalue", rs.getString("sol_id"));
            return map;
        });
    }

    public List<Map<String, String>> getRAHSol(String sol_id, String ppcno) {
        String sql = " select B.sol_id,B.br_name FROM ASSIGNSOL@MYBANK A JOIN MISBMT@MYBANK B ON B.REG_CODE=A.SOL_ID JOIN HRMSEMPDTLS@MYBANK H ON H.PPCNO=A.PPCNO AND H.JOBROLE='SALES SUPPORT OFFICER RAH(HLBG)' AND trunc(A.END_DATE)=to_date('31-12-2099','dd-mm-yyyy') AND B.CLOSE_DATE IS NULL AND A.PPCNO=:ppcno AND B.SOL_ID=:sol_id union all select B.sol_id,B.br_name FROM ASSIGNSOL@MYBANK A JOIN MISBMT@MYBANK B ON B.REG_CODE=(SELECT  REG_CODE from misbmt@mybank where close_date is null and sol_id=a.sol_id  ) JOIN HRMSEMPDTLS@MYBANK H ON H.PPCNO=A.PPCNO AND H.JOBROLE='SALES SUPPORT OFFICER RAH(HLBG)' AND trunc(A.END_DATE)=to_date('31-12-2099','dd-mm-yyyy') AND A.PPCNO=:ppcno AND B.SOL_ID=:sol_id";
        return jdbcTemplate.query(sql, new Object[]{ppcno, sol_id, ppcno, sol_id}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("codedesc", rs.getString("br_name").toUpperCase().trim() + "(" + rs.getString("sol_id") + ")");
            map.put("codevalue", rs.getString("sol_id"));
            return map;
        });
    }


    public String limSancExpDate(String instStartDate, int tenor) {
        String sql = " select to_char(add_months(to_date(:instStartDate,'DD-MM-YYYY'), :tenor -1) ,'DD-MM-YYYY') from dual";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("instStartDate", instStartDate)
                .addValue("tenor", tenor);
        return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
    }

    public AgreementDetailsDTO findAgreementDetailsByStateCode(String stateCode) {
        try {
            String sql = "SELECT STATE_CODE, STATE_NAME, AGREEMENT_CODE, AGREEMENT_FIXED, AGREEMENT_PERCANTAGE, " +
                    "AGREEMENT_VALUE, AGREEMENT_MIN_VALUE, AGREEMENT_MAXIMUM_VALUE, HYPOTHECATION_CODE, " +
                    "HYPOTHECATION_FIXED, HYPOTHECATION_PERCANTAGE, HYPOTHECATION_VALUE, HYPOTHECATION_MIN_VALUE, " +
                    "HYPOTHECATION_MAXIMUM_VALUE, GUARANTEE_CODE, GUARANTEE_FIXED, GUARANTEE_PERCANTAGE, " +
                    "GUARANTEE_VALUE, GUARANTEE_MIN_VALUE, GUARANTEE_MAXIMUM_VALUE, ARBITRATION_CODE, " +
                    "ARBITRATION_FIXED, ARBITRATION_PERCANTAGE, ARBITRATION_VALUE, ARBITRATION_MIN_VALUE, " +
                    "ARBITRATION_MAXIMUM_VALUE FROM vlstampmas@mybank WHERE DEL_FLAG='N' AND STATE_CODE=?";

            List<AgreementDetailsDTO> result = jdbcTemplate.query(
                    sql,
                    new Object[]{stateCode},
                    new RowMapper<AgreementDetailsDTO>() {
                        @Override
                        public AgreementDetailsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                            AgreementDetailsDTO dto = new AgreementDetailsDTO();
                            dto.setStateCode(rs.getString("STATE_CODE"));
                            dto.setStateName(rs.getString("STATE_NAME"));
                            dto.setAgreementCode(rs.getString("AGREEMENT_CODE"));
                            dto.setAgreementFixed(rs.getString("AGREEMENT_FIXED"));
                            dto.setAgreementPercentage(rs.getString("AGREEMENT_PERCANTAGE"));
                            dto.setAgreementValue(rs.getString("AGREEMENT_VALUE"));
                            dto.setAgreementMinValue(rs.getBigDecimal("AGREEMENT_MIN_VALUE"));
                            dto.setAgreementMaxValue(rs.getBigDecimal("AGREEMENT_MAXIMUM_VALUE"));
                            dto.setHypothecationCode(rs.getString("HYPOTHECATION_CODE"));
                            dto.setHypothecationFixed(rs.getString("HYPOTHECATION_FIXED"));
                            dto.setHypothecationPercentage(rs.getString("HYPOTHECATION_PERCANTAGE"));
                            dto.setHypothecationValue(rs.getString("HYPOTHECATION_VALUE"));
                            dto.setHypothecationMinValue(rs.getBigDecimal("HYPOTHECATION_MIN_VALUE"));
                            dto.setHypothecationMaxValue(rs.getBigDecimal("HYPOTHECATION_MAXIMUM_VALUE"));
                            dto.setGuaranteeCode(rs.getString("GUARANTEE_CODE"));
                            dto.setGuaranteeFixed(rs.getString("GUARANTEE_FIXED"));
                            dto.setGuaranteePercentage(rs.getString("GUARANTEE_PERCANTAGE"));
                            dto.setGuaranteeValue(rs.getString("GUARANTEE_VALUE"));
                            dto.setGuaranteeMinValue(rs.getBigDecimal("GUARANTEE_MIN_VALUE"));
                            dto.setGuaranteeMaxValue(rs.getBigDecimal("GUARANTEE_MAXIMUM_VALUE"));
                            dto.setArbitrationCode(rs.getString("ARBITRATION_CODE"));
                            dto.setArbitrationFixed(rs.getString("ARBITRATION_FIXED"));
                            dto.setArbitrationPercentage(rs.getString("ARBITRATION_PERCANTAGE"));
                            dto.setArbitrationValue(rs.getString("ARBITRATION_VALUE"));
                            dto.setArbitrationMinValue(rs.getBigDecimal("ARBITRATION_MIN_VALUE"));
                            dto.setArbitrationMaxValue(rs.getBigDecimal("ARBITRATION_MAXIMUM_VALUE"));
                            return dto;
                        }
                    }
            );
            if (result.isEmpty()) {
                throw new ValidationException(ValidationError.COM001, "The Applicants Permanent Address State is Not Present in Digitally enrolled State List");
            }

            return result.get(0);
        } catch (EmptyResultDataAccessException ex) {
            throw new ValidationException(ValidationError.COM001, "The Applicants Permanent Address State is Not Present in Digitally enrolled State List");
        } catch (Exception e) {
            throw new ValidationException(ValidationError.COM001, e.getMessage());
        }
    }

    public String getDesig(String ppcno, String date) {
        String sql = " select desig from mishrd@mybank where ppcno= :ppcno AND to_date(:date,'DD-MM-YYYY') between start_date and end_date   ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ppcno", ppcno)
                .addValue("date", date);
        return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
    }

    public String getSanCode(String desig) {
        String sql = " select codedesc from misrct where codetype='SCODE' AND CODEVALUE= :desig and nvl(delflag,'N')='N' ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("desig", desig);
        return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
    }

    public String findBodyTypeByMakeAndVariant(String makeId, String variantId) {
        String sql = "select BODY_tYPE from VLCDMODEL c join VLCDVARIANT t on c.MODEL_ID=t.MODEL_ID where MAKE_ID=? and VARIANT_ID=?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{makeId, variantId}, String.class);
        } catch (Exception e) {
            // Handle the case where no category is found
            return "Missing";
        }
    }

    public String findCategoryByMakeAndModel(String makeName) {
        String sql = "SELECT master_name FROM Vlcdmake WHERE MAKE_NAME = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{makeName}, String.class);
        } catch (Exception e) {
            // Handle the case where no category is found
            return "Missing";
        }
    }

    public String findLatestRemarks(String wiNum) {
        String sql = "SELECT REMARKS FROM VEHICLE_LOAN_QUEUE_DETAILS WHERE WI_NUM = ? AND CMDATE = (SELECT MAX(CMDATE) FROM VEHICLE_LOAN_QUEUE_DETAILS WHERE WI_NUM = ?)";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{wiNum, wiNum}, String.class);
        } catch (Exception e) {
            // Handle the case where no category is found
            return " ";
        }
    }

    public String findVehicleLTV(Long slno) {
        String sql = "SELECT (ltv_per/100)ltv_per FROM vehicle_loan_eligibility WHERE SLNO = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{slno}, String.class);
        } catch (Exception e) {
            // Handle the case where no category is found
            return "0.00";
        }
    }

    public String getAccountNumAndOpenDate(String cifId, String limit_sanct_ref_num) {
        String sql = "select foracid||'|'|| to_char(rcre_time,'dd-mm-yyyy hh24:mi:ss') accopeneddate " +
                "from tbaadm.gam@finacle10 gam " +
                "where cust_id=:cifId and acct_cls_flg='N' and exists(select 1 from tbaadm.lht@finacle10 where lht.acid=gam.acid and lht.sanct_ref_num=:limit_sanct_ref_num)";
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("cifId", cifId)
                    .addValue("limit_sanct_ref_num", limit_sanct_ref_num);
            return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            // Handle the case where no category is found
            return "0.00";
        }
    }

    public BigDecimal getDisbursedAmountFromCBS(String tranId, String tranDate) {
        /*
        String tablename="htd";
        if(CommonUtils.isToday(tranDate)){
            tablename="dtd";
        }
         */
        String sql = "select tran_amt from tbaadm.dtd@finacle10 where tran_id=:tranId and acid=(select g.acid from tbaadm.gam@finacle10 g where g.foracid=:bogPoolAcc) and part_tran_type='C' and tran_date=to_date(:tranDate ,'YYYY-MM-DD')";//fetch the part tran where credit does to bog suspense account
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("tranId", tranId)
                    .addValue("tranDate", tranDate)
                    .addValue("bogPoolAcc", bogPoolAcc);
            return namedParameterJdbcTemplate.queryForObject(sql, params, BigDecimal.class);
        } catch (Exception e) {
            // Handle the case where no category is found
            //return BigDecimal.ZERO;
            sql = "select tran_amt from tbaadm.htd@finacle10 where tran_id=:tranId and acid=(select g.acid from tbaadm.gam@finacle10 g where g.foracid=:bogPoolAcc) and part_tran_type='C' and tran_date=to_date(:tranDate ,'YYYY-MM-DD')";//fetch the part tran where credit does to bog suspense account
            try {
                MapSqlParameterSource params = new MapSqlParameterSource()
                        .addValue("tranId", tranId)
                        .addValue("tranDate", tranDate)
                        .addValue("bogPoolAcc", bogPoolAcc);
                return namedParameterJdbcTemplate.queryForObject(sql, params, BigDecimal.class);
            } catch (Exception e1) {
                // Handle the case where no category is found
                return BigDecimal.ZERO;
            }
        }
    }

    public String fetchDealerAcctNoAndIfsc(String dealercode, String dealersubcode, String cityId, String cityname, String oemid, String slno) {
        cityname = cityname.trim();

        String sql = " select foracid||'-'||ifsc from dealersubmas@mybank s join dealermas@mybank d  on s.slno=d.slno join VEHICLE_LOAN_VEHICLE v on v.DEALER_ACCOUNT=foracid and v.DEALER_IFSC=ifsc where city_id=? and trim(city_name)=trim(?) and oem_id=? and " +
                "d.dealer_code=? and d.dealer_sub_code=? and nvl(d.del_flg,'N')='N' and v.slno=?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{cityId, cityname, oemid, dealercode, dealersubcode, slno}, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean isvalidVehicleAccount(String slno) {
        String sql = " select count(*) from dealersubmas@mybank s join dealermas@mybank d  on s.slno=d.slno join VEHICLE_LOAN_VEHICLE v on v.DEALER_ACCOUNT=foracid and v.DEALER_IFSC=ifsc where city_id=v.DEALER_CITY_ID and oem_id=v.MAKE_ID and  d.dealer_code=v.DEALER_CODE and d.dealer_sub_code=v.DEALER_SUB_CODE and nvl(d.del_flg,'N')='N' and v.slno=:slno";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("slno", slno);
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class) > 0;
    }

    public String fetchDealerMobile(String dealercode, String dealersubcode) {

        String sql = "select mob_no from dealermas@mybank d where d.dealer_code=:dealercode and d.dealer_sub_code=:dealersubcode and nvl(d.del_flg,'N')='N' ";
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("dealercode", dealercode)
                    .addValue("dealersubcode", dealersubcode);
            return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            // Handle the case where no category is found
            return "";
        }
    }

    public int checkDealerBlocked(String dealercode, String dealersubcode, String cityId, String cityname, String oemid) {
        cityname = cityname.trim();
        String sql = " select count(*) from dealersubmas@mybank where city_id=:cityId and city_name=:cityname and oem_id=:oemid and nvl(block_user,'N')='N' and slno in (" +
                "select d.slno from dealermas@mybank d where d.dealer_code=:dealercode and d.dealer_sub_code=:dealersubcode and nvl(d.del_flg,'N')='N' and " +
                "nvl(block_user,'N')='N' and nvl(del_flg,'N')='N' " +
                ")";
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("cityId", cityId)
                    .addValue("cityname", cityname)
                    .addValue("oemid", oemid)
                    .addValue("dealercode", dealercode)
                    .addValue("dealersubcode", dealersubcode);
            return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            // Handle the case where no category is found
            return -1;
        }
    }

    public Map<String, String> getDealerCityLocation(String dealercode, String dealersubcode, String cityId, String cityname, String oemid) {

        cityname = cityname.trim();
        String sql = " select city_name,state from dealersubmas@mybank where city_id=? and city_name=? and oem_id=? and slno in (" +
                "select d.slno from dealermas@mybank d where d.dealer_code=? and d.dealer_sub_code=? and nvl(d.del_flg,'N')='N' " +
                ") and rownum=1";
        try {

            return jdbcTemplate.queryForObject(sql, new Object[]{cityId, cityname, oemid, dealercode, dealersubcode}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("city_name", rs.getString("city_name").toUpperCase().trim());
                map.put("state", rs.getString("state"));
                return map;
            });
        } catch (Exception e) {
            // Handle the case where no category is found
            return null;
        }
    }


    public Map<String, String> getDTD(String accountNum, String refnum, String part_tran_type) {
        String tablename = "dtd";
        String sql = "select tran_id, to_char(tran_date,'DD-MM-YYYY') tran_date, tran_amt from tbaadm." + tablename + "@finacle10 where  " +
                "acid=(select g.acid from tbaadm.gam@finacle10 g where g.foracid=:accountNum) " +
                "and part_tran_type=:part_tran_type  and ref_num=:refnum and pstd_flg='Y' ";//fetch the part tran where credit does to bog suspense account
        try {

            return jdbcTemplate.queryForObject(sql, new Object[]{accountNum, part_tran_type, refnum}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("tran_id", rs.getString("tran_id").toUpperCase().trim());
                map.put("tran_date", rs.getString("tran_date"));
                map.put("tran_amt", rs.getString("tran_amt"));
                return map;
            });
        } catch (Exception e) {
            // Handle the case where no category is found
            return null;
        }
    }

    public Map<String, String> getHTD(String accountNum, String refnum, String part_tran_type, String tranDate) {
        String tablename = "htd";
        /*
        if(CommonUtils.isToday(tranDate)){
            tablename="dtd";
        }

         */
        String sql = "select tran_id, to_char(tran_date,'DD-MM-YYYY') tran_date , tran_amt from tbaadm." + tablename + "@finacle10 where " +
                "tran_date=:tranDate and " +
                "acid=(select g.acid from tbaadm.gam@finacle10 g where g.foracid=:accountNum) " +
                "and part_tran_type=:part_tran_type  and ref_num=:refnum and pstd_flg='Y' ";//fetch the part tran where credit does to bog suspense account
        try {

            return jdbcTemplate.queryForObject(sql, new Object[]{tranDate, accountNum, part_tran_type, refnum}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("tran_id", rs.getString("tran_id").toUpperCase().trim());
                map.put("tran_date", rs.getString("tran_date"));
                map.put("tran_amt", rs.getString("tran_amt"));
                return map;
            });
        } catch (Exception e) {
            // Handle the case where no category is found
            return null;
        }
    }

    public BigDecimal getTotalProcessingCharge(String slno, String program) {
        String sql = "select SUM(NVL(FINAL_FEE,0)) from VEHICLE_LOAN_CHARGE_WAIVER v " +
                "join vlchargemas@mybank m on v.fee_code=m.charge_code " +
                "JOIN VLCHARGE@MYBANK C ON C.PROGRAM_NAME=M.PROGRAM_NAME AND C.CHARGE_CODE=M.CHARGE_CODE " +
                "where v.slno=:slno and m.program_name=:program AND M.PREDICTIVE='Y' AND NVL(C.DEL_FLAG,'N')='N' AND NVL(V.DEL_FLAG,'N')='N'";
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("slno", slno)
                    .addValue("program", program);
            return namedParameterJdbcTemplate.queryForObject(sql, params, BigDecimal.class);
        } catch (Exception e) {
            // Handle the case where no category is found
            return null;
        }
    }

    public String getTdsCode(String corp_flg, String NRE_STATUS, String SNRCTZN_STAT, String AGE80_STAT, String PANSTAT, String AADH_LNK_STAT,
                             String STAT_206, String STAT_15g, String VALUE_15g, String AP_PREV) {
        String sql = "select newfinact_tdscode@finacle10(?,?,?,?,?,?,?,?,?,?) from dual";
        return jdbcTemplate.queryForObject(sql, new Object[]{corp_flg, NRE_STATUS, SNRCTZN_STAT, AGE80_STAT, PANSTAT, AADH_LNK_STAT, STAT_206, STAT_15g, VALUE_15g, AP_PREV}, (rs, rowNum) -> {
            String tdscode = rs.getString(1).split("\\|")[0];
            return tdscode;
        });
    }


    public int loanIsDisbursed(String foracid) {
        String sql = " select count(*) from tbaadm.lam@finacle10 where acid=(select g.acid from tbaadm.gam@finacle10 g where g.foracid=:foracid) and dis_amt>0  " +
                "and del_flg='N' AND ENTITY_CRE_FLG='Y' ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("foracid", foracid);
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public List<Map<String, Object>> getBureauDetailsByApplicant(String wiNum) {
        String sql = "SELECT FIRST_NAME, MIDDLE_NAME, LAST_NAME, MASKED_AADHAAR, " +
                "NAME, STATUS, AADHAAR_LINKED, GENDER, " +
                "IS_PAN_VALID, PAN_TYPE, PAN, SLNO, APPLICANT_ID " +
                "FROM BUREAU_CHECK_DETAILS " +
                "WHERE WI_NUM = ?  AND DEL_FLG = 'N' " +
                "ORDER BY SLNO, INO DESC";
        return jdbcTemplate.queryForList(sql, wiNum);
    }

    public List<Map<String, String>> getAppData( ) {
        String sql = "select CODEVALUE,CODEDESC from MISRCT@MYBANK where CODETYPE='LS' and CODEVALUE<>'VL' and DELFLAG='N'";
        try {
            return jdbcTemplate.query(sql, new Object[]{}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put( rs.getString("CODEVALUE"), rs.getString("CODEDESC"));
                return map;
            });
        } catch (EmptyResultDataAccessException e) {
            return null; // Return an empty map if no result is found
        }
    }
    public BigDecimal getMclrRate() {
    String sql = "select getlaareporate@finacle10('MAB12',sysdate) from dual";
    try {
        String result = jdbcTemplate.queryForObject(sql, String.class);
        // The result might be in format like "8.50|other_data", so we take the first part
        if (result != null && result.contains("|")) {
            return new BigDecimal(result.split("\\|")[0]);
        }
        return new BigDecimal(result != null ? result : "0");
    } catch (Exception e) {
        log.error("Error fetching MCLR rate", e);
        return BigDecimal.ZERO;
    }
}
}
