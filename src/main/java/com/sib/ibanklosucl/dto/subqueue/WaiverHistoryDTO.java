package com.sib.ibanklosucl.dto.subqueue;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WaiverHistoryDTO {
    private String decision;
    private BigDecimal requestedSpread;
    private BigDecimal sanctionedSpread;
    private BigDecimal revisedRoi;
    private BigDecimal revisedEmi;
    private String remarks;
    private String sanctionRemarks;
    private Date lastModDate;
    private String lastModUser;

    private String feeCode;
    private String feeName;
    private BigDecimal feeValue;
    private BigDecimal feeValueRec;
    private BigDecimal feeSancValue;
    private BigDecimal finalFee;
    private String waiverFlag;
    private String createUser;
    private String createdDate;
    private String completedUser;
    private String completedDate;
    private String taskStatus;
    private String taskId;

}
