package com.sib.ibanklosucl.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class VehicleLoanMasterRepositoryJdbc {
    @Autowired
    private JdbcTemplate jdbcTemplate;

     @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Map<String, String>> getBsQueue(String queue,   List<String> solId) {
        /*
        String sql = "WITH TAT AS (SELECT wiNum,SLNO,QUEUE,queueExitUser ,TO_CHAR(queueExitDate,'DD-MM-YYYY HH24:MI:SS') queueExitDate FROM (SELECT wiNum,SLNO, QUEUE,queueExitUser,queueExitDate, ROW_NUMBER() OVER (PARTITION BY wiNum,SLNO ORDER BY INO DESC) AS RN FROM VehicleLoanTat) SUB WHERE RN=2)" +
                "SELECT m.slno,m.wi_num,m.cust_name, to_char(m.ri_rcre_date,'dd-mm-yyyy hh24:mi:ss') ri_rcre_date, m.channel, l.locked_by, l.lock_flg, " +
                "s.queue previousqueue, s.queueExitUser,s.queueExitDate FROM VehicleLoanMaster m LEFT OUTER JOIN VehicleLoanLock l ON m.wiNum = l.wiNum  and l.delFlg='N' and l.lockFlg='Y' and l.releasedOn is null LEFT OUTER JOIN TAT S ON M.wiNum=S.wiNum AND M.SLNO=S.SLNO WHERE   m.queue = ? AND m.activeFlg = 'Y' AND m.sol_Id = ? AND m.cust_Name IS NOT NULL";
                */

        String sql = "WITH TAT AS (SELECT wi_num,SLNO,QUEUE,queue_Exit_User ,TO_CHAR(queue_Exit_Date,'DD-MM-YYYY HH24:MI:SS') queue_Exit_Date FROM (SELECT wi_num,SLNO, QUEUE,queue_Exit_User,queue_Exit_Date, ROW_NUMBER() OVER (PARTITION BY wi_num,SLNO ORDER BY INO DESC) AS RN FROM Vehicle_Loan_Tat) SUB WHERE RN=2)" +
                "SELECT m.slno,m.wi_num,m.cust_name, to_char(m.ri_rcre_date,'dd-mm-yyyy hh24:mi:ss') ri_rcre_date, m.channel, l.locked_by, l.lock_flg, " +
                "(select CODEDESC from misrct where codetype='QT' and codevalue = s.queue and DELFLAG = 'N') previousqueue, s.queue_Exit_User,s.queue_Exit_Date FROM Vehicle_Loan_Master m LEFT OUTER JOIN Vehicle_Loan_Lock l ON m.wi_num = l.wi_num  and l.del_Flg='N' and l.lock_Flg='Y' and l.released_On is null LEFT OUTER JOIN TAT S ON M.wi_num=S.wi_num AND M.SLNO=S.SLNO WHERE   m.queue = :queue AND m.active_Flg = 'Y' AND m.sol_Id in (:solIds)  AND m.cust_Name IS NOT NULL";

           Map<String, Object> params = new HashMap<>();
        params.put("queue", queue);
        params.put("solIds", solId);

         return namedParameterJdbcTemplate.query(
            sql,
            params,
            (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("slno", rs.getString("slno"));
                map.put("wi_num", rs.getString("wi_num"));
                map.put("cust_name", rs.getString("cust_name"));
                map.put("ri_rcre_date", rs.getString("ri_rcre_date"));
                map.put("channel", rs.getString("channel"));
                map.put("locked_by", rs.getString("locked_by"));
                map.put("lock_flg", rs.getString("lock_flg"));
                map.put("previousqueue", rs.getString("previousqueue"));
                map.put("queue_Exit_User", rs.getString("queue_Exit_User"));
                map.put("queue_Exit_Date", rs.getString("queue_Exit_Date"));
                return map;
            }
    );



    }
}
