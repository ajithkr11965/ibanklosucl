package com.sib.ibanklosucl.dto.ocr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OCRResponse {
    private String status;
    private String errorMessage;
    private String documentProcessed;
    private List<Document> documents;

    // Getters and setters
    @Getter
    @Setter
    public static class Document {
        private String documentType;
        private String documentPageType;
        private String documentNumber;
        private OCRData ocrData;
        private AdditionalDetails additionalDetails;

        // Getters and setters
    }
    @Getter
    @Setter
    public static class OCRData {
        private String dob;
        private String father;
        private String name;
        private String pan;
        private String doi;
        private String yob;
        private String gender;
        private String relationName;
        private String voterId;
        private String address;
        private String givenName;
        private String aadhaar;
        private String passportNumber ;
        private String doe;

    }
    @Getter
    @Setter
    public static class AdditionalDetails {
        private AddressSplit addressSplit;

        // Getters and setters
    }
    @Getter
    @Setter
    public static class AddressSplit {
        private String building;
        private String city;
        private String district;
        private String pin;
        private String floor;
        private String house;
        private String locality;
        private String state;
        private String street;
        private String complex;
        private String landmark;
        private String untagged;

        // Getters and setters
    }


}
