package com.sib.ibanklosucl.repository.program;

import com.sib.ibanklosucl.dto.program.bsaBankDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BsaBankDetailsRepository {
    private final JdbcTemplate jdbcTemplate;

    public BsaBankDetailsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<bsaBankDetails> getBankDetails() {
                String query = "SELECT DISTINCT(INSTITUTION_ID), NAME FROM BSA_BANK_DETAILS ORDER BY " +
                       "CASE WHEN NAME = 'South Indian Bank, India' THEN 0 ELSE 1 END, NAME";
        //String query = "SELECT DISTINCT(INSTITUTION_ID),NAME FROM BSA_BANK_DETAILS ORDER BY 2";
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            bsaBankDetails bankdetails = new bsaBankDetails();
            bankdetails.setInstitutionId(rs.getString("INSTITUTION_ID"));
            bankdetails.setName(rs.getString("NAME"));
            return bankdetails;
        });
    }
}

