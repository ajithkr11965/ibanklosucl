package com.sib.ibanklosucl.dto.dedup;

import lombok.Data;

@Data
public class DedupeRelationRequest {
    private Long applicantId;
    private String relation;
}
