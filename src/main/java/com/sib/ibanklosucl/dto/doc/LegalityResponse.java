package com.sib.ibanklosucl.dto.doc;

import lombok.Data;
import java.util.List;

@Data
public class LegalityResponse {

    private List<Message> messages;
    private int status;
    private data data;


    @Data
    public static class data{
        private String documentId;
        private String file;
        private String status;
        private String creationDate;
        private String completionDate;
        private String irn;
        private List<Invitee> invitees;
        private List<invitations> invitations;
    }

    // Getters and Setters

    @Data
    public static class Invitee {
        private String name;
        private String email;
        private String phone;
        private String signUrl;
        private boolean active;
        private String expiryDate;



    }
    @Data
    public static class invitations {
        private String name;
        private String email;
        private String phone;
        private String signUrl;
        private boolean active;
        private boolean signed;
        private boolean rejected;
        private boolean expired;
        private String creationDate;
        private String expiryDate;
        private String signDate;


    }

    @Data
    public static class Message {
        private String code;
        private String message;

    }

   public boolean isCompleted(){
        return "COMPLETED".equalsIgnoreCase(this.getData().getStatus());
    }

}
