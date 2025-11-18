package com.sib.ibanklosucl.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RBCPCMakerSave {
    private Long slno;
    private String winum;
    private String decision;
    private  String preDisbursementCondition;
    private String remarks;
     private String rbcpcCheckerUser;
}
