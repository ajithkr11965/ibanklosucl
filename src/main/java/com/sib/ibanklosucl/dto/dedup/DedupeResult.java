package com.sib.ibanklosucl.dto.dedup;

import lombok.Data;

import java.util.List;

@Data
public class DedupeResult {
    private boolean matched;
    private String matchReason;
    private List<DedupeMatch> matches;
}
