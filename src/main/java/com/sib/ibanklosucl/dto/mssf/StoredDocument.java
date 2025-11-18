package com.sib.ibanklosucl.dto.mssf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
    @Builder
    @AllArgsConstructor
@NoArgsConstructor
    public class StoredDocument {
        private String fileName;
        private String documentType;
        private String docTypeId;
    }
