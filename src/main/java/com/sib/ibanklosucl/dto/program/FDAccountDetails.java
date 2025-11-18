package com.sib.ibanklosucl.dto.program;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FDAccountDetails {
    private BigDecimal fdAmount;
    private String fdAccNo;
    private String accountName;
    private String fdStatus;
    private BigDecimal depositAmount;
    private String accountType;
    private String cifIds;
    private String acid;
    private Date accountOpenDate;
    private Date maturityDate;
    private BigDecimal maturityAmount;
    private BigDecimal fsldAdjAmount;
    private BigDecimal eligFDAmount;

}
