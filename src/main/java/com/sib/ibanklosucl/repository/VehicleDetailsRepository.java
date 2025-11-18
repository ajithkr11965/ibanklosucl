package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.DealerCodeResponse;
import com.sib.ibanklosucl.model.DealerNameResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class VehicleDetailsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, String>> findAllStates() {
        String sql = "SELECT DISTINCT state_name FROM vlcdcity";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("state_name", rs.getString("state_name"));
            return map;
        });
    }

    public List<Map<String, String>> findCitiesByState(String stateCode) {
        String sql = "SELECT CITY_ID, city_name FROM vlcdcity WHERE state_name = ?";
        return jdbcTemplate.query(sql, new Object[]{stateCode}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("city_id", rs.getString("CITY_ID"));
            map.put("city_name", rs.getString("city_name"));
            return map;
        });
    }


    public List<Map<String, String>> findAllMakes() {
        String sql = "SELECT MAKE_ID, MAKE_NAME FROM VLCDMAKE WHERE STATUS='Y' AND DEL_FLG='N'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("make_id", rs.getString("MAKE_ID"));
            map.put("make_name", rs.getString("MAKE_NAME"));
            return map;
        });
    }

    public List<Map<String, String>> findModelsByMake(String makeId) {
        String sql = "SELECT MODEL_ID, MODEL_NAME, IMAGE_BASE64 FROM VLCDMODEL WHERE MAKE_ID = ? AND STATUS='Y' AND DEL_FLG='N'";
        return jdbcTemplate.query(sql, new Object[]{makeId}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("model_id", rs.getString("MODEL_ID"));
            map.put("model_name", rs.getString("MODEL_NAME"));
            map.put("image_base64", rs.getString("IMAGE_BASE64"));
            return map;
        });
    }

    public List<Map<String, String>> findVariantsByModel(String modelId) {
        String sql = "SELECT VARIANT_ID, VARIANT_NAME, FUEL_TYPE, TRANSMISSION FROM VLCDVARIANT WHERE MODEL_ID = ? AND STATUS='Y' AND DEL_FLG='N'";
        return jdbcTemplate.query(sql, new Object[]{modelId}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("variant_id", rs.getString("VARIANT_ID"));
            map.put("variant_name", rs.getString("VARIANT_NAME"));
            map.put("fuel_type", rs.getString("FUEL_TYPE"));
            map.put("transmission", rs.getString("TRANSMISSION"));
            return map;
        });
    }

    public List<Map<String, String>> findPricesByVariantAndCity(String variantId, String cityId) {
        String sql = "SELECT PRICE_ID, EX_SHOWROOM, INSURANCE, RTO, OTHER_PRICE, ONROAD_PRICE, EXTENDED_WARRANTY FROM VLCDPRICE WHERE VARIANT_ID = ? AND CITY_ID = ? AND DEL_FLG='N'";
        return jdbcTemplate.query(sql, new Object[]{variantId, cityId}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("price_id", rs.getString("PRICE_ID"));
            map.put("ex_showroom", rs.getString("EX_SHOWROOM"));
            map.put("insurance", rs.getString("INSURANCE"));
            map.put("rto", rs.getString("RTO"));
            map.put("other", rs.getString("OTHER_PRICE"));
            map.put("onroad_price", rs.getString("ONROAD_PRICE"));
            map.put("extended_warranty", rs.getString("EXTENDED_WARRANTY"));
            return map;
        });
    }

