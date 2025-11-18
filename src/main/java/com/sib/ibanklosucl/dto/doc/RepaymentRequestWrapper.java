package com.sib.ibanklosucl.dto.doc;

import lombok.Data;

@Data
public class RepaymentRequestWrapper {
    private RepaymentDTO repaymentDTO;
    private String remarks;
}
