package com.sib.ibanklosucl.dto.doc;

import lombok.Data;

import java.util.List;

@Data
public class LegalityDto {
    private String profileId;
    private FileDTO file;
    private List<InviteeDTO> invitees;
    private NeslDataDTO neslData;
    private String irn;
    @Data
    public static  class FileDTO {

        private String name;
        private String file;

    }

    @Data
    public static  class InviteeDTO {

        private String name;
        private String email;
        private String phone;

        // Getters and Setters
    }

    @Data
    public static  class NeslDataDTO {

        private DocumentDetailDTO documentDetail;
        private List<ParticipantDTO> participants;
        private List<NeslSecurityDTO> neslSecurities;
        private List<StampDataDTO> stampData;
        private List<NeslPartyDTO> neslParties;

        // Getters and Setters
    }

    @Data
    public static  class DocumentDetailDTO {

        private String loanNumber;
        private String sanctionNumber;
        private String registrationType;
        private String state;
        private String branchName;
        private String branchAddress;
        private String dateOfSanction;
        private String emiAmount;
        private String rateOfInterest;
        private String sanctionAmount;
        private String tenure;
        private String typeOfDebt;
        private String accountClosedFlag;
        private String fundType;
        private String sanctionCurrency;
        private String creditSubtype;
        private String facilityName;
        private String amountOverdue;
        private String otherChargeAmount;
        private String debtStartDate;
        private String interestAmount;
        private String oldDebtRefNo;
        private String principalOutstanding;
        private String loanRemark;
        private String totalOutstandingAmount;
        private String creditorBusinessUnit;
        private String drawingPower;
        private String daysPastDue;
        private String docRefNo;
        private String event;
        private String expiryDateEbg;
        private String currencyOfDebt;
        private String claimExpiryDate;
        private String contractRefNo;
        private String vendorCode;
        private String portalID;

        // Getters and Setters
    }

    @Data
    public static  class ParticipantDTO {

        private String fullName;
        private String contactPersonName;
        private String contactRelation;
        private String emailId;
        private String mobileNumber;
        private String dob;
        private String legalConstitution;
        private String alternateEmailId;
        private String alternateMobileNumber;
        private String officialDocType;
        private String officialDocId;
        private String registeredAddress;
        private String registeredPinCode;
        private String designation;
        private String communicationAddress;
        private String communicationAddressPinCode;
        private String cin;
        private String kin;
        private String partyType;
        private String isIndividual;
        private String signatoryGender;
        private String businessUnit;

        // Getters and Setters
    }

    @Data
    public static  class NeslSecurityDTO {
        // Define fields if needed
        // Getters and Setters
    }

    @Data
    public static  class StampDataDTO {

        private String firstParty;
        private String secondParty;
        private String stampDutyAmount;
        private String considerationPrice;
        private String descriptionOfDocument;
        private String stampDutyPaidBy;
        private String articleCode;
        private String firstPartyPin;
        private String secondPartyPin;
        private String firstPartyOVDType;
        private String firstPartyOVDValue;
        private String secondPartyOVDType;
        private String secondPartyOVDValue;

        // Getters and Setters
    }

    @Data
    public static  class NeslPartyDTO {
        // Define fields if needed
        // Getters and Setters
    }

}
