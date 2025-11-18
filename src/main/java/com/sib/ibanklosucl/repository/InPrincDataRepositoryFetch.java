package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.dto.SanctionChargeDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class InPrincDataRepositoryFetch {

    private final JdbcTemplate jdbcTemplate;

    public InPrincDataRepositoryFetch(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public SanctionChargeDTO getSanctionCharge(String pgm, String chargecode) {
        String query = "select c.CHARGE_CODE,c.CHARGE_NAME,STATIC_FIXED,PERCANTAGE,LOAN_AMOUNT_PERCANTAGE,VEHICLE_PRICE_PERCANTAGE,OTHERS_PERCANTAGE,VALUE,MAXIMUM_LIMIT,MAXIMUM_VALUE, c.FREQUENCY\n" +
                "from vlchargemas@mybank c join vlcharge@mybank m on m.del_flag='N' and m.CHARGE_CODE=c.CHARGE_CODE \n" +
                "AND m.PROGRAM_NAME=c.PROGRAM_NAME     WHERE  c.DEL_FLAG='N' \n" +
                "AND c.PROGRAM_NAME =? AND c.CHARGE_CODE=?";

        return jdbcTemplate.queryForObject(query, new Object[]{pgm,chargecode}, (rs, rowNum) -> {
            SanctionChargeDTO chargedetails = new SanctionChargeDTO();
            chargedetails.setChargeCode(rs.getString("CHARGE_CODE"));
            chargedetails.setChargeName(rs.getString("CHARGE_NAME"));
            chargedetails.setStaticFixed(rs.getString("STATIC_FIXED"));
            chargedetails.setPercentage(rs.getString("PERCANTAGE"));
            chargedetails.setLoanAmountPercentage(rs.getString("LOAN_AMOUNT_PERCANTAGE"));
            chargedetails.setVehiclePricePercentage(rs.getString("VEHICLE_PRICE_PERCANTAGE"));
            chargedetails.setOthersPercentage(rs.getString("OTHERS_PERCANTAGE"));
            chargedetails.setValue(rs.getDouble("VALUE"));
            chargedetails.setMaximumLimit(rs.getString("MAXIMUM_LIMIT"));
            chargedetails.setMaximumValue((long) rs.getInt("MAXIMUM_VALUE"));
            chargedetails.setFrequency(rs.getString("FREQUENCY"));
            return chargedetails;
        });
    }
}
