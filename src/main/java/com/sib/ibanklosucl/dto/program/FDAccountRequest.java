package com.sib.ibanklosucl.dto.program;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FDAccountRequest {
    private String slno;
    private String applicantId;
    private String wiNum;
    private String cifId;
    private String sibCustomer;
    private String residentialStatus;
    private String fdAaccountNumber;
    private String lastModUser;
     private String homeSol;
     private String reqIpAddr;
}
