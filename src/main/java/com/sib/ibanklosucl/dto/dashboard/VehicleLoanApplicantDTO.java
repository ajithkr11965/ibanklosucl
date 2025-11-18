package com.sib.ibanklosucl.dto.dashboard;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class VehicleLoanApplicantDTO {

   private Long applicantId;
    private String applicantType;
    private String applName;
    private Date applDob;
    private double progress;
    private String genComplete;
    private String kycComplete;
    private String basicComplete;
    private String employmentComplete;
    private String incomeComplete;
    private String creditComplete;
    private String financialComplete;
    private String vehicleComplete;
    private String insuranceComplete;
    private String loanComplete;
    private String blCheckCompleted;
    private String hunterCheckCompleted;

}
