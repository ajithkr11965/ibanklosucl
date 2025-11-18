package com.sib.ibanklosucl.dto.dedup;

import lombok.Data;

import java.util.List;

@Data
public class DedupeSubmitRequest {
    private List<DedupeResultSubmission> results;
}
