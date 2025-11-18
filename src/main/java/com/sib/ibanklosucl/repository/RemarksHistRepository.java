package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.dto.dashboard.RemarksHistDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RemarksHistRepository {
    private final JdbcTemplate jdbcTemplate;

    public RemarksHistRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RemarksHistDTO> getRemarks(String slno) {
        String query = "SELECT TO_CHAR(R.CMDATE,'DD-MM-YYYY HH24:MI:SS')CMDATE , R.CMUSER,  P.PPC_NAME,  J.SOL_ID,    R.FROM_QUEUE, R.TO_QUEUE,  R.REMARKS, R.SLNO,  R.WI_NUM, D.REG_NAME,B.BR_NAME,R.ASSIGN_USER FROM  VEHICLE_LOAN_QUEUE_DETAILS R INNER JOIN MISHRP@mybank P ON R.CMUSER = P.PPCNO INNER JOIN MISHRJ@mybank J ON R.CMUSER = J.PPCNO LEFT JOIN MISREG@mybank D ON J.SOL_ID = D.REG_CODE LEFT JOIN MISBMT@mybank B ON J.SOL_ID = B.SOL_ID WHERE  R.SLNO = ? AND (SYSDATE BETWEEN J.START_DATE AND J.END_DATE) ORDER BY R.CMDATE DESC\n";

        return jdbcTemplate.query(query, new Object[]{slno},  (rs, rowNum) -> {
            RemarksHistDTO remarksHist = new RemarksHistDTO();
            remarksHist.setCmdate(rs.getString("CMDATE"));
            remarksHist.setCmuser(rs.getString("CMUSER"));
            remarksHist.setPpcName(rs.getString("PPC_NAME"));
            remarksHist.setSolId(rs.getString("SOL_ID"));
            remarksHist.setFromQueue(rs.getString("FROM_QUEUE"));
            remarksHist.setToQueue(rs.getString("TO_QUEUE"));
            remarksHist.setRemarks(rs.getString("REMARKS"));
            remarksHist.setWiNum(rs.getString("WI_NUM"));
            remarksHist.setRegName(rs.getString("REG_NAME"));
            remarksHist.setBrName(rs.getString("BR_NAME"));
            remarksHist.setAssignUser(rs.getString("ASSIGN_USER"));
            return remarksHist;
        });
    }
}

