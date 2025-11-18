package com.sib.ibanklosucl.dto.acopn;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class VLMas {
    private Long slno;
    private String wiNum;
    private String accOpened;
    private String accNumber;
    private Date accOpenDate;
}
