package com.sib.ibanklosucl.dto.losintegrator.WI;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WIResult {
    public String code;
    public String desc;
    private String cifId;
}
