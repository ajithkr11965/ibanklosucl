package com.sib.ibanklosucl.dto.dashboard;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class VehicleLoanMasterDTO {

    private Long slno;
    private String wiNum;
    private String custName;
    private String solId;
    private String queue;
    private String status;
    private String riRcreDate;
    private String queueDate;
    private String channel;
    private VehicleLoanLockDTO vehicleLoanLock;
    private String brName;
    private String roName;
    private String lastLockUser;
    private String lastLockDate;
//    private List<VehicleLoanApplicantDTO> applicants;
//    private VehicleLoanApplicantDTO mainApplicant;
//        private double averageProgress;
}
