package com.sib.ibanklosucl.dto.program;

import com.sib.ibanklosucl.model.VehicleLoanFD;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class FDAccountResponse {
    private VehicleLoanFD vehicleLoanFD;
    private boolean eligible;
}
