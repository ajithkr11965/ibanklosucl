package com.sib.ibanklosucl.dto.dashboard;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RemarksHistDTO {

    private String cmdate;
    private String cmuser;
    private String ppcName;
    private String solId;
    private String fromQueue;
    private String toQueue;
    private String remarks;
    private String slno;
    private String wiNum;
    private String regName;
    private String brName;
    private String assignUser;

}
