package com.sib.ibanklosucl.dto.ocr;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcrParsed {
    private String status;
    private String errorMessage;

    public OcrParsed(String status,String errorMessage){
        this.status=status;
        this.errorMessage=errorMessage;
    }
    private PanDoc Pan;

    private Uid uid;
    private Passport passport;

    @Data
    public static class PanDoc {
        private String name;
        private String dob;
        private String pan;
    }
    @Data
    public static class Uid {
        private String name;
        private String aadhaar;
        private String gender;
        private String dob;
        private String documentProcessed;
    }
    @Data
    public static class Passport {
        private String passportNumber ;
        private String doe;
        private String givenName;
    }
}
