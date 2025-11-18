package com.sib.ibanklosucl.dto.mssf;

import com.sib.ibanklosucl.dto.DOC_ARRAY;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MssfDocProcessResponse {
    private String status;
    private int documentCount;
    private String message;
    private List<DOC_ARRAY> documents;
}
