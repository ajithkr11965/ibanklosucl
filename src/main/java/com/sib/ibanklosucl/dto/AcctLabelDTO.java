package com.sib.ibanklosucl.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AcctLabelDTO {
    private String wiNum;
    private Long slno;
    private AcctLabel[] acctLabels;

    @Data
    public static class AcctLabel {
        private String acctLabel;
        private String labelText;
    }

    public void makeLabelTextUppercase() {
        if (acctLabels != null) {
            for (AcctLabel label : acctLabels) {
                if (label != null && label.getLabelText() != null) {
                    label.setLabelText(label.getLabelText().toUpperCase());
                }
            }
        }
    }

}

