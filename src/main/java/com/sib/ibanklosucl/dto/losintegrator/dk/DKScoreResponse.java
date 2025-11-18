package com.sib.ibanklosucl.dto.losintegrator.dk;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DKScoreResponse {
    private String message;
    private List<DKScoreItem> dkScoreItems;

    @Data
    @Getter
    @Setter
    public static class DKScoreItem {
        private String applicantType;
        private String applicantName;
        private Date runDate;
        private String status;
        private String score;  // Bureau Score
        private String raceScore;
        private String errorReason;
        private String color;
        private String scoreRange;
    }
}
