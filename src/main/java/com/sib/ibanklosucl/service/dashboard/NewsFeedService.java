package com.sib.ibanklosucl.service.dashboard;

import com.sib.ibanklosucl.dto.dashboard.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class NewsFeedService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Message> fetchMessages(String employeeType, String usersolid) {
        String msgqry;
        Object[] params;

        if ("SIB".equals(employeeType)) {
            if (usersolid.startsWith("0") || usersolid.startsWith("1")) {
                msgqry = "SELECT TRIM(msg), home_sol, cmuser FROM notepad@mybank " +
                        "WHERE (home_sol = ? AND stop_flag = 'N' AND disp_from <= SYSDATE AND disp_end > SYSDATE AND vuser IS NULL AND vdate IS NULL) " +
                        "OR (disp_sol IN ('1', '2', '6') AND stop_flag = 'N' AND disp_from <= SYSDATE AND disp_end > SYSDATE AND vuser IS NULL AND vdate IS NULL) " +
                        "UNION " +
                        "SELECT TRIM(msg), home_sol, cmuser FROM notepad@mybank " +
                        "WHERE stop_flag = 'N' AND disp_from <= SYSDATE AND disp_end > SYSDATE AND vuser IS NULL AND vdate IS NULL " +
                        "AND home_sol IN (SELECT reg_code FROM misbmt@mybank WHERE sol_id = ?) AND disp_sol = '5'";
                params = new Object[]{usersolid, usersolid}; // Two parameters for the query
            } else if (usersolid.startsWith("9")) {
                msgqry = "SELECT TRIM(msg), home_sol, cmuser FROM notepad@mybank " +
                        "WHERE (home_sol = ? AND stop_flag = 'N' AND disp_from <= SYSDATE AND disp_end > SYSDATE AND vuser IS NULL AND vdate IS NULL) " +
                        "OR (disp_sol IN ('1', '3', '6') AND stop_flag = 'N' AND disp_from <= SYSDATE AND disp_end > SYSDATE AND vuser IS NULL AND vdate IS NULL)";
                params = new Object[]{usersolid}; // Only one parameter for this query
            } else if (usersolid.startsWith("8")) {
                msgqry = "SELECT TRIM(msg), home_sol, cmuser FROM notepad@mybank " +
                        "WHERE (home_sol = ? AND stop_flag = 'N' AND disp_from <= SYSDATE AND disp_end > SYSDATE AND vuser IS NULL AND vdate IS NULL) " +
                        "OR (disp_sol IN ('1', '4') AND stop_flag = 'N' AND disp_from <= SYSDATE AND disp_end > SYSDATE AND vuser IS NULL AND vdate IS NULL)";
                params = new Object[]{usersolid}; // Only one parameter for this query
            } else {
                msgqry = "SELECT TRIM(msg), home_sol, cmuser FROM notepad@mybank " +
                        "WHERE home_sol = ? AND stop_flag = 'N' AND disp_from <= SYSDATE AND disp_end > SYSDATE AND vuser IS NULL AND vdate IS NULL";
                params = new Object[]{usersolid}; // Only one parameter for this query
            }
        } else {
            msgqry = "SELECT TRIM(msg), home_sol, cmuser FROM notepad@mybank " +
                    "WHERE home_sol = ? AND stop_flag = 'N' AND disp_from <= SYSDATE AND disp_end > SYSDATE AND vuser IS NULL AND vdate IS NULL";
            params = new Object[]{usersolid}; // Only one parameter for this query
        }

        return jdbcTemplate.query(msgqry, params, (rs, rowNum) -> {
            String msg = rs.getString(1);
            String homeSol = rs.getString(2);
            String cmuser = rs.getString(3);

            boolean imgExists = checkIfImageExists(cmuser);
            String clr = determineCssClass(rowNum);

            return new Message(msg, homeSol, cmuser, clr, imgExists);
        });
    }

    public List<Message> fetchMessages( String usersolid) {
        Object[] params;
                String msgqry = "SELECT TRIM(msg), home_sol, cmuser FROM notepad@mybank " +
                        "WHERE (home_sol = ? AND stop_flag = 'N' AND disp_from <= SYSDATE AND disp_end > SYSDATE AND vuser IS NULL AND vdate IS NULL) " +
                        "OR (disp_sol IN ('1', '2', '6') AND stop_flag = 'N' AND disp_from <= SYSDATE AND disp_end > SYSDATE AND vuser IS NULL AND vdate IS NULL) " +
                        "UNION " +
                        "SELECT TRIM(msg), home_sol, cmuser FROM notepad@mybank " +
                        "WHERE stop_flag = 'N' AND disp_from <= SYSDATE AND disp_end > SYSDATE AND vuser IS NULL AND vdate IS NULL " +
                        "AND home_sol IN (SELECT reg_code FROM misbmt@mybank WHERE sol_id = ?) AND disp_sol = '5'";
                params = new Object[]{usersolid, usersolid}; // Two parameters for the query


        return jdbcTemplate.query(msgqry, params, (rs, rowNum) -> {
            String msg = rs.getString(1);
            String homeSol = rs.getString(2);
            String cmuser = rs.getString(3);

            boolean imgExists = checkIfImageExists(cmuser);
            String clr = determineCssClass(rowNum);

            return new Message(msg, homeSol, cmuser, clr, imgExists);
        });
    }

    public List<Message> fetchFinacleMessages() {
        String msgqry = "SELECT TO_CHAR(start_dt, 'dd-mm-yyyy'), tick_txt FROM custom.sibticker@finacle10 " +
                "WHERE to_date('30-06-2022','DD-MM-YYYY') BETWEEN start_dt AND exp_dt";
              //  "WHERE SYSDATE BETWEEN start_dt AND exp_dt";

        return jdbcTemplate.query(msgqry, (rs, rowNum) -> {
            String date = rs.getString(1);
            String msg = rs.getString(2);
            return new Message(msg, "Finacle", "", date, false);
        });
    }

    private boolean checkIfImageExists(String cmuser) {
        File file = new File("/data/UPLOAD_FILES/IMAGE/" + cmuser + ".xjpg");
        return file.exists();
    }

    private String determineCssClass(int rowNum) {
        return (rowNum % 2 == 0) ? "media-chat-item hstack align-items-start gap-3" : "media-chat-item media-chat-item-reverse hstack align-items-start gap-3";
    }

}
