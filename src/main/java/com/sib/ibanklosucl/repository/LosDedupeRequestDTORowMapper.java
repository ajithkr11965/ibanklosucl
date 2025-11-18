package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.dto.LosDedupeRequestDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LosDedupeRequestDTORowMapper implements RowMapper<LosDedupeRequestDTO> {

    @Override
    public LosDedupeRequestDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        LosDedupeRequestDTO dto = new LosDedupeRequestDTO();
        dto.setCustName(rs.getString("custName"));
        dto.setDob(rs.getString("dob"));
        dto.setPanNo(rs.getString("panNo"));
        dto.setAadharNo(rs.getString("aadharNo")==null?"":rs.getString("aadharNo"));
        dto.setCustID(rs.getString("custID")==null?"":rs.getString("custID"));
        dto.setPassport(rs.getString("passport")==null?"":rs.getString("passport"));
        return dto;
    }
}
