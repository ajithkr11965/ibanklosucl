package com.sib.ibanklosucl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CommonDTO {
    private FileUploadForm fileUploadForm;

    private ExperianForm experianForm;

    private DocUpload docRequest;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExperianForm{
        private String appid;
        private String slno;
        private String winum;
        private String reqtype;
        private BigDecimal exptenure;
        private BigDecimal expLoanAmt;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DocUpload{
        private String fileName;
        private String fileExtension;
        private String base64Data;
        private String winum;
        private String foldername;
        private String remarks;
    }
}
