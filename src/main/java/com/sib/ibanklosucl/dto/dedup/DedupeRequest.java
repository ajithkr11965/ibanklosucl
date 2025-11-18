package com.sib.ibanklosucl.dto.dedup;

import lombok.Data;

@Data
public class DedupeRequest {
    private String origin;
    private String workItemNumber;
    private String slno;
    private boolean mock;
    private String apiName;
    private boolean encFlag;
    private DedupeRequestBody request;
}

