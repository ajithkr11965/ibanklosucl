package com.sib.ibanklosucl.dto.dashboard;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Data
@Getter
@Setter
public class VehicleLoanLockDTO  {
    String lockedBy;
    Date lockedOn;
    String lockFlg;
    String queue;
}