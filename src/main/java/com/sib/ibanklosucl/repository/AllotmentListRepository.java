package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.dto.dashboard.AllotmentDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllotmentListRepository {
    private final JdbcTemplate jdbcTemplate;

    public AllotmentListRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AllotmentDTO> getAllotmentList(String queue) {
        String query = "SELECT M.WI_NUM,M.SLNO ,e.LOAN_AMT, m.channel, " +
                "(SELECT TO_CHAR(MAX(QUEUE_ENTRY_DATE),'dd-mm-yyyy') FROM vltat t WHERE t.WI_NUM=m.wi_num AND t.slno=m.slno AND t.queue=?) AS queuedate, " +
                "M.CUST_NAME, CASE WHEN m.queue=? THEN 'PENDING' ELSE 'ALLOTTED' END AS STATUS, " +
                "mb.br_name, m.sol_id, mr.reg_name, mr.reg_code, a.DO_PPC allottedppc, " +
                "(SELECT queue FROM (SELECT t.*, ROW_NUMBER() OVER(ORDER BY t.queue_entry_date DESC) AS rnum FROM VEHICLE_LOAN_tat t WHERE t.WI_NUM=m.wi_num AND t.slno=m.slno) WHERE rnum=2) AS previousqueue " +
                "FROM vehicle_loan_master m " +
                "JOIN VEHICLE_LOAN_ELIGIBILITY e ON m.WI_NUM=e.WI_NUM AND m.slno=e.SLNO " +
                "JOIN misbmt@mybank mb ON m.sol_id=mb.sol_id " +
                "JOIN misreg@mybank mr ON mb.reg_code=mr.reg_code " +
                "LEFT OUTER JOIN VEHICLE_LOAN_ALLOTMENT a ON m.wi_num=a.wi_num AND m.slno=a.slno AND a.ACTIVE_FLG='Y' " +
                "WHERE m.QUEUE IN (?) AND m.active_flg='Y' " +
                "ORDER BY 4";

        return jdbcTemplate.query(query,new Object[]{queue,queue,queue}, (rs, rowNum) -> {
            AllotmentDTO allotmentData = new AllotmentDTO();
            allotmentData.setWiNum(rs.getString("WI_NUM"));
            allotmentData.setSlno(rs.getString("SLNO"));
            allotmentData.setLoanAmt(rs.getString("LOAN_AMT"));
            allotmentData.setChannel(rs.getString("channel"));
            allotmentData.setQueueDate(rs.getString("queuedate"));
            allotmentData.setCustName(rs.getString("CUST_NAME"));
            allotmentData.setStatus(rs.getString("STATUS"));
            allotmentData.setBrName(rs.getString("br_name"));
            allotmentData.setSolId(rs.getString("sol_id"));
            allotmentData.setRegName(rs.getString("reg_name"));
            allotmentData.setRegCode(rs.getString("reg_code"));
            allotmentData.setAllotedPpc(rs.getString("allottedppc"));
            allotmentData.setPreviousQueue(rs.getString("previousqueue"));
            allotmentData.setAllotQueueDate(rs.getString("queuedate"));
            return allotmentData;
        });
    }
}

