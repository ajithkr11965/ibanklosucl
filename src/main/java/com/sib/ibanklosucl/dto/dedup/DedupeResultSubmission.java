package com.sib.ibanklosucl.dto.dedup;

import lombok.Data;

@Data
public class DedupeResultSubmission {
    private Long applicantId;
    private String relation;
    private String status;
}
