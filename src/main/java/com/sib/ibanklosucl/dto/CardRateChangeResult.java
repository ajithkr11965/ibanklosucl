package com.sib.ibanklosucl.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardRateChangeResult {
    private boolean hasChanged;
    private BigDecimal savedCardRate;
    private BigDecimal currentCardRate;

}
