package com.sib.ibanklosucl.dto.losintegrator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FIRequest {

    private Request request;
    private boolean mock;
    private String apiName;
    private String slno;
    private String workItemNumber;
    @Getter
    @Setter
    public static class Request {
        @JsonProperty("merchantCode")private String merchantCode;
        @JsonProperty("UUID")private String UUID;
        @JsonProperty("merchantName")private String merchantName;
        @JsonProperty("PartTrnRec")private PartTrnRec[] PartTrnRec;
    }

    @Getter
    @Setter
    public static class PartTrnRec{
        @JsonProperty("AcctId")private String AcctId;
        @JsonProperty("ValueDt")private String ValueDt;
        @JsonProperty("expiry_date")private String expiry_date;
        @JsonProperty("tod_user_id")private String tod_user_id;
        @JsonProperty("TrnAmt")private String TrnAmt;
        @JsonProperty("discrete_advn_catgr")private String discrete_advn_catgr;
        @JsonProperty("tod_event_type")private String tod_event_type;
        @JsonProperty("TrnParticulars")private String TrnParticulars;
        @JsonProperty("tod_lev_int_flg")private String tod_lev_int_flg;
        @JsonProperty("grant_tod_flg")private String grant_tod_flg;
        @JsonProperty("grant_date")private String grant_date;
        @JsonProperty("refnum")private String refnum;
        @JsonProperty("PartTrnRmks")private String PartTrnRmks;
        @JsonProperty("tran_header_remarks")private String tran_header_remarks;
        @JsonProperty("tod_entity_type")private String tod_entity_type;
        @JsonProperty("tod_amt_grntd_crncy")private String tod_amt_grntd_crncy;
        @JsonProperty("remarks2")private String remarks2;
        @JsonProperty("discrete_advn_type")private String discrete_advn_type;
        @JsonProperty("tod_amt_grntd")private String tod_amt_grntd;
        @JsonProperty("currencyCode")private String currencyCode;
        @JsonProperty("penal_date")private String penal_date;
        @JsonProperty("CreditDebitFlg")private String CreditDebitFlg;

    }
}