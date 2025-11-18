package com.sib.ibanklosucl.dto.losintegrator;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class HunterReviewRequest {
        private Long slno;
    private String wiNum;
    private List<HunterReviewItem> hunterReviews;
    private String reviewUser;
    private String reviewDate;

}
