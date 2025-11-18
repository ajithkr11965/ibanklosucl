package com.sib.ibanklosucl.dto.dashboard;

import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class QueueCountDTO {


    private Integer entryPendingCount;
    private Integer verificationPendingCount;
    private Integer crtCount;
    private Integer crtAmberCount;
    private Integer rbcpcAllotmentCount;
    private Integer rbcpcMakerCount;
    private Integer deviationCount;
    private Integer rbcpcCheckerCount;
    private Integer brDocumentationCount;
    private Integer bogQueueCount;
    private Integer brSendBack;
    private Integer sanctionModification;
    private Integer accountOpening;
    private Integer waiverList;
    private Integer rejectedCount;
    private Integer hunterCount;


    public Integer getCountForMenu(String menuID) {
        switch (menuID) {
            case "BM": return getEntryPendingCount();
            case "BC": return getVerificationPendingCount();
            case "CA": return getCrtAmberCount();
            case "CS","CRTC": return getCrtCount();
            case "ALLOT": return getRbcpcAllotmentCount();
            case "RBCM": return getRbcpcMakerCount();
            case "DEV": return getDeviationCount();
            case "RBCC": return getRbcpcCheckerCount();
            case "DQ","BD": return getBrDocumentationCount();
            case "BS": return getBrSendBack();
            case "BOGQUEUE": return getBogQueueCount();
            case "ACOPN": return getAccountOpening();
            case "WAIVE": return getWaiverList();
            case "NIL": return getRejectedCount();
         //   case "WAIVE": return 5;
            case "SM": return getSanctionModification();
            case "HUNTER": return getHunterCount();
            default: return 0;
        }
    }


}
