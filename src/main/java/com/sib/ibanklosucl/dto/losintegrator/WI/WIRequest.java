package com.sib.ibanklosucl.dto.losintegrator.WI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class WIRequest {
    private boolean mock;
    private String apiName;
    private String workItemNumber;
    private String origin;
    private String slno;
    private String remarks;
    private request request;


    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class request {

        @JsonProperty("createWorkItemRequest")
        private createWorkItemRequest createWorkItemRequest;
        @JsonProperty("customerDetails")
        private customerDetails[] customerDetails;

        @JsonProperty("accountDetails")
        private  accountDetails accountDetails;

        @JsonProperty("nomineeDetails")
        private nomineeDetails nomineeDetails;
        @JsonProperty("kycValidationDetails")
        private kycValidationDetails[] kycValidationDetails;

        @JsonProperty("documents")
        private documents[] documents;
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class createWorkItemRequest {
        @JsonProperty("URN")
        private String URN="";
        @JsonProperty("solId")
        private String solId="";

        @JsonProperty("typeOfCustomer")
        private String typeOfCustomer="";
        @JsonProperty("requestDateTime")
        private String requestDateTime  ="";
        @JsonProperty("requestFunction")
        private String requestFunction="";
        @JsonProperty("initiatedFrom")
        private String initiatedFrom="";
        @JsonProperty("username")
        private String username="";
        @JsonProperty("password")
        private String password="";
        @JsonProperty("processName")
        private String processName="";
        @JsonProperty("noOfCustomers")
        private String noOfCustomers="";
        @JsonProperty("kycPlaceOfDeclaration")
        private String kycPlaceOfDeclaration="";
        @JsonProperty("kycDateOfDeclaration")
        private String kycDateOfDeclaration="";
        @JsonProperty("kycVerifiedBy")
        private String kycVerifiedBy="";//reference number will passed here
        @JsonProperty("userID")
        private String userID="";
        @JsonProperty("typeOfAccount")
        private String typeOfAccount="";


    }



    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class documents {
        @JsonProperty("cid")private String cid="";
        @JsonProperty("docName")private String docName="";
        @JsonProperty("document")private String document="";
        @JsonProperty("docExtension")private String docExtension="";
    }
    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class kycValidationDetails {
        @JsonProperty("pan")private String pan="";
        @JsonProperty("panStatus")private String panStatus="";
        @JsonProperty("panAadharSeeded")private String panAadharSeeded="";
        @JsonProperty("panNameOnCard")private String panNameOnCard="";
        @JsonProperty("panDOB")private String panDOB="";
        @JsonProperty("panNameMatch")private String panNameMatch="";
        @JsonProperty("panDOBMatch")private String panDOBMatch="";

    }
    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class customerDetails {
        @JsonProperty("cid")
        private String cid="";
        @JsonProperty("customerId")
        private String customerId="";
        @JsonProperty("customerName")
        private String customerName="";
        @JsonProperty("NRE")
        private String NRE="";
        @JsonProperty("minor")
        private String minor="";
        @JsonProperty("poa")
        private String poa="";
        @JsonProperty("cpoa")
        private String cpoa="";
        @JsonProperty("kycAttestation")
        private String kycAttestation="";
        @JsonProperty("fatherName")
        private String fatherName="";
        @JsonProperty("motherName")
        private String motherName="";
        @JsonProperty("spouseName")private String spouseName="";
        @JsonProperty("nationality")private String nationality="";
        @JsonProperty("jurisdiction")private String jurisdiction="";
        @JsonProperty("residentialStatus")private String residentialStatus="";
        @JsonProperty("ckycReq")private String ckycReq="";
        @JsonProperty("phoneContactType")private String phoneContactType="";
        @JsonProperty("customerCreation")private String customerCreation="";

    }


    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class accountDetails {
        @JsonProperty("accountNo")
        private String accountNo="";
        @JsonProperty("typeOfAccount")private String typeOfAccount="";
        @JsonProperty("glCode")private String glCode="";
        @JsonProperty("schemeCode")private String schemeCode="";
        @JsonProperty("nomineeFlag")private String nomineeFlag="";
        @JsonProperty("atmCard")private String atmCard="";
        @JsonProperty("categorisation")private String categorisation="";
        @JsonProperty("ueidCode")private String ueidCode="";
        @JsonProperty("ueidName")private String ueidName="";
        @JsonProperty("accountCreation")private String accountCreation="";
        @JsonProperty("modeOfOperation")private String modeOfOperation="";

    }



    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class nomineeDetails {
        @JsonProperty("regNo")private String regNo="";
        @JsonProperty("title")private String title="";
        @JsonProperty("name")private String name="";
        @JsonProperty("dob")private String dob="";
        @JsonProperty("relation")private String relation="";
        @JsonProperty("nominationPercent")private String nominationPercent="";
        @JsonProperty("nomineeAddress1")private String nomineeAddress1="";
        @JsonProperty("nomineeAddress2")private String nomineeAddress2="";
        @JsonProperty("nomineeAddress3")private String nomineeAddress3="";
        @JsonProperty("nomineeCity")private String nomineeCity="";
        @JsonProperty("nomineeState")private String nomineeState="";
        @JsonProperty("nomineeCountry")private String nomineeCountry="";
        @JsonProperty("nomineePin")private String nomineePin="";
        @JsonProperty("isMinor")private String isMinor="";
        @JsonProperty("nomineeGuardianTitle")private String nomineeGuardianTitle="";
        @JsonProperty("nomineeGuardianName")private String nomineeGuardianName="";
        @JsonProperty("nomineeGuardianDob")private String nomineeGuardianDob="";
        @JsonProperty("nomineeGuardianCode")private String nomineeGuardianCode="";
        @JsonProperty("nomineeGuardianAddress1")private String nomineeGuardianAddress1="";
        @JsonProperty("nomineeGuardianAddress2")private String nomineeGuardianAddress2="";
        @JsonProperty("nomineeGuardianAddress3")private String nomineeGuardianAddress3="";
        @JsonProperty("nomineeGuardianCity")private String nomineeGuardianCity="";
        @JsonProperty("nomineeGuardianState")private String nomineeGuardianState="";
        @JsonProperty("nomineeGuardianCountry")private String nomineeGuardianCountry="";
        @JsonProperty("nomineeGuardianPin")private String nomineeGuardianPin="";

    }
}
