package com.sib.ibanklosucl.repository.program;

import com.sib.ibanklosucl.dto.program.FDAccountDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
@Slf4j
public class FDAccountRepository {
    private final JdbcTemplate jdbcTemplate;

    public FDAccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


            public List<FDAccountDetails> findAccountDetails(String customerId) {
        String query = "select decode(sign((least(drwng_power, sanct_lim) + clr_bal_amt + dacc_lim + ffd_contrib_to_acct - system_reserved_amt - system_gen_lim - gam.lien_amt) *(select fcrate@finacle10(acct_crncy_code, sysdate, 'NOR') from dual)), -1, 0, 0, 0, (least(drwng_power, sanct_lim) + clr_bal_amt + dacc_lim + ffd_contrib_to_acct - system_reserved_amt - system_gen_lim - gam.lien_amt) * (select fcrate@finacle10(acct_crncy_code, sysdate, 'NOR') from dual)) fd_amount, foracid , acct_name , decode(nvl(acct_cls_flg, 'N'), 'N', 'ACTIVE', 'NONACTIVE') status , deposit_amount * (SELECT fcrate@finacle10(gam.acct_crncy_code, open_effective_date, 'NOR') FROM DUAL) amount, aas.acct_poa_as_rec_type , to_char(ACCT_OPN_DATE, 'dd-mm-yyyy') ACCT_OPN_DATE, to_char(MATURITY_DATE, 'dd-mm-yyyy') MATURITY_DATE, DEPOSIT_AMOUNT, MATURITY_AMOUNT, gam.acid,getfsld_dep_liab_check@finacle10(foracid)FSLD_ADJ from tbaadm.tam@finacle10, tbaadm.gam@finacle10, tbaadm.alt@finacle10, tbaadm.aas@finacle10 where gam.acid = tam.acid and gam.acid = alt.acid(+) and gam.schm_type = 'TDA' and gam.acct_CrNCY_code = 'INR' AND gam.SCHM_CODE NOT LIKE 'FFD%' AND gam.GL_SUB_HEAD_CODE NOT IN (SELECT GL_CODE FROM custom.c_matrix@finacle10 WHERE GROUP_CODE='TAXGN' AND CLOSED_GL='N' AND del_flg='N') and aas.cust_id =? and gam.acid = aas.acid and aas.acct_poa_as_rec_type IN ('J', 'M') and aas.del_flg != 'Y' and acct_cls_flg != 'Y'";

        try {
            List<FDAccountDetails> accountDetailsList = jdbcTemplate.query(query, new Object[]{customerId}, (rs, rowNum) -> {
                FDAccountDetails details = new FDAccountDetails();
                details.setFdAmount(rs.getBigDecimal("fd_amount"));
                details.setFdAccNo(rs.getString("FORACID"));
                details.setAccountName(rs.getString("acct_name"));
                details.setFdStatus(rs.getString("STATUS"));
                details.setDepositAmount(rs.getBigDecimal("DEPOSIT_AMOUNT"));
                details.setFsldAdjAmount(rs.getBigDecimal("FSLD_ADJ"));
                BigDecimal eligibleFDAmount = rs.getBigDecimal("fd_amount").subtract(rs.getBigDecimal("FSLD_ADJ"));
                details.setEligFDAmount(eligibleFDAmount);
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

                Date utilOpenDt = null;
                Date utilMatDate = null;
                try {
                    utilOpenDt = formatter.parse(rs.getString("ACCT_OPN_DATE"));
                    utilMatDate = formatter.parse(rs.getString("MATURITY_DATE"));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                details.setAccountOpenDate(utilOpenDt);
                details.setMaturityDate(utilMatDate);
                details.setAcid(rs.getString("ACID"));
                details.setMaturityAmount(rs.getBigDecimal("MATURITY_AMOUNT"));

                // Additional logic to set account type and other details
                String fdDetailsQuery = "select listagg(aas.cust_id, ',') within group (order by aas.cust_id) from tbaadm.aas@finacle10 where aas.acid in (?)";
                String fdDetails = jdbcTemplate.queryForObject(fdDetailsQuery, new Object[]{details.getAcid()}, String.class);

                if (fdDetails != null && !fdDetails.isEmpty()) {
                    String[] fdDetailsArray = fdDetails.split(",");
                    String accountType = fdDetailsArray.length > 1 ? "Joint" : "Main";
                    details.setAccountType(accountType);
                    details.setCifIds(fdDetails);
                }

                return details;
            });

            return accountDetailsList;
        } catch (EmptyResultDataAccessException exception) {
            return List.of();  // Returning an empty list if no results are found
        }
    }


     public List<String> findFDAccountNumbersByCustomerId(String customerId) {
        String query = "SELECT foracid FROM tbaadm.gam@finacle10 WHERE CUST_ID = ? AND SCHM_TYPE = 'TDA' AND ACCT_CLS_FLG = 'N'";
        return jdbcTemplate.queryForList(query, String.class, customerId);
    }
}
