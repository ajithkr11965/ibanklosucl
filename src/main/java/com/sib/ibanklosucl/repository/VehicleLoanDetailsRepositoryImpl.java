package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.service.VehicleLoanProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VehicleLoanDetailsRepositoryImpl {


    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private VehicleLoanProgramService VehicleLoanProgramservice;

    public boolean checkTenor(String P_Name, String Emp_name, int tenor)
    {
        String sql = "SELECT count(*) FROM vltenureeligiblemas@mybank WHERE " +
                "PROGRAM_NAME = :programName " +
                "AND (EMPLOYMENT_NAME = :employmentName OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("programName", P_Name)
                .addValue("employmentName", Emp_name);

        int count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);

        if (count > 0) {
            sql = "SELECT count(*) FROM vltenureeligiblemas@mybank WHERE " +
                    "PROGRAM_NAME = :programName " +
                    "AND :tenor BETWEEN MIN_TENURE AND MAX_TENURE " +
                    "AND (EMPLOYMENT_NAME = :employmentName OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";

            params.addValue("loanAmount", tenor);
            count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        } else {
            return true;
        }
        return count > 0;
    }

    public String getViewDataTenorLimit(String P_Name, String Emp_name, int tenor) {
        String sql = "SELECT VIEW_DATA FROM vltenureeligiblemas@mybank WHERE " +
                "PROGRAM_NAME = :programName " +
                "AND (EMPLOYMENT_NAME = :employmentName OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("programName", P_Name)
                .addValue("employmentName", Emp_name)
                .addValue("loanAmount", tenor);

        return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
    }


    public boolean checkLoanAmount(String P_Name, String Emp_name, Float LoanAmount) {
        String sql = "SELECT count(*) FROM vlloanamounteligiblemas@mybank WHERE " +
                "PROGRAM_NAME = :programName " +
                "AND (EMPLOYMENT_NAME = :employmentName OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("programName", P_Name)
                .addValue("employmentName", Emp_name);

        int count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);

        if (count > 0) {
            sql = "SELECT count(*) FROM vlloanamounteligiblemas@mybank WHERE " +
                    "PROGRAM_NAME = :programName " +
                    "AND :loanAmount BETWEEN MIN_AMT AND MAX_AMT " +
                    "AND (EMPLOYMENT_NAME = :employmentName OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";

            params.addValue("loanAmount", LoanAmount);
            count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        } else {
            return true;
        }
        return count > 0;
    }

    public String getViewDataAmtLimit(String P_Name, String Emp_name, Float LoanAmount) {
        String sql = "SELECT VIEW_DATA FROM vlloanamounteligiblemas@mybank WHERE " +
                "PROGRAM_NAME = :programName " +
                "AND (EMPLOYMENT_NAME = :employmentName OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("programName", P_Name)
                .addValue("employmentName", Emp_name)
                .addValue("loanAmount", LoanAmount);

        return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
    }

    public boolean isApplicantNRI(String wiNum, String slno) {
        String sql = "SELECT count(*) FROM VEHICLE_LOAN_APPLICANTS vla,VEHICLE_LOAN_PROGRAM vlp WHERE vla.RESIDENT_FLG = 'N' AND vla.APPLICANT_TYPE in ('A','C') AND vla.DEL_FLG ='N' AND vla.slno = vlp.slno AND vla.WI_NUM  = vlp.WI_NUM AND vlp.INCOME_CONSIDERED = 'Y' and vla.wi_num = ? and vla.wi_num = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{wiNum, slno}, Integer.class) > 0;
    }

    public boolean isProgramSet(String wiNum, String slno) {
        return VehicleLoanProgramservice.validateProgram(wiNum, Long.valueOf(slno));
    }

    public boolean isBureauValid(String wiNum, String slno) {
        String sql = "SELECT count(*) FROM VEHICLE_LOAN_APPLICANTS vla,VEHICLE_LOAN_PROGRAM vlp WHERE vla.slno = vlp.slno AND vla.WI_NUM  = vlp.WI_NUM AND vlp.INCOME_CONSIDERED = 'Y' and vla.wi_num = ? and vla.wi_num = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{wiNum, slno}, Integer.class) > 0;
    }


}
