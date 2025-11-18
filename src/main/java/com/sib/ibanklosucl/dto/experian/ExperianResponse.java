package com.sib.ibanklosucl.dto.experian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ExperianResponse {
    private String status;
    private String msg;
    private String errorReason;
    private String jsonResponse;
    private String pdfResponse;
    private String experiaCCIRJsonReport;
    private int score;
    private String pdf;
    private String fetchTime;
    @Schema(description = "experian_ino")
    private String experian_ino;

    private String  newtoCredit;
}
