package com.sib.ibanklosucl.dto.acopn;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LienMarkingRequest {
    private String account_number;
    private String lien_amt;
    private String lien_reason_code;
    private String lien_remarks;
}
