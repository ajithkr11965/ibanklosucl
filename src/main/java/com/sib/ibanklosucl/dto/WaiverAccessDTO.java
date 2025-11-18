package com.sib.ibanklosucl.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WaiverAccessDTO {
    private boolean hasRoiAccess;
    private boolean hasChargeAccess;
}
