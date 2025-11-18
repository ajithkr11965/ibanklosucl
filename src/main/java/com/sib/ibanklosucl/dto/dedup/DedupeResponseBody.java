package com.sib.ibanklosucl.dto.dedup;

import lombok.Data;

import java.util.List;

@Data
public class DedupeResponseBody {
    private List<DedupeMatch> matches;
}