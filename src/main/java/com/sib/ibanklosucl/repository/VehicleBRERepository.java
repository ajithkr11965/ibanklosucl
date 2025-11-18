package com.sib.ibanklosucl.repository;
import org.springframework.beans.factory.annotation.Autowired;

import org.json.JSONObject;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
@Repository
public class VehicleBRERepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;



    public List<Map<String, String>> findmaxage(String pgm, String emp) {
        String sql = "SELECT SUPER_ANNUATION,MAX_AGE FROM VLMAXAGEBRESLABMAS@mybank " +
                "WHERE  " +
                "PROGRAM_NAME = ? AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
        return jdbcTemplate.query(sql, new Object[]{pgm,emp}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("SUPER_ANNUATION", rs.getString("SUPER_ANNUATION"));
            map.put("MAX_AGE", rs.getString("MAX_AGE"));
            return map;
        });
    }
    public String findtenurecolour(String pgm,String emp,String tenure) throws Exception {
        String color="",view_data="";
        JSONObject tenordata = new JSONObject();
        String sql = "SELECT VIEW_DATA FROM VLTENUREBRESLABMAS@mybank WHERE " +
                "PROGRAM_NAME = ? AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
        try{
            view_data = jdbcTemplate.queryForObject(sql, new Object[]{pgm, emp}, String.class);
        }catch(
        EmptyResultDataAccessException e){
            view_data="";
        }
        try{
            if (view_data != null && !view_data.equals("")) {
                if(tenure == null || tenure.equals("")|| tenure.isEmpty() || tenure.equals("null"))
                    color = "red";
                else {
                    sql = "SELECT COUNT(*) FROM VLTENUREBRESLABMAS@mybank WHERE " +
                            "PROGRAM_NAME = ? AND ? BETWEEN MIN_TENURE AND MAX_TENURE " +
                            "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
                    int count = jdbcTemplate.queryForObject(sql, new Object[]{pgm, Integer.parseInt(tenure), emp}, Integer.class);
                    if (count > 0) {
                        color = "green";
                    } else {
                        color = "amber";
                    }
                }
            } else {
                color = "green";
                view_data = "No Data";
            }
        }catch(Exception e){
            e.printStackTrace();
            color = "amber";
            view_data="Error";
        }
        tenordata.put("currentValue", tenure);
        tenordata.put("color", color);
        tenordata.put("masterValue", view_data);
        return tenordata.toString();
    }
    public String findBreemploymentcolour(String pgm,String emp,String totalemp) throws Exception {
        String color="",view_data="";
        JSONObject totalempdata = new JSONObject();
        String sql = "SELECT  MIN_TOTAL_EMPLOYMENT FROM VLEMPLOPYMENTBRESLABMAS@mybank WHERE " +
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
                    sql = "SELECT COUNT(*) FROM VLEMPLOPYMENTBRESLABMAS@mybank WHERE " +
                            "PROGRAM_NAME = ? AND ? >= MIN_TOTAL_EMPLOYMENT " +
                            "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
                    int count = jdbcTemplate.queryForObject(sql, new Object[]{pgm, Integer.parseInt(totalemp), emp}, Integer.class);
                    if (count > 0) {
                        color = "green";
                    } else {
                        color = "amber";
                    }
                }
            } else {
                color = "green";
                view_data = "No Data";
            }
        }catch(Exception e){
            e.printStackTrace();
            color = "amber";
            view_data="Error";
        }
        totalempdata.put("currentValue", totalemp);
        totalempdata.put("color", color);
        totalempdata.put("masterValue", view_data);
        return totalempdata.toString();
    }
    public String findBreCurrentemploymentempcolour(String pgm,String emp,String totalemp) throws Exception {
        String color="",view_data="";
        JSONObject totalempdata = new JSONObject();
        String sql = "SELECT  MIN_CURRENT_EMPLOYMENT FROM VLEMPLOPYMENTBRESLABMAS@mybank WHERE " +
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
                    sql = "SELECT COUNT(*) FROM VLEMPLOPYMENTBRESLABMAS@mybank WHERE " +
                            "PROGRAM_NAME = ? AND ? >= MIN_CURRENT_EMPLOYMENT " +
                            "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
                    int count = jdbcTemplate.queryForObject(sql, new Object[]{pgm, Integer.parseInt(totalemp), emp}, Integer.class);
                    if (count > 0) {
                        color = "green";
                    } else {
                        color = "amber";
                    }
                }
            } else {
                color = "green";
                view_data = "No Data";
            }
        }catch(Exception e){
            e.printStackTrace();
            color = "amber";
            view_data="Error";
        }
        totalempdata.put("currentValue", totalemp);
        totalempdata.put("color", color);
        totalempdata.put("masterValue", view_data);
        return totalempdata.toString();
    }


    public String findbureaucolour(String pgm,String emp,Long bureauscore) throws Exception {
        String bureaucolor = "",view_data="";
        JSONObject bureaudata = new JSONObject();
        String sql = "SELECT VIEW_DATA FROM VLBUREAUBRESLABMAS@mybank WHERE " +
                "PROGRAM_NAME = ? AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
        try{
            view_data = jdbcTemplate.queryForObject(sql, new Object[]{pgm, emp}, String.class);
        }catch(
                EmptyResultDataAccessException e){
            view_data="";
        }
        try{
            if (view_data != null && !view_data.equals("")) {
                if(bureauscore == null )
                    bureaucolor = "red";
                else {
                    String sqlbureau = "SELECT COUNT(*) FROM VLBUREAUBRESLABMAS@mybank WHERE " +
                            "PROGRAM_NAME = ? AND ? BETWEEN MIN_BUREAU_SCORE AND MAX_BUREAU_SCORE " +
                            "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
                    Integer count = jdbcTemplate.queryForObject(sqlbureau, new Object[]{pgm, bureauscore, emp}, Integer.class);
                    if (count > 0) {
                        bureaucolor = "green";
                    } else {
                        bureaucolor = "amber";
                    }
                }
            } else {
                bureaucolor = "green";
                view_data = "No Data";
            }
        }catch(Exception e){
            e.printStackTrace();
            bureaucolor = "amber";
            view_data="Error";
        }
        bureaudata.put("currentValue", bureauscore);
        bureaudata.put("color", bureaucolor);
        bureaudata.put("masterValue", view_data);
        return bureaudata.toString();
    }


    public List<Map<String, String>> findracemasterdata(String pgm,String tabel_name) {
        String sql = "";
        if(tabel_name.equals("VLKERALARACESLABMAS"))
            sql = "SELECT SCOREVIEW  FROM VLKERALARACESLABMAS@mybank WHERE " +
                    "PROGRAM_NAME = ? AND DEL_FLAG = 'N' and color in ('green')";
        else if(tabel_name.equals("VLNONKERALARACESLABMAS"))
            sql = "SELECT SCOREVIEW FROM VLNONKERALARACESLABMAS@mybank WHERE " +
                    "PROGRAM_NAME = ?  AND DEL_FLAG = 'N' and color in ('green') ";
        else if(tabel_name.equals("VLNTBRACESLABMAS"))
            sql = "SELECT SCOREVIEW FROM VLNTBRACESLABMAS@mybank WHERE " +
                    "PROGRAM_NAME = ?  AND DEL_FLAG = 'N' and color in ('green') ";
        return jdbcTemplate.query(sql, new Object[]{pgm}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("SCOREVIEW", rs.getString("SCOREVIEW"));
            return map;
        });
    }
    public List<Map<String, String>> findracemasterdataNEW(String pgm,String tabel_name,String model_type) {
        String sql = "";
        if(tabel_name.equals("VLBUREAURACEBREMAP")) {
            sql = "SELECT BUREAU_DATA,RACE_DATA  FROM VLBUREAURACEBREMAP@mybank WHERE " +
                    " DEL_FLAG = 'N' and color in ('green')";
            return jdbcTemplate.query(sql, new Object[]{}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                String data= "Bureau: " + rs.getString("BUREAU_DATA") + " AND " +"Race: " + rs.getString("RACE_DATA");
                map.put("SCOREVIEW",data);
                return map;
            });
        }else if(tabel_name.equals("VLNTBRACESLABMAS")) {
            sql = "SELECT SCOREVIEW FROM VLNTBRACESLABMAS@mybank WHERE " +
                    "PROGRAM_NAME = ?  AND DEL_FLAG = 'N' and color in ('green') ";
            return jdbcTemplate.query(sql, new Object[]{pgm}, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("SCOREVIEW", rs.getString("SCOREVIEW"));
                return map;
            });
        }else{
            Map<String, String> map = new HashMap<>();
            return (List<Map<String, String>>) map;
        }

    }
    public String findracecolour(String pgm,String emp,Long racescore,String tabel_name) throws Exception {
        String bureaucolor = "",view_data="",sql="";
        JSONObject bureaudata = new JSONObject();
        if(tabel_name.equals("VLKERALARACESLABMAS")) {
            sql = "SELECT count(*)  FROM VLKERALARACESLABMAS@mybank WHERE " +
                    "PROGRAM_NAME = ? AND DEL_FLAG = 'N'";
        }else if(tabel_name.equals("VLNONKERALARACESLABMAS")) {
            sql = "SELECT count(*) FROM VLNONKERALARACESLABMAS@mybank WHERE " +
                    "PROGRAM_NAME = ?  AND DEL_FLAG = 'N'";
        }else if(tabel_name.equals("VLNTBRACESLABMAS")) {
            sql = "SELECT count(*) FROM VLNTBRACESLABMAS@mybank WHERE " +
                    "PROGRAM_NAME = ?  AND DEL_FLAG = 'N'";
            view_data="(NTC)";
        }else{
            bureaucolor = "red";
        }
        Integer count =0;
        if(bureaucolor.equals("")) {
            try {
                 count = jdbcTemplate.queryForObject(sql, new Object[]{pgm}, Integer.class);
            } catch (
                    EmptyResultDataAccessException e) {
                count =0;
            }
            try {
                if (count>0) {
                    if (racescore == null)
                        bureaucolor = "red";
                    else {
                        String sqlbureau ="",viewquery="";
                        if(tabel_name.equals("VLKERALARACESLABMAS")) {
                            sqlbureau = "SELECT COLOR FROM VLKERALARACESLABMAS@mybank WHERE " +
                                    "PROGRAM_NAME = ? AND ? BETWEEN START_SCORE AND END_SCORE " +
                                    "AND DEL_FLAG = 'N'";
                            /*viewquery = "SELECT scoreview FROM VLKERALARACESLABMAS@mybank WHERE " +
                                    "PROGRAM_NAME = ? AND ? BETWEEN START_SCORE AND END_SCORE " +
                                    "AND DEL_FLAG = 'N'";*/
                        }else if(tabel_name.equals("VLNONKERALARACESLABMAS")) {
                            sqlbureau = "SELECT COLOR FROM VLNONKERALARACESLABMAS@mybank WHERE " +
                                    "PROGRAM_NAME = ? AND ? BETWEEN START_SCORE AND END_SCORE " +
                                    "AND  DEL_FLAG = 'N'";
                            /*viewquery = "SELECT scoreview FROM VLNONKERALARACESLABMAS@mybank WHERE " +
                                    "PROGRAM_NAME = ? AND ? BETWEEN START_SCORE AND END_SCORE " +
                                    "AND DEL_FLAG = 'N'";*/
                        }else if(tabel_name.equals("VLNTBRACESLABMAS")) {
                            sqlbureau = "SELECT COLOR FROM VLNTBRACESLABMAS@mybank WHERE " +
                                    "PROGRAM_NAME = ? AND ? BETWEEN START_SCORE AND END_SCORE " +
                                    "AND  DEL_FLAG = 'N'";
                            /*viewquery = "SELECT scoreview FROM VLNONKERALARACESLABMAS@mybank WHERE " +
                                    "PROGRAM_NAME = ? AND ? BETWEEN START_SCORE AND END_SCORE " +
                                    "AND DEL_FLAG = 'N'";*/
                        }

                            try {
                                bureaucolor = jdbcTemplate.queryForObject(sqlbureau, new Object[]{pgm,racescore}, String.class);
                               // view_data = jdbcTemplate.queryForObject(viewquery, new Object[]{pgm,racescore}, String.class);
                            } catch (EmptyResultDataAccessException e) {
                                bureaucolor = "red";
                               // view_data = "Error";
                            }
                    }
                } else {
                    bureaucolor = "green";
                    //view_data = "No Data";
                }
            } catch (Exception e) {
                e.printStackTrace();
                bureaucolor = "red";
                //view_data = "Error";
            }
        }
        bureaudata.put("currentValue", racescore+view_data);
        bureaudata.put("color", bureaucolor);
        //bureaudata.put("masterValue", view_data);
        return bureaudata.toString();
    }
    public String findracecolourNEW(String pgm,String emp,Long racescore,String tabel_name,Long bureauscore,String model_type) throws Exception {
        String brecolor = "", view_data = "", sql = "", sqlbureau = "", sqlrace = "";
        JSONObject bureaudata = new JSONObject();
        String bureau_slno = "", bureau_viewdata = "";
        String race_slno = "", race_viewdata = "";
        if (tabel_name.equals("VLBUREAURACEBREMAP")) {
            sqlbureau = "SELECT slno FROM VLBUREAUSLABBREMAS@mybank WHERE " +
                    " MODEL_TYPE= ? AND ? BETWEEN START_SCORE AND END_SCORE " +
                    "AND  DEL_FLAG = 'N'";
            try {
                bureau_slno = jdbcTemplate.queryForObject(sqlbureau, new Object[]{model_type, bureauscore}, String.class);
                /*  String viewquery = "SELECT VIEW_DATA FROM VLBUREAUSLABBREMAS@mybank WHERE " +
                    " MODEL_TYPE= ? AND ? BETWEEN START_SCORE AND END_SCORE " +
                    "AND  DEL_FLAG = 'N'";
                    bureau_viewdata = jdbcTemplate.queryForObject(viewquery, new Object[]{model_type,bureauscore}, String.class);
                 */
            } catch (EmptyResultDataAccessException e) {
                brecolor = "red";
                view_data = "Error";
            }
            sqlrace = "SELECT slno FROM VLRACESLABBREMAS@mybank WHERE " +
                    " MODEL_TYPE= ? AND ? BETWEEN START_SCORE AND END_SCORE " +
                    "AND  DEL_FLAG = 'N'";
            try {
                race_slno = jdbcTemplate.queryForObject(sqlrace, new Object[]{model_type, racescore}, String.class);
                /*  viewquery = "SELECT VIEW_DATA FROM VLBUREAUSLABBREMAS@mybank WHERE " +
                    " MODEL_TYPE= ? AND ? BETWEEN START_SCORE AND END_SCORE " +
                    "AND  DEL_FLAG = 'N'";
                    race_viewdata = jdbcTemplate.queryForObject(viewquery, new Object[]{model_type,racescore}, String.class);
                 */
            } catch (EmptyResultDataAccessException e) {
                brecolor = "red";
                view_data = "Error";
            }
            Integer count = 0;
            if (!race_slno.equals("") && !bureau_slno.equals("") && brecolor.equals("")) {
                sql = "SELECT count(*)  FROM VLBUREAURACEBREMAP@mybank WHERE " +
                        "MODEL_TYPE= ? AND BUREAU_SLNO =? and RACE_SLNO =? and DEL_FLAG = 'N'";
                /* sql = "SELECT count(*)  FROM VLBUREAURACEBREMAP@mybank WHERE " +
                        "MODEL_TYPE= ? AND BUREAU_SLNO =? and RACE_SLNO =? and BUREAU_DATA= ? and RACE_DATA= ?  and DEL_FLAG = 'N'";
                        count = jdbcTemplate.queryForObject(sql, new Object[]{model_type,bureau_slno,race_slno,bureau_viewdata,race_viewdata}, Integer.class);

                */
                try {
                    count = jdbcTemplate.queryForObject(sql, new Object[]{model_type, bureau_slno, race_slno}, Integer.class);
                } catch (
                        EmptyResultDataAccessException e) {
                    count = 0;
                }
                try {
                    if (count > 0) {
                        String sqlbureauNew = "", viewquery1 = "";
                        sqlbureauNew = "SELECT COLOR FROM VLBUREAURACEBREMAP@mybank WHERE " +
                                "MODEL_TYPE= ? AND BUREAU_SLNO =? and RACE_SLNO =? and DEL_FLAG = 'N'";
                        try {
                            brecolor = jdbcTemplate.queryForObject(sqlbureauNew, new Object[]{model_type, bureau_slno, race_slno}, String.class);
                        } catch (EmptyResultDataAccessException e) {
                            brecolor = "red";
                            view_data = "Error";
                        }
                    } else {
                        brecolor = "green";
                        view_data = "No Data";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    brecolor = "red";
                    view_data = "Error";
                }
            }
            String current_data="bureauscore :" +bureauscore +",racescore :"+racescore  +" "+ view_data;
            bureaudata.put("currentValue",current_data);

        } else if (tabel_name.equals("VLNTBRACESLABMAS")) {
            sql = "SELECT count(*) FROM VLNTBRACESLABMAS@mybank WHERE " +
                    "PROGRAM_NAME = ?  AND DEL_FLAG = 'N'";
            view_data = "(NTC)";
            Integer count = 0;
            try {
                count = jdbcTemplate.queryForObject(sql, new Object[]{pgm}, Integer.class);
            } catch (
                    EmptyResultDataAccessException e) {
                count =0;
            }
            try {
                if (count > 0) {
                    String sqlbureauNew = "", viewquery1 = "";
                    sqlbureauNew = "SELECT COLOR FROM VLNTBRACESLABMAS@mybank WHERE " +
                            "PROGRAM_NAME = ? AND ? BETWEEN START_SCORE AND END_SCORE " +
                            "AND  DEL_FLAG = 'N'";
                    try {
                        brecolor = jdbcTemplate.queryForObject(sqlbureauNew, new Object[]{pgm,racescore}, String.class);
                    } catch (EmptyResultDataAccessException e) {
                        brecolor = "red";
                        view_data = "Error";
                    }
                } else {
                    brecolor = "green";
                    view_data = "No Data";
                }
            } catch (Exception e) {
                e.printStackTrace();
                brecolor = "red";
                view_data = "Error";
            }
            bureaudata.put("currentValue", racescore + view_data);
        } else {
            brecolor = "red";
            bureaudata.put("currentValue", racescore + view_data);
        }
        bureaudata.put("color", brecolor);
        //bureaudata.put("masterValue", view_data);
        return bureaudata.toString();
    }

    public String findfirsttimeloanamount(String pgm,String emp,String loanamount ) throws Exception {
        JSONObject loandata = new JSONObject();
        String color="",view_data="";
        String sql = "SELECT VIEW_DATA  FROM VLLOANAMOUNTBRESLABMAS@mybank WHERE " +
                "PROGRAM_NAME = ? AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All')  AND  FIRST_TIME_FLG = 'Y' AND DEL_FLAG = 'N'";
        try{
            view_data = jdbcTemplate.queryForObject(sql, new Object[]{pgm, emp}, String.class);
        }catch(
            EmptyResultDataAccessException e){
            view_data="";
        }
        try{
            if (view_data != null && !view_data.equals("")) {
                if (loanamount == null || loanamount.equals("") || loanamount.equals("null") || loanamount.isEmpty()) {
                    color = "red";
                }else {
                    BigDecimal loanAmountNew= new BigDecimal(loanamount);
                    sql = "SELECT COUNT(*) FROM VLLOANAMOUNTBRESLABMAS@mybank WHERE " +
                            "PROGRAM_NAME = ? AND ? BETWEEN MIN_AMT AND MAX_AMT " +
                            "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND  FIRST_TIME_FLG = 'Y' AND DEL_FLAG = 'N'";
                    int count = jdbcTemplate.queryForObject(sql, new Object[]{pgm, loanAmountNew, emp}, Integer.class);
                    if (count > 0) {
                        color = "green";
                    } else {
                        color = "amber";
                    }
                }
            } else {
                color = "green";
                view_data = "No Data";
            }
        }catch(Exception e){
            e.printStackTrace();
            color = "amber";
            view_data="Error";
        }
        loandata.put("masterValue", view_data);
        loandata.put("color", color);
        loandata.put("currentValue", loanamount);
        return loandata.toString();
    }
    public String findloanamount(String pgm,String emp,String loanamount ) throws Exception {
        JSONObject loandata = new JSONObject();String color = "",view_data=""; int amtCount=0;
        String sql = "SELECT count(*)  FROM VLLOANAMOUNTBRESLABMAS@mybank WHERE " +
                "PROGRAM_NAME = ? AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All')  AND  FIRST_TIME_FLG in ('N','A') AND DEL_FLAG = 'N'";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{pgm, emp}, Integer.class);
        if(count==0){
            color = "green";
            view_data = "No Data";
        }else if(count==1){
            if (loanamount == null || loanamount.equals("") || loanamount.equals("null") || loanamount.isEmpty()) {
                color = "red";
            }else {
                try{
                    BigDecimal loanAmountNew= new BigDecimal(loanamount);
                    sql = "SELECT COUNT(*) FROM VLLOANAMOUNTBRESLABMAS@mybank WHERE " +
                            "PROGRAM_NAME = ? AND ? BETWEEN MIN_AMT AND MAX_AMT " +
                            "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND  FIRST_TIME_FLG in ('N','A') AND DEL_FLAG = 'N'";
                    amtCount = jdbcTemplate.queryForObject(sql, new Object[]{pgm, loanAmountNew, emp}, Integer.class);
                    if (amtCount > 0) {
                        color = "green";
                    } else {
                        color = "amber";

                    }
                    sql = "SELECT view_data FROM VLLOANAMOUNTBRESLABMAS@mybank WHERE " +
                            "PROGRAM_NAME = ? AND ? BETWEEN MIN_AMT AND MAX_AMT " +
                            "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND  FIRST_TIME_FLG in ('N','A') AND DEL_FLAG = 'N'";
                    try{
                        view_data = jdbcTemplate.queryForObject(sql, new Object[]{pgm,loanAmountNew, emp}, String.class);
                    }catch(
                            EmptyResultDataAccessException e){
                        view_data="";
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    color = "amber";
                    view_data="Error";
                }
            }

        }else{
            color = "amber";
            view_data="Error";
        }
        loandata.put("masterValue", view_data);
        loandata.put("color", color);
        loandata.put("currentValue", loanamount);
        return loandata.toString();
    }


}
