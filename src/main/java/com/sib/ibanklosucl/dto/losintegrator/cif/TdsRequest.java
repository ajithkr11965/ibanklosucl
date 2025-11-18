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
public class TdsRequest {
    private Request request;
    private boolean mock;
    private String apiName;
    private String workItemNumber;
    private String applicantId;
    private String slno;
    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class Request {
        @JsonProperty("UUID")
        private String UUID="";
        @JsonProperty("DOB")
        private String DOB="";

        @JsonProperty("PAN")
        private String PAN="";
        @JsonProperty("CIF_ID")
        private String CIF_ID="";
        @JsonProperty("CORP_FLG")
        private String CORP_FLG="";
        @JsonProperty("NRE_STATUS")
        private String NRE_STATUS="";
        @JsonProperty("SNRCTZN_STAT")
        private String SNRCTZN_STAT="";
        @JsonProperty("AGE80_STAT")
        private String AGE80_STAT="";

    }
}
