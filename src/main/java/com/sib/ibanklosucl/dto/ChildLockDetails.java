package com.sib.ibanklosucl.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChildLockDetails {
    private String wiNum;
    private String taskType;
    private String lockedBy;
    private LocalDateTime lockedOn;
    private String lockFlag;
}
