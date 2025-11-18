package com.sib.ibanklosucl.dto.losintegrator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sib.ibanklosucl.dto.CustDedup;
import com.sib.ibanklosucl.dto.LosDedupeRequestDTO;
import com.sib.ibanklosucl.dto.doc.LegalityDto;
import com.sib.ibanklosucl.dto.esb.FetchDetailsBody;
import com.sib.ibanklosucl.dto.esb.PanNsdlBody;
import com.sib.ibanklosucl.dto.esb.UIDDemographicBody;
import com.sib.ibanklosucl.dto.ocr.FileRequest;
import lombok.*;


public class LosRequest {
    @Data
    public static class OCR
    {
        private FileRequest request;
        private boolean mock=false;
        private String apiName="karzaValidation";

        private String workItemNumber;
        private String origin;

    }
    @Data
    public static class LOSDedup
    {
        private LosDedupeRequestDTO request;
        private boolean mock=false;
        private String apiName="bpmLOSDedupCheck";

        private String workItemNumber;
        private String origin;

    }
    @Data
    public static class PanNsdl
    {
        private PanNsdlBody request;
        private boolean mock=false;
        private String apiName="panNsdlValidate";

        private String workItemNumber;
        private String origin;
        private String slno;


    }
    @Data
    public static class UIDDemographic
    {
        private UIDDemographicBody request;
        private boolean mock=false;
        private String apiName="UiddemoGraphic";

        private String workItemNumber;
        private String origin;
        private String slno;


    }
    @Data
    public static class FinacleDedup
    {
        private CustDedup request;
        private boolean mock=false;
        private String apiName="finacleDedupCheck";

        private String workItemNumber;
        private String origin;

    }
    @Data
    public static class CIFView
    {
        private FetchDetailsBody request;
        private boolean mock=false;
        private String apiName="finacleCIFView";
        private String workItemNumber;
        private String origin;
    }
    @Data
    public static class LegalityRequest
    {
        private LegalityDto request;
        private boolean mock=false;
        private String apiName="fetchLegality";
        private String workItemNumber;
        private String origin;

        private boolean encFlag=false;
    }
    @Data
    public static class LegalityStatusRequest
    {
        private LegalityDto request;
        private boolean mock=false;
        private String apiName="legalStatus";
        private String workItemNumber;
        private String slno;
        private String origin;
        private String documentId;

    }
    @Data
    public static class BureauRequest
    {
        private BureauRequestDTO request;
        private boolean mock=false;
        private String apiName="bureauCheck";
        private String workItemNumber;
        private String slno;
        private String origin;

    }
    @Data
    public static class BureauRequestDTO
    {
        private String pan;
        @JsonProperty("UUID")
        private String uuid;
        private String merchantCode;
        private String merchantName;

    }


}
