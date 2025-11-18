package com.sib.ibanklosucl.utilies;
public class Constants {

    public class Messages{

        public static final String SESSION_INVALID = "Session Expired!!.Kindly retry";
        public static final String INPUT_ERROR = "It seems the inputs you entered are wrong !!!";
        public static final String SOMETHING_ERROR = "Something went wrong. Please try after some time";
        public static final String DEMOGRAPHIC_FAILED = "Aadhaar Demographic Failed!!";
        public static final String BPM_VL_ERROR = "Error While Creating DOC Tray";
        public static final String CIF_ERROR = "Error While Fetching Customer Details From CBS";

        public static final String SERVICE_UNAVAILABLE = "Service not available. Please try after some time";
        public static final String FEATURE_NOT_AVAILABLE = "This feature is not available now. Please try after some time";
        public static final String CACHE_ERROR = "Please try after clearing browser history/cache";
        public static final String CAPTCHA_ERROR = "Incorrect captcha";
        public static final String NO_RECORDS = "No Reocrds Found";
        public static final String ACCOUNT_NOTEXIST="Account number doesnt exist";
        public static final String MOB_CHECK="Mobile Number differs, Kindly Check";
        public static final String NRI_BLOCK="Only resident customers are allowed to proceed";
        public static final String CONST_CODE="Only Retail domestic customer are allowed to proceed, Kindly contact nearest branch";
        public static final String MINOR_CUST="Minor customers are not allowed to proceed, Kindly contact your nearest branch";
        public static final String MINOR_MAJOR="Minor to major conversion is not allowed as operative accounts are not self-operated";
        public static final String MAX_RETRY="OTP Sent Failed.Kindly Retry Again after 10 minutes as maximum number of retries exceeds";
        public static final String OTP_RESENT="OTP resent successfully. Attempts left:";
        public static final String OTP_RESENT_MAX="OTP Resent Limit Exceeded !!";
        public static final String PAN_DOB_MISMATCH="Date of birth in PAN is different, Kindly contact nearest branch for proceeding with modification request.";
        public static final String PAN_NAME_MISMATCH="Name in PAN is different, Kindly contact nearest branch for proceeding with modification request.";
        public static final String UID_NAME_MISMATCH="Name in Aadhaar is different, Kindly contact nearest branch for proceeding with modification request.";
        public static final String UID_DOB_MISMATCH=" Date of birth in Aadhaar differs. Kindly contact nearest branch for proceeding with modification request";
        public static final String UID_GEN_MISMATCH="Gender in Aadhaar differs. Kindly contact nearest branch for proceeding with modification request";
        public static final String UID_ISSUE="Invalid Response Received While Aadhaar Validation.";
        public static final String PAN_UID_NAME_MISMATCH=" “Name mismatch is there between Aadhaar and PAN, Kindly contact nearest branch”";
        public static final String PAN_INVALID="Please Enter Valid PAN Details.";
        public static final String DIFF_UID="Different Aadhaar exist in customer id, Kindly contact nearest branch";
        public static final String DIFF_PAN="Different PAN exist in customer id, kindly contact nearest branch";
        public static final String OTP_INVALID="OTP Validation Failed";
        public static final String OTP_EXPIRED="Your OTP has Expired";
        public static final String REQUEST_INVALID="Invalid Request !!";
        public static final String PIN_MISMATCH="Please Enter Valid PINCODE in Communication Address!!";
        public static final String STATE_PIN_MISMATCH="The combination of State & Pincode in Communication Address selected is not correct!!";
        public static final String CITY_PIN_MISMATCH="The combination of City & Pincode in Communication Address selected is not correct!!";
        public static final String BLACKLIST_MATCH="Modification through VKYC is not allowed, Kindly contact nearest branch";
        public static final String DEDUP_MATCH="Duplicate customer id exist, Kindly contact nearest branch";

        public static final String VKYC_ERROR="Error While Redirecting to Agent. Please try after some time";
         public static final String MAKER_CHECKER_MISMATCH="Maker and Checker for the Workitem cannot be same person.";
    }
    public class GeneralTabMessages{
        public static final String CUST_ERROR="Kindly Enter Customer ID";
        public static final String STAFF_CUSTID="Customer ID of STAFF is  not Allowed";
        public static final String CUST_ID_DUP="Customer ID Already Exist in the WI";
        public static final String PARAM_CHANGE="The Custid/Res. status/Customer Status cannot not be changed after saved!!";
        public static final String PAN_NOT_FOUND="PAN not updated in the Customer ID !!";
    }
    public class KYCTabMessages{
        public static final String PAN_MISMATCH="Entered PAN does not match with the data in Finacle.";
        public static final String AADHAAR_MISMATCH="Entered Aadhaar number does not match with the data in Finacle.";
        public static final String UID_NOT_FOUND="Aadhaar not updated in the Customer ID !!";
        public static final String PASSPORT_MISMATCH="Entered Passport number does not match with the data in Finacle.";
        public static final String DOB_MISMATCH="Entered Date of Birth does not match with the data in Finacle.";
        public static final String NAME_MISMATCH="Entered Name does not match with the data in Finacle.";
        public static final String KYC_NOT_COMPLIED="KYC NOT COMPLIED IN CUSTOMER ID.";
        public static final String AGE_NOT_ELIGIBLE="AGE Eligibilty Check Failed";

    }



}
