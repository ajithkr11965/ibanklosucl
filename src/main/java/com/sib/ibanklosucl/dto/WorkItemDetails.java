package com.sib.ibanklosucl.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class WorkItemDetails {
    private String wiNum;
    private String queue;
    private String status;
    private String lockStatus;
    private String lockedBy;
    private LocalDateTime lockedOn;
    private List<ChildLockDetails> childLocks;
    private String docMode;
}
