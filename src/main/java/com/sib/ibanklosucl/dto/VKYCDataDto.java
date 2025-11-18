package com.sib.ibanklosucl.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VKYCDataDto {

        private AadhaarDetails aadhaarDetails;
        private PanDetails panDetails;
        private boolean communicationSameAsPermanent;
        private CustomerDetails customerDetails;
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AadhaarDetails {
            private String aadhaarName;
            private String aadhaarYob;
            private String aadhaarRefNo;
            private String aadhaarSex;
            private String aadhaarAddress;
            private String aadhaarPdf;
            private String aadhaarImage;
        }
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class PanDetails {
            private String panCardName;
            private String panCardNum;
            private String panHolderTitle;
            private String nameOnCard;
            private String aadhaarSeedingStatus;
        }
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CustomerDetails {
            private String name;
            private String gender;
            private String systemIp;
            private String branch_sol_id;
            private String occupation;
            private String annualIncome;
            private String fatherName;
            private String motherName;
            private String language;
            private String latitude;
            private String longitude;
            private String maritalStatus;
            private String spouseName;
            private String permanentAddressline1;
            private String permanentAddressline2;
            private String permanentCity;
            private String permanentCityCode;
            private String permanentCountry;
            private String permanentCountryCode;
            private String permanentState;
            private String permanentStateCode;
            private String permanentPincode;
            private String permanentAddress;
            private String communicationAddressline1;
            private String communicationAddressline2;
            private String communicationCity;
            private String communicationCityCode;
            private String communicationState;
            private String communicationStateCode;
            private String communicationCountry;
            private String communicationCountryCode;
            private String communicationPincode;
            private String communicationAddress;
            private String mobileNum;
            private boolean priorityStatus;
            private String applicationDate;
            private String accountScheme;
            private String email;
        }


}
