package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.dto.dashboard.UserSelectDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserSelectRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserSelectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UserSelectDTO> getUserList() {
        String query = "SELECT P.PPCNO,M.PPC_NAME,D.CATEGORY FROM PPCAVAIL@mybank P, MISHRP@mybank M,MISHRD@mybank D where M.PPCNO=P.PPCNO AND P.PPCNO=D.PPCNO AND (SYSDATE BETWEEN P.START_DATE AND P.END_DATE)\n" +
                "AND (SYSDATE BETWEEN D.START_DATE AND D.END_DATE) AND P.STATUS='Active' AND SOL_ID='8009' AND D.CATEGORY != 'C8' ORDER BY 3 DESC";

        return jdbcTemplate.query(query, (rs, rowNum) -> {
            UserSelectDTO userdetails = new UserSelectDTO();
            userdetails.setPpcno(rs.getString("PPCNO"));
            userdetails.setPpcName(rs.getString("PPC_NAME"));
            userdetails.setCategory(rs.getString("CATEGORY"));
            return userdetails;
        });
    }
}

