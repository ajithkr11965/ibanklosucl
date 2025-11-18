package com.sib.ibanklosucl.dto.bre;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
public class AmberDeviationUpdateRequest {
    private String wiNum;
    private String slno;
    private List<AmberData> amberData;
}
