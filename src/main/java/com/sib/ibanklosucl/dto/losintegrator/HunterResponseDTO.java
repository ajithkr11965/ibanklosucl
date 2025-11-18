package com.sib.ibanklosucl.dto.losintegrator;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
public class HunterResponseDTO {
    private String status;
    private String decision;
    private int score;
    boolean matchfound;
    boolean reviewed;
    private List<String> warnings;
    private String totalMatchScore;
    private List<String> ruleIds;  // New field for Rule IDs
    private Date reviewDate;       // New field for Review Remarks Date
    private String errorMessage;

}
