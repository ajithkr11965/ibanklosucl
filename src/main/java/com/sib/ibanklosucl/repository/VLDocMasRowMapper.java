package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.dto.VLDocMas;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VLDocMasRowMapper implements RowMapper<VLDocMas> {

    @Override
    public VLDocMas mapRow(ResultSet rs, int rowNum) throws SQLException {
        VLDocMas dto = new VLDocMas();
        dto.setApplicant(rs.getString("APPLICANT"));
        dto.setCoapplicant(rs.getString("COAPPLICANT"));
        dto.setGurantor(rs.getString("GURANTOR"));
        dto.setGeneric(rs.getString("GENERIC"));
        dto.setLabelcode(rs.getString("LABEL_CODE"));
        dto.setLabelname(rs.getString("LABEL_NAME"));
        dto.setFilename(rs.getString("FILE_NAME"));
        dto.setMandatory(rs.getString("MANDATORY"));
        return dto;
    }

}