package com.sib.ibanklosucl.dto.losintegrator.cif;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class VehicleLoanCifRequest {
    private Request request;
    private boolean mock;
    private String apiName;
    private String workItemNumber;
    private String applicantId;
    private String slno;
    private String remarks;
    private String action;//APPROVE, REJECT
    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class Request {
        @JsonProperty("UUID")
        private String UUID="";
        @JsonProperty("sender_code")
        private String sender_code="";

        @JsonProperty("sender_name")
        private String sender_name="";
        @JsonProperty("channel_id")
        private String channel_id="";
        @JsonProperty("gender")
        private String gender="";
        @JsonProperty("salutation")
        private String salutation="";
        @JsonProperty("name")
        private String name="";
        @JsonProperty("cust_dob")
        private String cust_dob="";
        @JsonProperty("guardian_cust_id")
        private String guardian_cust_id="";
        @JsonProperty("customernreflg")
        private String customernreflg="";
        @JsonProperty("branch_code")
        private String branch_code="";
        @JsonProperty("pan")
        private String pan="";
        @JsonProperty("aadhaar")
        private String aadhaar="";//reference number will passed here
        @JsonProperty("mobile")
        private String mobile="";
        @JsonProperty("email")
        private String email="";
        @JsonProperty("father_name")
        private String father_name="";
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @JsonProperty("spouse_name")
        private String spouse_name="";
        @JsonProperty("mother_name")
        private String mother_name="";
        @JsonProperty("marital_status")
        private String marital_status="";
        @JsonProperty("occupation")
        private String occupation="";
        @JsonProperty("annual_income")
        private String annual_income="";
        @JsonProperty("tds_tbl_code")
        private String tds_tbl_code="";
        @JsonProperty("constitution_code")
        private String constitution_code="";
        @JsonProperty("Phone_Num_CountryCode")
        private String Phone_Num_CountryCode="";
        @JsonProperty("address")
        private address[] add;

    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class address {

        @JsonProperty("address_type")
        private String address_type="";
        @JsonProperty("address_line1")
        private String address_line1="";
        @JsonProperty("address_line2")
        private String address_line2="";
        @JsonProperty("address_line3")
        private String address_line3="";
        @JsonProperty("city")
        private String city="";
        @JsonProperty("country")
        private String country="";
        @JsonProperty("state")
        private String state="";
        @JsonProperty("zip")
        private String zip="";
        @JsonProperty("preferred_address")
        private String preferred_address="";
    }
}
