package com.sib.ibanklosucl.dto;

import lombok.Data;

@Data
public class LoanVehicleDto {

    private String   makeName;
    private String modelName;
    private String vehicleAmt;
    private String tenor;
    private String loanAmt;
    private String insVal;
    private String insType;
    private String insAmt;

}
