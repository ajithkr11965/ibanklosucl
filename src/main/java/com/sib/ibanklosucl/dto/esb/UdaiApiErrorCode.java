package com.sib.ibanklosucl.dto.esb;

public enum UdaiApiErrorCode {

    PI_ATTRIBUTES_MISMATCH("100", "The provided Personal Identity information didn't match. Please re-enter your<name, lname, gender, dob, dobt, age, phone, email> details correctly"),
    BIOMETRIC_MISMATCH("400", "Resident must ensure that the correct OTP value is provided for authentication .The resident is advised to generate new OTP in case of repeated authentication failure"),
    DUPLICATE_FINGERS("998", "Ensure you have entered correct Aadhaar number. Please retry with correct Aadhaar number after sometime."),
    DUPLICATE_IRIS("997", "Your Aadhaar is suspended, Resident shall visit Permanent Enrolment Centre with document proofs and follow the enrolment update process.AUA/Resident can reach UIDAI contact center for more details"),
    FMR_FIR_CONFLICT("996", "Your Aadhaar is cancelled , Resident shall visit Permanent EnrolmentCentre with document proofs and follow the re-enrollment process..AUA/Resident can reach UIDAI contact center for more details.");

    private final String code;
    private final String suggestion;

    UdaiApiErrorCode(String code, String suggestion) {
        this.code = code;
        this.suggestion = suggestion;
    }

    
    public String getCode() {
        return code;
    }


    public String getSuggestion() {
        return suggestion;
    }

    public static String fromCode(String code) {
        for (UdaiApiErrorCode errorCode : UdaiApiErrorCode.values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode.getSuggestion();
            }
        }
      return "Ensure you have entered correct Aadhaar number. Please retry with correct Aadhaar number after sometime.";
    }

    @Override
    public String toString() {
        return String.format("Error Code: %d - %s. Suggestion: %s", code, suggestion);
    }
}

