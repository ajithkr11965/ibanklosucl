package com.sib.ibanklosucl.dto.subqueue;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LockStatusDTO {
    private boolean roiLocked;
    private boolean chargeLocked;
    private String roiLockedBy;
    private String chargeLockedBy;
    public boolean isAnyLocked() {
        return roiLocked || chargeLocked;
    }
    public boolean canAccess(boolean hasRoiAccess, boolean hasChargeAccess) {
        if (hasRoiAccess && hasChargeAccess) {
            return !roiLocked && !chargeLocked;
        } else if (hasRoiAccess) {
            return !roiLocked;
        } else if (hasChargeAccess) {
            return !chargeLocked;
        }
        return false;
    }
}
