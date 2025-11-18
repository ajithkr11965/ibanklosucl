package com.sib.ibanklosucl.dto.dedup;

import lombok.Data;

@Data
public class DedupeResponseWrapper {
    private DedupeResponseStatus status;
    private DedupeResponseBody body;
}
