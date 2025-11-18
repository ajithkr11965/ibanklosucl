package com.sib.ibanklosucl.dto.mssf;

import lombok.Data;

@Data
public class MSSFLockDTO {
    private String lockFlag;
    private String lockedBy;
}