//    public List<Map<String, String>> findAllDealers() {
//        String sql = "SELECT DISTINCT DEALER_NAME FROM RODEALERMAP@mybank WHERE DEALER_NAME IS NOT NULL";
//        return jdbcTemplate.query(sql, (rs, rowNum) -> {
//            Map<String, String> map = new HashMap<>();
//            map.put("dealer_name", rs.getString("dealer_name"));
//            return map;
//        });
//    }
    public List<Map<String, String>> findAllDealers() {
        String sql = "SELECT DISTINCT dealer_name, dealer_code FROM dealermas@mybank WHERE del_flg='N'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("dealer_name", rs.getString("dealer_name"));
            map.put("dealer_code", rs.getString("dealer_code"));
            return map;
        });
    }

    public List<Map<String, String>> findLocationsByDealerInfo(String dealerSubcode, String dealerCode, String dealerName) {
        String sql = "SELECT  distinct b.city_name, b.state, b.city_id FROM dealermas@mybank a, dealersubmas@mybank b " +
                "WHERE a.slno=b.slno AND a.DEL_FLG='N' AND b.del_flg='N' AND a.DEALER_NAME IS NOT NULL " +
                "AND a.dealer_sub_code=? AND a.dealer_code=? AND TRIM(a.dealer_name)=? ";
        return jdbcTemplate.query(sql, new Object[]{dealerSubcode, dealerCode, dealerName}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("city_name", rs.getString("city_name"));
            map.put("state_name", rs.getString("state"));
            map.put("city_id", rs.getString("city_id"));
            return map;
        });
    }

    public List<Map<String, String>> findMakesByDealerAndLocation(String dealerSubcode, String dealerCode, String dealerName, String cityId) {
        String sql = "SELECT distinct b.oem_id makeid, b.oem makename ,b.state FROM dealermas@mybank a, dealersubmas@mybank b " +
                "WHERE a.slno=b.slno AND a.DEL_FLG='N' AND b.del_flg='N' AND a.DEALER_NAME IS NOT NULL " +
                "AND a.dealer_sub_code=? AND a.dealer_code=? AND TRIM(a.dealer_name)=? AND b.city_id=?";
        return jdbcTemplate.query(sql, new Object[]{dealerSubcode, dealerCode, dealerName, cityId}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("make_id", rs.getString("makeid"));
            map.put("make_name", rs.getString("makename"));
            map.put("state_name", rs.getString("state"));
            return map;
        });
    }


    public DealerCodeResponse getDealerCodes(String dealerName) {
        DealerCodeResponse response = new DealerCodeResponse();

        //  Fetch dealer Name as text
        String dealerCodeQuery = "SELECT DISTINCT Dealer_Code AS DEALERCODE FROM dealermas@mybank WHERE TRIM(DEALER_NAME) = ? and del_flg='N'";
        String dealerCode = jdbcTemplate.queryForObject(dealerCodeQuery, new Object[]{dealerName}, String.class);
        response.setDealerCode(dealerCode);

        // Fetch distinct DST codes
        String dstCodesQuery = "select distinct DST_CODE as id,DST_NAME as name FROM Dstmas@mybank where del_flg='N' and reg_code in (select reg_code  from misreg@mybank where state_code in (select distinct(state_code) from misreg@mybank where reg_code in (select distinct(reg_code) from dealermas@mybank where del_flg='N' and TRIM(dealer_name)=? and dealer_code=?)) and reg_code like'9%') ";
         //dstCodesQuery = "SELECT distinct a.DST_CODE as id,a.DST_NAME as name FROM Dstmas@mybank a,dealermas@mybank b where a.DEL_FLG='N' and b.del_flg='N' and a.reg_code=b.reg_code and b.dealer_name= ? and b.dealer_code=?";
        List<Map<String, Object>> dstCodes = jdbcTemplate.queryForList(dstCodesQuery,dealerName,dealerCode);
        response.setDstCodes(dstCodes);

        // Fetch distinct DSA codes
        //String dsaCodesQuery = "SELECT DSA_SUB_CODE AS id , DSA_NAME AS name FROM DSAMAS@Mybank where DEL_FLG='N'";
        String dsaCodesQuery = "SELECT distinct a.DSA_SUB_CODE as id,a.DSA_NAME as name FROM Dsamas@mybank a,dealermas@mybank b where a.DEL_FLG='N' and b.del_flg='N' and a.reg_code=b.reg_code and TRIM(b.dealer_name)= ? and b.dealer_code=?";
        List<Map<String, Object>> dsaCodes = jdbcTemplate.queryForList(dsaCodesQuery,dealerName,dealerCode);
        response.setDsaCodes(dsaCodes);

        // Fetch dealer sub codes
        String dealerSubCodesQuery = "SELECT DISTINCT Dealer_SUB_CODE AS id, Dealer_SUB_CODE AS name  FROM dealermas@mybank WHERE TRIM(DEALER_NAME) = ? and DEALER_CODE= ? AND del_flg='N'";
        List<Map<String, Object>> dealerSubCodes = jdbcTemplate.queryForList(dealerSubCodesQuery, dealerName,dealerCode);
        response.setDealerSubCodes(dealerSubCodes);

        return response;
    }


    public DealerNameResponse getDealerNames(String dealerName,String dealerCode) {
        DealerNameResponse response = new DealerNameResponse();

        // Fetch distinct DST codes
        String dstCodesQuery = "select distinct DST_CODE as id,DST_NAME as name FROM Dstmas@mybank where del_flg='N' and reg_code in (select reg_code  from misreg@mybank where state_code in (select distinct(state_code) from misreg@mybank where reg_code in (select distinct(reg_code) from dealermas@mybank where del_flg='N' and TRIM(dealer_name)=? and dealer_code=?)) and reg_code like'9%')  ";
            //dstCodesQuery = "SELECT distinct a.DST_CODE as id,a.DST_NAME as name FROM Dstmas@mybank a,dealermas@mybank b where a.DEL_FLG='N' and b.del_flg='N' and a.reg_code=b.reg_code and b.dealer_name= ? and b.dealer_code=?";
        List<Map<String, Object>> dstCodes = jdbcTemplate.queryForList(dstCodesQuery,dealerName,dealerCode);
        response.setDstCodes(dstCodes);

        // Fetch distinct DSA codes
        //String dsaCodesQuery = "SELECT DSA_SUB_CODE AS id , DSA_NAME AS name FROM DSAMAS@Mybank where DEL_FLG='N'";
        String dsaCodesQuery = "SELECT distinct a.DSA_SUB_CODE as id,a.DSA_NAME as name FROM Dsamas@mybank a,dealermas@mybank b where a.DEL_FLG='N' and b.del_flg='N' and a.reg_code=b.reg_code and TRIM(b.dealer_name)= ? and b.dealer_code=?";
        List<Map<String, Object>> dsaCodes = jdbcTemplate.queryForList(dsaCodesQuery,dealerName,dealerCode);
        response.setDsaCodes(dsaCodes);

        // Fetch dealer sub codes
        String dealerSubCodesQuery = "SELECT DISTINCT Dealer_SUB_CODE AS id, Dealer_SUB_CODE AS name  FROM dealermas@mybank WHERE TRIM(DEALER_NAME) = ? and DEALER_CODE= ? AND del_flg='N'";
        List<Map<String, Object>> dealerSubCodes = jdbcTemplate.queryForList(dealerSubCodesQuery, dealerName,dealerCode);
        response.setDealerSubCodes(dealerSubCodes);

        return response;
    }
}

