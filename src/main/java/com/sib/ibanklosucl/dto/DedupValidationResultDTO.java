package com.sib.ibanklosucl.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class DedupValidationResultDTO {
    private boolean isValid;
    private List<ApplicantDedupStatus> applicantStatuses;
    @Data
    @Getter
    @Setter
    public static class ApplicantDedupStatus {
        private Long applicantId;
        private String applicantName;
        private boolean finacleDedupDone;
        private boolean losDedupDone;
    }
}
