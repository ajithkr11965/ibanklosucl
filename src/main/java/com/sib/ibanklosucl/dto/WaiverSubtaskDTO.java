package com.sib.ibanklosucl.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WaiverSubtaskDTO {
      private String wiNum;
    private Long slno;
    private String custName;
    private Date riRcreDate;
    private String channel;
    private String lockFlg;
    private String lockedBy;
    private String status;
    private String taskType;
}
