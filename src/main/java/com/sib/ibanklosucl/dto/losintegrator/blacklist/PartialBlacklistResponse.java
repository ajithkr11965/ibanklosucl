package com.sib.ibanklosucl.dto.losintegrator.blacklist;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartialBlacklistResponse {
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
        private List<PartialBlacklistcheck> blacklistcheck;
    }
    @Getter
    @Setter
    public static class PartialBlacklistcheck {
        @JsonProperty("UID")
        private String UID;
        @JsonProperty("LASTNAME")
        private String LASTNAME;
        @JsonProperty("FIRSTNAME")
        private String FIRSTNAME;

        @JsonProperty("ALIASES")
        private String ALIASES;

        @JsonProperty("AGE")
        private String AGE;

        @JsonProperty("DOB")
        private String DOB;

        @JsonProperty("PLACEOFBIRTH")
        private String PLACEOFBIRTH;
        @JsonProperty("PASSPORTS")
        private String PASSPORTS;

        @JsonProperty("LOCATIONS")
        private String LOCATIONS;

        @JsonProperty("COUNTRIES")
        private String COUNTRIES;

        @JsonProperty("COMPANIES")
        private String COMPANIES;
        @JsonProperty("FURTHERINFORMATION")
        private String FURTHERINFORMATION;
    }
}
