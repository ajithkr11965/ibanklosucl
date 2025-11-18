package com.sib.ibanklosucl.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Repository
public class VehicleEligiblityRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;



    public List<Map<String, String>> findscoredata(String pgm) {
        String sql = "SELECT SCOREVIEW,COLOR FROM VLCOLOURSLABMAS@mybank WHERE " +
                "PROGRAM_NAME = ? AND DEL_FLAG = 'N' and color in ('red')";
        return jdbcTemplate.query(sql, new Object[]{pgm}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("COLOR", rs.getString("COLOR"));
            map.put("SCOREVIEW", rs.getString("SCOREVIEW"));
            return map;
        });
    }

    public List<Map<String, String>> finddpddata(String pgm,String emp,String queue) {
        String sql ="";
        if(queue.equals("BC")){
            sql = "SELECT DURATION,MIN_DPD_DAYS,MAX_DPD_DAYS FROM VLDPDDAYSBRESLABMAS@mybank WHERE " +
                    "PROGRAM_NAME = ? AND DEL_FLAG = 'N'  AND ( EMPLOYMENT_NAME= ? OR EMPLOYMENT_NAME = 'All') ";
        }else{
            sql = "SELECT DURATION,MIN_DPD_DAYS,MAX_DPD_DAYS FROM VLDPDDAYSELIGIBLEMAS@mybank WHERE " +
                    "PROGRAM_NAME = ? AND DEL_FLAG = 'N'  AND ( EMPLOYMENT_NAME= ? OR EMPLOYMENT_NAME = 'All') ";
        }
        return jdbcTemplate.query(sql, new Object[]{pgm, emp}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("DURATION", rs.getString("DURATION"));
            map.put("MIN_DPD_DAYS", rs.getString("MIN_DPD_DAYS"));
            map.put("MAX_DPD_DAYS", rs.getString("MAX_DPD_DAYS"));
            return map;
        });
    }
    public String findEligibleMinIncomecolour(String pgm,String emp,String income) throws Exception {
        String color="",view_data="";
        JSONObject incomedata = new JSONObject();
        String sql = "SELECT MIN_ELIGIBLE_AMOUNT FROM VLINCELIGIBLEMAS@mybank WHERE " +
                "PROGRAM_NAME = ? AND ( EMPLOYMENT_NAME= ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
        try {
            view_data = jdbcTemplate.queryForObject(sql, new Object[]{pgm, emp}, String.class);
        }catch(EmptyResultDataAccessException e){
            view_data="";
        }
        try{
            if (view_data != null && !view_data.equals("")) {
                if(income == null || income.equals("") || income.isEmpty())
                    color = "red";
                else {
                    BigDecimal loanAmount= new BigDecimal(income);
                    sql = "SELECT COUNT(*) FROM VLINCELIGIBLEMAS@mybank WHERE " +
                            "PROGRAM_NAME = ? AND ? >= MIN_ELIGIBLE_AMOUNT " +
                            "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
                    Integer count = jdbcTemplate.queryForObject(sql, new Object[]{pgm, loanAmount, emp}, Integer.class);
                    if (count > 0) {
                        color = "green";
                    } else {
                        color = "red";
                    }
                }
            } else {
                color = "green";
                view_data = "No Data";
            }
        }catch(Exception e){
            e.printStackTrace();
            color = "red";
            view_data="Error";
        }
        incomedata.put("currentValue", income);
        incomedata.put("color", color);
        incomedata.put("masterValue", view_data);
        return incomedata.toString();
    }

    public String findEligibletenurecolour(String pgm,String emp,String tenure) {
        String color="",view_data="";
        JSONObject tenuredata = new JSONObject();
        String sql = "SELECT VIEW_DATA FROM VLTENUREELIGIBLEMAS@mybank WHERE " +
                "PROGRAM_NAME = ? AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
        try {
            view_data = jdbcTemplate.queryForObject(sql, new Object[]{pgm, emp}, String.class);
        }catch(EmptyResultDataAccessException e){
            view_data="";
        }
        try{
            if (view_data != null && !view_data.equals("")) {
                if(tenure == null  || tenure.equals("") || tenure.isEmpty() )
                    color = "red";
                else {
                    sql = "SELECT COUNT(*) FROM VLTENUREELIGIBLEMAS@mybank  WHERE " +
                            "PROGRAM_NAME = ? " +
                            "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N' AND MIN_TENURE<=? and MAX_TENURE>=?";
                    int count = jdbcTemplate.queryForObject(sql, new Object[]{pgm, emp, Integer.parseInt(tenure), Integer.parseInt(tenure)}, Integer.class);
                    if (count > 0) {
                        color = "green";
                    } else {
                        color = "red";
                    }
                }
            } else {
                color = "green";
                view_data = "No Data";
            }
        }catch(Exception e){
            e.printStackTrace();
            color = "red";
            view_data="Error";
        }
        tenuredata.put("currentValue", tenure);
        tenuredata.put("color", color);
        tenuredata.put("masterValue", view_data);
        return tenuredata.toString();
    }

    public String findEligibleageecolour(String pgm,String emp,BigDecimal age) throws Exception {
        String color="",view_data="";
        JSONObject agedata = new JSONObject();
        String sql = "SELECT VIEW_DATA FROM VLAGEELIGIBLEMAS@mybank WHERE " +
                "PROGRAM_NAME = ? AND (EMPLOYMENT_NAME= ? OR EMPLOYMENT_NAME= 'All') AND DEL_FLAG = 'N'";
        try {
            view_data = jdbcTemplate.queryForObject(sql, new Object[]{pgm, emp}, String.class);
        }catch(EmptyResultDataAccessException e){
            view_data="";
        }
        try{
            if (view_data != null && !view_data.equals("")) {
                if(age.compareTo(BigDecimal.ZERO) == 0 )
                    color = "red";
                else {
                    sql = "SELECT COUNT(*) FROM VLAGEELIGIBLEMAS@mybank WHERE " +
                            "PROGRAM_NAME = ? AND ? BETWEEN MIN_AGE AND MAX_AGE " +
                            "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
                    int count = jdbcTemplate.queryForObject(sql, new Object[]{pgm, age, emp}, Integer.class);
                    if (count > 0) {
                        color = "green";
                    } else {
                        color = "red";
                    }
                }
            } else {
                color = "green";
                view_data = "No Data";
            }
        }catch(Exception e){
            e.printStackTrace();
            color = "red";
            view_data="Error";
        }
        agedata.put("currentValue", age.intValue());
        agedata.put("color", color);
        agedata.put("masterValue", view_data);
        return agedata.toString();
    }
    public String findEligibleminageecolour(String pgm,String emp,int age) throws Exception {
        String color = "", view_data = "";
        JSONObject minagedata = new JSONObject();
        String sql = "SELECT MIN_AGE FROM VLMINAGEELIGIBLEMAS@mybank WHERE " +
                "PROGRAM_NAME = ? AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
        try {
            view_data = jdbcTemplate.queryForObject(sql, new Object[]{pgm, emp}, String.class);
        } catch (EmptyResultDataAccessException e) {
            view_data = "";
        }
        try{
            if (view_data != null && !view_data.equals("")) {
                if (age == 0)
                    color = "red";
                else {
                    sql = "SELECT COUNT(*) FROM VLMINAGEELIGIBLEMAS@mybank WHERE " +
                            "PROGRAM_NAME = ? AND ? >= MIN_AGE " +
                            "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
                    int count = jdbcTemplate.queryForObject(sql, new Object[]{pgm, age, emp}, Integer.class);
                    if (count > 0) {
                        color = "green";
                    } else {
                        color = "red";
                    }
                }
            } else {
                color = "green";
                view_data = "No Data";
            }
        }catch(Exception e){
                e.printStackTrace();
                color = "red";
                view_data="Error";
        }
        minagedata.put("currentValue", age);
        minagedata.put("color", color);
        minagedata.put("masterValue", view_data);
        return minagedata.toString();
    }
    public String findEligibleLoanAmountcolour(String pgm,String emp,String loanamt) throws Exception {
        String color = "", view_data = "";

        JSONObject loandata = new JSONObject();
        String sql = "SELECT VIEW_DATA FROM VLLOANAMOUNTELIGIBLEMAS@mybank WHERE " +
                "PROGRAM_NAME = ? AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
        try {
            view_data = jdbcTemplate.queryForObject(sql, new Object[]{pgm, emp}, String.class);
        } catch (EmptyResultDataAccessException e) {
            view_data = "";
        }
        try{
            if (view_data != null && !view_data.equals("")) {
                if (loanamt == null || loanamt.equals("") || loanamt.equals("null") || loanamt.isEmpty()) {
                    color = "red";
                }else {
                    BigDecimal loanAmount= new BigDecimal(loanamt);
                    sql = "SELECT COUNT(*) FROM VLLOANAMOUNTELIGIBLEMAS@mybank WHERE " +
                            "PROGRAM_NAME = ? AND ? BETWEEN MIN_AMT AND MAX_AMT " +
                            "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
                    Integer count = jdbcTemplate.queryForObject(sql, new Object[]{pgm, loanAmount, emp}, Integer.class);
                    if (count > 0) {
                        color = "green";
                    } else {
                        color = "red";
                    }
                }
            } else {
                color = "green";
                view_data = "No Data";
            }
        }catch(Exception e){
            e.printStackTrace();
            color = "red";
            view_data="Error";
        }
        loandata.put("currentValue", loanamt);
        loandata.put("color", color);
        loandata.put("masterValue", view_data);
        return loandata.toString();
    }
    public String findEligibleemploymentcolour(String pgm,String emp,String totalemp) throws Exception {
        String color="",view_data="";
        JSONObject totalempdata = new JSONObject();
        String sql = "SELECT  MIN_TOTAL_EMPLOYMENT FROM VLEMPLOPYMENTELIGIBLEMAS@mybank WHERE " +
                "PROGRAM_NAME = ? AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
        try{
            view_data = jdbcTemplate.queryForObject(sql, new Object[]{pgm, emp}, String.class);
        }catch(EmptyResultDataAccessException e){
            view_data="";
        }
        try{
            if (view_data != null && !view_data.equals("") ) {
                if(totalemp == null || totalemp.equals("") || totalemp.equals("null") || totalemp.isEmpty())
                    color = "red";
                else {
                    sql = "SELECT COUNT(*) FROM VLEMPLOPYMENTELIGIBLEMAS@mybank WHERE " +
                            "PROGRAM_NAME = ? AND ? >= MIN_TOTAL_EMPLOYMENT " +
                            "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
                    int count = jdbcTemplate.queryForObject(sql, new Object[]{pgm, Integer.parseInt(totalemp), emp}, Integer.class);
                    if (count > 0) {
                        color = "green";
                    } else {
                        color = "red";
                    }
                }
            } else {
                color = "green";
                view_data = "No Data";
            }
        }catch(Exception e){
            e.printStackTrace();
            color = "red";
            view_data="Error";
        }
        totalempdata.put("currentValue", totalemp);
        totalempdata.put("color", color);
        totalempdata.put("masterValue", view_data);
        return totalempdata.toString();
    }

    public String findEligibleemploymentempcolour(String pgm,String emp,String totalemp) throws Exception {
        String color="",view_data="";
        JSONObject totalempdata = new JSONObject();
        String sql = "SELECT  MIN_CURRENT_EMPLOYMENT FROM VLEMPLOPYMENTELIGIBLEMAS@mybank WHERE " +
                "PROGRAM_NAME = ? AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
        try{
            view_data = jdbcTemplate.queryForObject(sql, new Object[]{pgm, emp}, String.class);
        }catch(EmptyResultDataAccessException e){
            view_data="";
        }
        try{
            if (view_data != null && !view_data.equals("") ) {
                if(totalemp == null || totalemp.equals("") || totalemp.equals("null") || totalemp.isEmpty())
                    color = "red";
                else {
                    sql = "SELECT COUNT(*) FROM VLEMPLOPYMENTELIGIBLEMAS@mybank WHERE " +
                            "PROGRAM_NAME = ? AND ? >= MIN_CURRENT_EMPLOYMENT " +
                            "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
                    int count = jdbcTemplate.queryForObject(sql, new Object[]{pgm, Integer.parseInt(totalemp), emp}, Integer.class);
                    if (count > 0) {
                        color = "green";
                    } else {
                        color = "red";
                    }
                }
            } else {
                color = "green";
                view_data = "No Data";
            }
        }catch(Exception e){
            e.printStackTrace();
            color = "red";
            view_data="Error";
        }
        totalempdata.put("currentValue", totalemp);
        totalempdata.put("color", color);
        totalempdata.put("masterValue", view_data);
        return totalempdata.toString();
    }

    public String findEligiblestaycolour(String pgm,String emp,Long stay) throws Exception {
        String color="",view_data="";
        JSONObject staydata = new JSONObject();

            String sql = "SELECT  MIN_MONTH FROM VLRESIDENCEELIGIBLEMAS@mybank WHERE " +
                    "PROGRAM_NAME = ? AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
            try {
                view_data = jdbcTemplate.queryForObject(sql, new Object[]{pgm, emp}, String.class);
            } catch (EmptyResultDataAccessException e) {
                view_data = "";
            }
        try {
            if (view_data != null && !view_data.equals("")) {
                if (stay == 0L)
                    color = "red";
                else {
                    sql = "SELECT COUNT(*) FROM VLRESIDENCEELIGIBLEMAS@mybank WHERE " +
                            "PROGRAM_NAME = ? AND ? >= MIN_MONTH " +
                            "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
                    int count = jdbcTemplate.queryForObject(sql, new Object[]{pgm, stay, emp}, Integer.class);
                    if (count > 0) {
                        color = "green";
                    } else {
                        color = "red";
                    }
                }
            } else {
                color = "green";
                view_data = "No Data";
            }
        }catch(Exception e){
            e.printStackTrace();
            color = "red";
            view_data="Error";
        }
        staydata.put("currentValue", stay);
        staydata.put("color", color);
        staydata.put("masterValue", view_data);
        return staydata.toString();
    }
    public String findEligibleBureaucolour(String pgm,String emp,String score) throws Exception {
        String color="";
        JSONObject incomedata = new JSONObject();
        try {
            if (score == null || score.equals("") || score.equals("null") || score.isEmpty())
                color = "red";
            else {
                String sql = "SELECT COUNT(*) FROM VLCOLOURSLABMAS@mybank WHERE " +
                        "PROGRAM_NAME = ? AND ? BETWEEN START_SCORE AND END_SCORE " +
                        "AND DEL_FLAG = 'N'  and color not in ('red')";
                Integer count = jdbcTemplate.queryForObject(sql, new Object[]{pgm, score}, Integer.class);
                if (count > 0) {
                    color = "green";
                } else {
                    color = "red";
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            color = "red";
        }
        incomedata.put("currentValue", score);
        incomedata.put("color", color);
        return incomedata.toString();
    }


}
