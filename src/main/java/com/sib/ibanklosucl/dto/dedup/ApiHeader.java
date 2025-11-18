package com.sib.ibanklosucl.dto.dedup;

import lombok.Data;

@Data
public class ApiHeader {
    private String Timestamp;
    private String APIName;
    private String APIVersion;
    private String Interface;

}
