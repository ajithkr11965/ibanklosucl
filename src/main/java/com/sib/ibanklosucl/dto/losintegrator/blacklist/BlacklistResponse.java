package com.sib.ibanklosucl.dto.losintegrator.blacklist;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistResponse {
    @JsonProperty("Response")
    private Response response;
    @Getter
    @Setter
    public static class Response {
        @JsonProperty("Header")
        private Header header;
        @JsonProperty("Status")
        private Status status;
         @JsonProperty("Body")
        private Body body;
    }
    @Getter
    @Setter
    public static class Header {
        @JsonProperty("Timestamp")
        private String timestamp;
        @JsonProperty("APIName")
        private String apiName;
        @JsonProperty("APIVersion")
        private String apiVersion;
        @JsonProperty("Interface")
        private String interfaceName;
    }
    @Getter
    @Setter
    public static class Status {
        @JsonProperty("Code")
        private String code;
        @JsonProperty("Desc")
        private String desc;
    }
    @Getter
    @Setter
    public static class Body {
        private List<Blacklistcheck> blacklistcheck;
    }
    @Getter
    @Setter
    public static class Blacklistcheck {
        private String uid;
        private String lastname;
        private String firstname;
        private String aliases;
        private String age;
        private String dob;
        private String placeofbirth;
        private String passports;
        private String locations;
        private String countries;
        private String companies;
    }
}
