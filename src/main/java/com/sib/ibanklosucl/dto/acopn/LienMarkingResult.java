package com.sib.ibanklosucl.dto.acopn;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LienMarkingResult {
    private String account_number;
    private String status;
    private String message;
}
