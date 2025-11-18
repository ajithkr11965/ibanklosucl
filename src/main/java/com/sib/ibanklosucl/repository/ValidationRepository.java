package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.dto.LosDedupeRequestDTO;
import com.sib.ibanklosucl.dto.VehicleEmpProgram;
import com.sib.ibanklosucl.dto.losintegrator.dk.ScoreInfo;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.utilies.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ValidationRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public boolean checkAge(String programType, String empType, int age) throws ValidationException {

        String sql = "SELECT VIEW_DATA FROM VLAGEELIGIBLEMAS@mybank WHERE " +
                "PROGRAM_NAME = :programName " +
                "AND (EMPLOYMENT_NAME = :employmentName OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("programName", programType)
                .addValue("employmentName", empType);
        try {
            String view = namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
            if (view != null) {
                sql = "SELECT count(*) FROM VLAGEELIGIBLEMAS@mybank WHERE " +
                        "PROGRAM_NAME = :programName " +
                        "AND :age BETWEEN MIN_AGE AND MAX_AGE " +
                        "AND (EMPLOYMENT_NAME = :employmentName OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";

                params.addValue("age", age);
                int count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
                if (count < 0) {
                    throw new ValidationException(ValidationError.COM001, Constants.KYCTabMessages.AGE_NOT_ELIGIBLE + "( " + view + " ).");
                }
                return count == 1;
            } else {
                return true;
            }
        } catch (EmptyResultDataAccessException e) {
            return true;
        }
    }

    public boolean checkExperianEligible(String programType, String empType, Long score) throws ValidationException {

        String sql = "SELECT VIEW_DATA FROM VLBUREAUBRESLABMAS@mybank WHERE " +
                "PROGRAM_NAME = :programName " +
                "AND (EMPLOYMENT_NAME = :employmentName OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("programName", programType)
                .addValue("employmentName", empType);
        try {
            String view = namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
            if (view != null) {
                sql = "SELECT count(*) FROM VLBUREAUBRESLABMAS@mybank WHERE " +
                        "PROGRAM_NAME = :programName " +
                        "AND :score BETWEEN MIN_BUREAU_SCORE AND MAX_BUREAU_SCORE " +
                        "AND (EMPLOYMENT_NAME = :employmentName OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";

                params.addValue("score", score);
                int count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
                if (count < 0) {
                    throw new ValidationException(ValidationError.COM001, "Credit Score didnt meet Required Criteria " + "( " + view + " ).");
                }
                return count == 1;
            } else {
                return true;
            }
        } catch (EmptyResultDataAccessException e) {
            return true;
        }
    }

    public boolean checkExperianEligibleColor(String programType, String empType, Long score) throws ValidationException {
        try {
            String sql = "SELECT COUNT(*) FROM VLCOLOURSLABMAS@mybank WHERE " +
                    "PROGRAM_NAME = ? " +
                    "AND DEL_FLAG = 'N'";
            Integer count = jdbcTemplate.queryForObject(sql, new Object[]{programType}, Integer.class);
            if(count>0) {

                sql = "SELECT COUNT(*) FROM VLCOLOURSLABMAS@mybank WHERE " +
                        "PROGRAM_NAME = ? AND ? BETWEEN START_SCORE AND END_SCORE " +
                        "AND DEL_FLAG = 'N'  and color not in ('red')";
                 count = jdbcTemplate.queryForObject(sql, new Object[]{programType, score}, Integer.class);
                if (count > 0) {
                    return true;
                } else {
                    return false;
                }
            }
            else{
                return true;
            }
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public boolean isSuperUSer(String ip,String ppcno){
       String sql = " SELECT COUNT(*) FROM  consolupdmast@mybank WHERE DELFLAG='N' AND PPCNO=:ppcno AND  IP=:ip";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ppcno", ppcno)
                .addValue("ip", ip);
       // return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class)>0;
        return true;
    }
    public boolean isSuperUSer(String ip){
       String sql = " SELECT COUNT(*) FROM  consolupdmast@mybank WHERE DELFLAG='N'  AND  IP=:ip";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ip", ip);
        //return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class)>0;
        return true;
    }


    public boolean validateBorrowerMinIncome(String programType, String empType, String monthlyIncome) {
        String sql = "SELECT count(*) FROM VLINCELIGIBLEMAS@mybank WHERE " +
                "PROGRAM_NAME = :programName " +
                "AND (EMPLOYMENT_NAME = :employmentName OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("programName", programType)
                .addValue("employmentName", empType);

        int count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        if (count > 0) {
            sql = "SELECT count(*) FROM VLINCELIGIBLEMAS@mybank WHERE " +
                    "PROGRAM_NAME = :programName " +
                    "AND :monthlyIncome >=MIN_ELIGIBLE_AMOUNT " +
                    "AND (EMPLOYMENT_NAME = :employmentName OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N';";

            params.addValue("monthlyIncome", monthlyIncome);
            count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
            return count == 1;
        } else {
            return true;
        }
    }


    public void checkLoanAmount(List<VehicleEmpProgram> applicants, BigDecimal loanAmt,String program,String referer) throws ValidationException {
        for(VehicleEmpProgram app : applicants){
            String sql = "SELECT VIEW_DATA FROM VLLOANAMOUNTELIGIBLEMAS@mybank WHERE " +
                    "PROGRAM_NAME = :programName " +
                    "AND (EMPLOYMENT_NAME = :employmentName OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("programName",program)
                    .addValue("employmentName", app.getEmploymentType());
            try {
                String view = namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
                log.info("checkloan amount - {} program {}  applicant id {} applicantName",loanAmt,program,app.getApplicantId(),app.getApplName());
                if (view != null) {
                    sql = "SELECT count(*) FROM VLLOANAMOUNTELIGIBLEMAS@mybank WHERE " +
                            "PROGRAM_NAME = :programName " +
                            "AND :loanAmt BETWEEN MIN_AMT AND MAX_AMT " +
                            "AND (EMPLOYMENT_NAME = :employmentName OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";

                    params.addValue("loanAmt", loanAmt);
                    int count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
                    if (count <= 0) {
                        if ("ELIG".equals(referer)) {
                            throw new ValidationException(ValidationError.ERRO03, app.getApplName(),loanAmt, view);

                        } else {
                            throw new ValidationException(ValidationError.ERRO03, app.getApplName(), view);
                    }
                    }
                }
            } catch (EmptyResultDataAccessException e) {
                //  throw new ValidationException(ValidationError.COM001, "Master Parameter's are not Set For Loan Amount !");
            }
        }
    }

    public void checkLoanTenor(List<VehicleEmpProgram> applicants, Integer loanTenor) throws ValidationException {
        for (VehicleEmpProgram app : applicants) {
            String sql = "SELECT VIEW_DATA FROM VLTENUREELIGIBLEMAS@mybank WHERE " +
                    "PROGRAM_NAME = :programName " +
                    "AND (EMPLOYMENT_NAME = :employmentName OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("programName", app.getLoanProgram())
                    .addValue("employmentName", app.getEmploymentType());
            try {
                String view = namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
                if (view != null) {
                    sql = "SELECT count(*) FROM VLTENUREELIGIBLEMAS@mybank WHERE " +
                            "PROGRAM_NAME = :programName " +
                            "AND :loanTenor BETWEEN MIN_TENURE AND MAX_TENURE " +
                            "AND (EMPLOYMENT_NAME = :employmentName OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";

                    params.addValue("loanTenor", loanTenor);
                    int count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
                    if (count <= 0) {
                        throw new ValidationException(ValidationError.ERRO04, view);
                    }
                }
            } catch (EmptyResultDataAccessException e) {
                //   throw new ValidationException(ValidationError.COM001, "Master Parameter's are not Set For Loan Tenor !");
            }
        }
    }

    public void isAllTabCompleted(Long slno) throws ValidationException {
        String sql = "SELECT APPL_NAME, GEN_COMPLETE, KYC_COMPLETE, BASIC_COMPLETE, EMPLOYMENT_COMPLETE, INCOME_COMPLETE, CREDIT_COMPLETE " +
                "FROM vehicle_loan_applicants WHERE DEL_FLG = 'N' AND SLNO=:slno";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("slno", slno);
        List<Map<String, Object>> applicants = namedParameterJdbcTemplate.queryForList(sql, params);
        applicants.stream().forEach(applicant -> {
            String applName = (String) applicant.get("APPL_NAME");

            if (!"Y".equals(applicant.get("GEN_COMPLETE"))) {
                throw new ValidationException(ValidationError.ERRO11, "General Details", applName);
            }

            if (!"Y".equals(applicant.get("KYC_COMPLETE"))) {
                throw new ValidationException(ValidationError.ERRO11, "KYC Details", applName);
            }

            if (!"Y".equals(applicant.get("BASIC_COMPLETE"))) {
                throw new ValidationException(ValidationError.ERRO11, "BASIC  Details", applName);
            }

            if (!"Y".equals(applicant.get("EMPLOYMENT_COMPLETE"))) {
                throw new ValidationException(ValidationError.ERRO11, "EMPLOYMENT  Details", applName);
            }

            if (!"Y".equals(applicant.get("INCOME_COMPLETE"))) {
                throw new ValidationException(ValidationError.ERRO11, "Program  Details", applName);
            }

            if (!"Y".equals(applicant.get("CREDIT_COMPLETE"))) {
                throw new ValidationException(ValidationError.ERRO11, "CREDIT  Details", applName);
            }
        });
    }

    public String validateLoanPrograms(List<VehicleEmpProgram> applicants) throws ValidationException {
        Map<String, List<VehicleEmpProgram>> applicantsByProgram = applicants.stream()
                .collect(Collectors.groupingBy(VehicleEmpProgram::getLoanProgram));
        long nonNoneProgramsCount = applicantsByProgram.keySet().stream()
                .filter(program -> !"NONE".equals(program))
                .count();
        long surrogateCount = applicantsByProgram.keySet().stream()
                .filter("SURROGATE"::equals)
                .count();

        if (nonNoneProgramsCount == 0) {
            throw new ValidationException(ValidationError.ERRO02);
        }

        if (nonNoneProgramsCount > 1) {
            throw new ValidationException(ValidationError.ERRO01);
        }

        if (surrogateCount > 1) {
            throw new ValidationException(ValidationError.ERRO09);
        }

        //Guarnator only NONE
        if (applicants.stream().anyMatch(t -> !"NONE".equals(t.getLoanProgram()) && "G".equals(t.getApplicantType()))) {
            throw new ValidationException(ValidationError.ERRO13);
        }
        // Ensure all applicants have the correct program string
        return applicantsByProgram.keySet().stream()
                .filter(program -> !"NONE".equals(program))
                .findFirst()
                .orElseThrow(() -> new ValidationException(ValidationError.ERRO02));

    }


    public boolean checkWhetherStaff(String cust_id) {
        String sql = "select count(*) from crmuser.accounts@finacle10 where staffflag='Y' and  orgkey=:cust_id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("cust_id", cust_id);
        int count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count > 0;
    }

    public boolean checkSibAccount(String foracid, Long slno) {
        String constitution_code="R3302";//PROPRIETORY CONCERNS
        String sql1 = " select mode_of_oper_code from tbaadm.gam@FINACLE10 where foracid=:foracid";
        MapSqlParameterSource params1 = new MapSqlParameterSource()
                .addValue("foracid", foracid);
        String mode_of_oper_code = namedParameterJdbcTemplate.queryForObject(sql1, params1, String.class);
        if("003".equals(mode_of_oper_code)){//EITHER OR SURVIVOR
            String sql="select count(*) from tbaadm.gam@finacle10 g, tbaadm.aas@finacle10 a where g.foracid=:foracid and g.acid=a.acid and a.cust_id is not null and acct_poa_as_rec_type in ('M','J') and g.SCHM_TYPE in('SBA','CAA','ODA','CCA') and NVL(g.frez_code,'-') not in('D','T') and g.ACCT_CLS_FLG='N' and exists( select 1 from vehicle_loan_applicants app where app.slno=:slno and app.del_flg='N' and app.applicant_type in ('A','C') and app.cif_id=a.cust_id)";//a.cust_id cust_id, acct_poa_as_rec_type acct_type, acct_poa_as_name acct_name
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("slno", slno)
                    .addValue("foracid", foracid);
            int count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
            if(count==0){//perform tds cif id check

                sql="select count(*) from tbaadm.gam@finacle10 g, tbaadm.aas@finacle10 a , crmuser.accounts@finacle10 acc where g.foracid=:foracid and g.acid=a.acid and a.cust_id is not null and acct_poa_as_rec_type in ('M','J') and g.SCHM_TYPE in('SBA','CAA','ODA','CCA') and NVL(g.frez_code,'-') not in('D','T') and g.ACCT_CLS_FLG='N' and a.cust_id=acc.orgkey and acc.constitution_code=:constitution_code and exists ( select 1 from vehicle_loan_applicants app where app.slno=:slno and app.del_flg='N' and app.applicant_type in ('A','C') and app.cif_id=acc.tds_cifid)";
                params=null;
                params = new MapSqlParameterSource()
                        .addValue("slno", slno)
                        .addValue("foracid", foracid)
                        .addValue("constitution_code", constitution_code);
                count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
                return count>0;
            }else{
              return true;
            }
        }else {

            String sql = "select count(*) from tbaadm.gam@FINACLE10 where  SCHM_TYPE in('SBA','CAA','ODA','CCA') and NVL(frez_code,'-') not in('D','T') and ACCT_CLS_FLG='N' and  CUST_ID in (select CIF_ID from VEHICLE_LOAN_APPLICANTS where APPLICANT_TYPE in('A','C') AND SIB_CUSTOMER='Y' AND DEL_FLG='N' and SLNO=:slno) and foracid=:foracid";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("slno", slno)
                    .addValue("foracid", foracid);
            int count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
            //return count > 0;
            if(count==0){//perform tds cif id check for main holder

                sql="select count(*) from tbaadm.gam@finacle10 g, tbaadm.aas@finacle10 a , crmuser.accounts@finacle10 acc where g.foracid=:foracid and g.acid=a.acid and a.cust_id is not null and acct_poa_as_rec_type in ('M') and g.SCHM_TYPE in('SBA','CAA','ODA','CCA') and NVL(g.frez_code,'-') not in('D','T') and g.ACCT_CLS_FLG='N' and a.cust_id=acc.orgkey and acc.constitution_code=:constitution_code and exists ( select 1 from vehicle_loan_applicants app where app.slno=:slno and app.del_flg='N' and app.applicant_type in ('A','C') and app.cif_id=acc.tds_cifid)";
                params=null;
                params = new MapSqlParameterSource()
                        .addValue("slno", slno)
                        .addValue("foracid", foracid)
                        .addValue("constitution_code", constitution_code);
                count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
                return count>0;
            }else{
                return true;
            }
        }
    }


    public LosDedupeRequestDTO getApplicantDetails(String applicantId,String hi) {
        String sql = "select appl_name custName, to_char(appl_dob, 'dd"+hi+"mm"+hi+"yyyy') dob, " +
                "pan_no panNo, aadhar_ref_num aadharNo, passport_number passport, " +
                " a.CIF_ID custID from VEHICLE_LOAN_APPLICANTS a " +
                "JOIN VEHICLE_LOAN_KYC k ON a.applicant_id=k.applicant_id " +
                "where a.applicant_id=? and a.del_flg='N' and k.del_flg='N'";
        return jdbcTemplate.queryForObject(sql, new Object[]{applicantId}, new LosDedupeRequestDTORowMapper());
    }


    public ScoreInfo getColorForScore(String tableName, Long score,String program) {
        log.info("DK Response validation started tableName {} experian score {} program {}",tableName,score,program);
        String sql = "SELECT COLOR, START_SCORE, END_SCORE FROM " + tableName + " WHERE ? BETWEEN START_SCORE AND END_SCORE" + " AND DEL_FLAG = 'N'" + " AND PROGRAM_NAME = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{score, program}, (rs, rowNum) -> {
                String color = rs.getString("COLOR");
                double startScore = rs.getDouble("START_SCORE");
                double endScore = rs.getDouble("END_SCORE");
                String scoreRange = startScore + "-" + endScore;
                return new ScoreInfo(color, scoreRange);
            });
        } catch (Exception e) { // Log the error
            log.error("Error fetching color for score {} from table {}: {}", score, tableName, e.getMessage());
            // Return a default color or null
            return new ScoreInfo("UNKNOWN", "N/A");
        }
    }
    public ScoreInfo getColorForScoreNew(String tabel_name, Long racescore,String program,Long bureauscore,String model_type) {
        log.info("DK Response validation started tableName {} Race score {} program {} Experian Score {} Model {}",tabel_name,racescore,program,bureauscore,model_type);
        if(program.equals("LOANFD")){
            return new ScoreInfo("green", "0-1000");
        }else {
            if (tabel_name.equals("VLBUREAURACEBREMAP@mybank")) {
                String sqlbureau = "", sqlrace = "", sql = "";
                String bureau_slno = "";
                String race_slno = "";
                sqlbureau = "SELECT slno FROM VLBUREAUSLABBREMAS@mybank WHERE " +
                        " MODEL_TYPE= ? AND ? BETWEEN START_SCORE AND END_SCORE " +
                        "AND  DEL_FLAG = 'N'";
                try {
                    bureau_slno = jdbcTemplate.queryForObject(sqlbureau, new Object[]{model_type, bureauscore}, String.class);
                } catch (EmptyResultDataAccessException e) {
                    log.error("Exception while fetching slab for experian score {} from VLBUREAUSLABBREMAS table to Model {} . Exception is  {} ", bureauscore, model_type, e.getMessage());
                    return new ScoreInfo("UNKNOWN", "N/A");
                }
                sqlrace = "SELECT slno FROM VLRACESLABBREMAS@mybank WHERE " +
                        " MODEL_TYPE= ? AND ? BETWEEN START_SCORE AND END_SCORE " +
                        "AND  DEL_FLAG = 'N'";
                try {
                    race_slno = jdbcTemplate.queryForObject(sqlrace, new Object[]{model_type, racescore}, String.class);
                } catch (EmptyResultDataAccessException e) {
                    log.error("Exception while fetching slab for race score {} from VLRACESLABBREMAS table to Model {} . Exception is  {} ", racescore, model_type, e.getMessage());
                    return new ScoreInfo("UNKNOWN", "N/A");
                }
                log.info("Bureau SLNO  {} Race SLNO {} ",bureau_slno,race_slno);
                Integer count = 0;
                if (!race_slno.equals("") && !bureau_slno.equals("")) {
                    sql = "SELECT count(*)  FROM VLBUREAURACEBREMAP@mybank WHERE " +
                            "MODEL_TYPE= ? AND BUREAU_SLNO =? and RACE_SLNO =? and DEL_FLAG = 'N'";
                    try {
                        count = jdbcTemplate.queryForObject(sql, new Object[]{model_type, bureau_slno, race_slno}, Integer.class);
                    } catch (
                            EmptyResultDataAccessException e) {
                        log.error("Exception while fetching slno for two slabs (both race slno : {}  and  bureau slno {} )  from VLBUREAURACEBREMAP table to Model {} . Exception is  {} ", race_slno, bureau_slno, model_type, e.getMessage());
                        count = 0;
                    }
                    try {
                        if (count > 0) {
                            String sqlbureauNew = "SELECT COLOR,BUREAU_DATA,RACE_DATA FROM VLBUREAURACEBREMAP@mybank WHERE " +
                                    "MODEL_TYPE= ? AND BUREAU_SLNO =? and RACE_SLNO =? and DEL_FLAG = 'N'";
                            try {
                                return jdbcTemplate.queryForObject(sqlbureauNew, new Object[]{model_type, bureau_slno, race_slno}, (rs, rowNum) -> {
                                    String color = rs.getString("COLOR");
                                    String RACE_DATA = rs.getString("RACE_DATA");
                                    String BUREAU_DATA = rs.getString("BUREAU_DATA");
                                    String scoreRange = "BUREAU_DATA:" + BUREAU_DATA + ", RACE_DATE:" + RACE_DATA;
                                    return new ScoreInfo(color, scoreRange);
                                });
                            } catch (Exception e) { // Log the error
                                log.error("Exception while fetching Colour for two slabs (both race slno : {}  and  bureau slno {} )  from VLBUREAURACEBREMAP@mybank table to Model {} . Exception is  {} ", race_slno, bureau_slno, model_type, e.getMessage());
                                return new ScoreInfo("UNKNOWN", "N/A");
                            }
                        } else {
                            return new ScoreInfo("UNKNOWN", "N/A");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("Exception while fetching Colour for two slabs (both race slno : {}  and  bureau slno {} )  from VLBUREAURACEBREMAP@mybank table to Model {} . Exception is  {} ", race_slno, bureau_slno, model_type, e.getMessage());
                        return new ScoreInfo("UNKNOWN", "N/A");
                    }
                }
            } else {
                String sql = "SELECT COLOR, START_SCORE, END_SCORE FROM " + tabel_name + " WHERE ? BETWEEN START_SCORE AND END_SCORE" + " AND DEL_FLAG = 'N'" + " AND PROGRAM_NAME = ?";
                try {
                    return jdbcTemplate.queryForObject(sql, new Object[]{racescore, program}, (rs, rowNum) -> {
                        String color = rs.getString("COLOR");
                        double startScore = rs.getDouble("START_SCORE");
                        double endScore = rs.getDouble("END_SCORE");
                        String scoreRange = startScore + "-" + endScore;
                        return new ScoreInfo(color, scoreRange);
                    });
                } catch (Exception e) { // Log the error
                    log.error("Error fetching color for score {} from table {}: {}", racescore, tabel_name, e.getMessage());
                    // Return a default color or null
                    return new ScoreInfo("UNKNOWN", "N/A");
                }
            }
        }
        return new ScoreInfo("UNKNOWN", "N/A");
    }
}
