package com.sib.ibanklosucl.dto.losintegrator;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccOpeningResult {
    public String code;
    private String desc;
    private String accNo;
}