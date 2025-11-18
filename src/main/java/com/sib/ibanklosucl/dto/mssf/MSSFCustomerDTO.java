package com.sib.ibanklosucl.dto.mssf;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MSSFCustomerDTO {
    private String refNo;
    private String customerName;
    private Long mobile;
    private String email;
    private String dealerCode;
    private Double loanAmount;
    private String solId;
    private String dealerName;
    private String status;
    private String workItemNumber ;
    private String workItemStatus;
    private LocalDateTime createdDate;
    private MSSFLockDTO lockDetails;
}