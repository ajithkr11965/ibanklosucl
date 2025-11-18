package com.sib.ibanklosucl.dto;

import lombok.Data;

import java.util.List;
@Data
public class LosApiResponse {
    private Response Response;

    @Data
    public static class Response {
        private Header Header;
        private Status Status;
        private Body Body;

        // Getters and setters
    }
    @Data
    public static class Header {
        private String Timestamp;
        private String APIName;
        private String APIVersion;
        private String Interface;

        // Getters and setters
    }
@Data
    public static class Status {
        private String Code;
        private String Desc;

        // Getters and setters
    }
@Data
    public static class Body {
        private List<BpmdedupeData> bpmdedupeData;
        private List<customer> customer;

        private String message;

    }
@Data
    public static class BpmdedupeData {
        private String wi_name;
        private String custName;
        private String loanType;
        private String wiStatus;
        private String AppType;
        private String rejectReason;
        private String doRemarks;
        private String DOB;
        private String aadhaar;
        private String panNo;
        private String voterID;
        private String passportNo;
        private String driveLic;
        private String gstNo;
        private String CorpID;
        private String message;


    }


@Data
    public static class customer {
        private String created_channel_id;
        private String suspend_status;
        private String customerid;
        private String emailid;
        private String mobilephone;
        private String voterid;
        private String aadhar_ref_no;
        private String pan;
        private String dob;
        private String name;
        private String tds_customerid;


    }
}
