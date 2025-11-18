package com.sib.ibanklosucl.dto;

import lombok.Data;

import java.util.Date;
@Data
public class VehicleLoanRBCPCDTO {
    private String wiNum;
    private String slNo;
    private String custName;
    private Date rcrDate;
    private String channel;
    private String delFlag;
    private String LockedBy;
    private String LockedFlag;
    private String lastLockUser;
    private String lastLockDate;
}
