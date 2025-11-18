package com.sib.ibanklosucl.dto.bre;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AmberData {
    private Long id;
    private String amberCode;
    private String amberDesc;
    private String color;
    private String deviationType;
    private String approvingAuth;
    private String doRemarks;
    private String parameterRange;
    private String parameterValue;
    private String approveCode;
    private String approveComments;
    private String applicantId;
    private String applicantName;
    private String applicantType;

}
