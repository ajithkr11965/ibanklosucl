package com.sib.ibanklosucl.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UIDRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public String getUID(String UIDrefNo) {

        try {
            String sql = "select  custom.getaadhar_no@finacle10(:uid) from dual";
            // String sql = "select  'XXXXXX' from dual";
            //String sql = "select  '888844442222' from dual";

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("uid", UIDrefNo);
            String response = namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
            return "ENTRY NOT FOUND".equals(response) ? "" : response;
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }


    }
    public String getUIDRefNo(String UID) {

        try {
            String sql = "select  custom.getaadhar_ref_no@finacle10(:uid) from dual";
            //String sql = "select  'XXXXXX' from dual";
            ///String sql = "select  custom.getaadhar_ref_no@finacle10(:uid) from dual";
            // String sql = "select  'XXXXXX' from dual";
          //  String sql = "select  'JJOT6FTN024TGVOJJK' from dual";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("uid", UID);
            String response = namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
            return "ENTRY NOT FOUND".equals(response) ? "" : response;
        }
        catch (Exception e){
            throw new RuntimeException("Unable to Obtain Aadhaar Reference Number");
        }
    }

}
