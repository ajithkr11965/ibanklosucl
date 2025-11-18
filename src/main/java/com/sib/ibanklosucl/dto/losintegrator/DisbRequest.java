package com.sib.ibanklosucl.dto.losintegrator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisbRequest {

    private Request request;
    private boolean mock;
    private String apiName;
    private String slno;
    private String workItemNumber;
    @Getter
    @Setter
    public static class Request {
        @JsonProperty("chargeFlag")private String chargeFlag;
        @JsonProperty("merchantCode")private String merchantCode;
        @JsonProperty("solId")private String solId;
        @JsonProperty("maker_user")private String maker_user;
        @JsonProperty("loan_acct_num")private String loan_acct_num;
        @JsonProperty("grossNetDisbt")private String grossNetDisbt;
        @JsonProperty("checker_user")private String checker_user;
        @JsonProperty("intrabank_data")private intrabank_data intrabank_data;
        @JsonProperty("chargeData")private chargeData chargeData;

        @JsonProperty("merchantName")private String merchantName;
        @JsonProperty("finalDisbFlg")private String finalDisbFlg;
        @JsonProperty("loan_disb_amt")private String loan_disb_amt;
        @JsonProperty("firstDisbFlg")private String firstDisbFlg;
        @JsonProperty("UUID")private String UUID;
        @JsonProperty("disbursementMode")private String disbursementMode;
    }

    @Getter
    @Setter
    public static class intrabank_data{

        @JsonProperty("additionalRemarks")private String additionalRemarks;
        @JsonProperty("customer_account_number")private String customer_account_number;
        @JsonProperty("customer_name")private String customer_name;
        @JsonProperty("remarks")private String remarks;
        @JsonProperty("tran_particulars")private String tran_particulars;
    }

    @Getter
    @Setter
    public static class chargeData{
        @JsonProperty("process_fee")private String process_fee;
        @JsonProperty("upfront_fee")private String upfront_fee;
        @JsonProperty("prop_val_chrg")private String prop_val_chrg;
        @JsonProperty("expert_val_chrg")private String expert_val_chrg  ;
        @JsonProperty("devn_chrg")private String devn_chrg;
        @JsonProperty("docu_chrg")private String docu_chrg;
        @JsonProperty("cersai_chrg")private String cersai_chrg;
        @JsonProperty("cic_chrg")private String cic_chrg;
        @JsonProperty("nesl_chrg")private String nesl_chrg;
        @JsonProperty("proc_fee_arrear")private String proc_fee_arrear;
        @JsonProperty("qly_insp_chrg")private String qly_insp_chrg;
        @JsonProperty("yly_insp_chrg")private String yly_insp_chrg;
    }
}