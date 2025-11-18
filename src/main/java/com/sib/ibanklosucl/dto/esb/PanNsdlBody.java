package com.sib.ibanklosucl.dto.esb;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PanNsdlBody {
    private String UUID;
    private String merchantCode;
    private String merchantName;
    private String pan;
    private String name;
    private String dob;

}
