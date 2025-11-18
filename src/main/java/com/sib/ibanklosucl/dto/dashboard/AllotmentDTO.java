package com.sib.ibanklosucl.dto.dashboard;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AllotmentDTO {

    private String wiNum;

    private String slno;
    private String loanAmt;
    private String channel;
    private String queueDate;
    private String custName;
    private String status;
    private String brName;
    private String solId;
    private String regName;
    private String regCode;
    private String allotedPpc;
    private String previousQueue;
    private String allotQueueDate;
    private String cpcQueueDate;

}
