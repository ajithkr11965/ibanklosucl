package com.sib.ibanklosucl.dto.dedup;

import lombok.Data;

@Data
public class DedupeRejectRequest {
    private Long applicantId;
    private String remarks;
}
