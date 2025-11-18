package com.sib.ibanklosucl.dto.losintegrator.cif;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TdsResponse {
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
        @JsonProperty("tdscode")
        private String tdscode;
    }

}
