package com.sib.ibanklosucl.dto;
import lombok.*;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerVkycBasicDetailsDTO {

    private String applName;
    private String mobileNo;
    private String aadharRefNum;
    private Long applicantId;
    private String wiNum;
    private Long slno;
    private String aadharno;
    private String cifid;
    private String sibcustomer;
    private String cifcreationmode;
    private String panno;
    private String panname;
    private String pandob;


//    public CustomerVkycBasicDetailsDTO(String applName, String mobileNo, String aadharRefNum, Long applicantId, String wiNum, Long slno) {
//        this.applName = applName;
//        this.mobileNo = mobileNo;
//        this.applicantId = applicantId;
//        this.aadharRefNum = aadharRefNum;
//        this.wiNum = wiNum;
//        this.slno = slno;
//    }

}
