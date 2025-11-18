package com.sib.ibanklosucl.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DkDataDTO {
    private String score;
    private Date cmdDate;
    private String delinquencyAnalysis;
    private String raceScore;
}

