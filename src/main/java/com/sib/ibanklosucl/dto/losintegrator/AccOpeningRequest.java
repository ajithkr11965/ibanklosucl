package com.sib.ibanklosucl.dto.losintegrator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccOpeningRequest {

    private Request request;
    private boolean mock;
    private String apiName;
    private String slno;
    private String workItemNumber;
    @Getter
    @Setter
    public static class Request {
        @JsonProperty("Merchant_code")private String Merchant_code;
        @JsonProperty("Schm_code")private String Schm_code;
        @JsonProperty("amount")private String amount;
        @JsonProperty("EqInstallFlg")private String EqInstallFlg;
        @JsonProperty("IntTblCode")private String IntTblCode;
        @JsonProperty("LoanPeriodDays")private String LoanPeriodDays;
        @JsonProperty("LoanPeriodMonths")private String LoanPeriodMonths;
        @JsonProperty("Merchant_name")private String Merchant_name;
        @JsonProperty("Gl_sub_head_code")private String Gl_sub_head_code;
        @JsonProperty("AcctDrPrefPcnt")private String AcctDrPrefPcnt;
        @JsonProperty("Cif_id")private String Cif_id;
        @JsonProperty("IntCollFlg")private String IntCollFlg;
        @JsonProperty("Sol_id")private String Sol_id;
        @JsonProperty("UUID")private String UUID;
        @JsonProperty("currencyCode")private String currencyCode;

        @JsonProperty("RepmtRec") private RepmtRec[] repmtRec;
        @JsonProperty("RelPartyRec") private RelPartyRec[] relPartyRec;
        @JsonProperty("LoanAcctAdd_CustomData") private LoanAcctAdd_CustomData loanAcctAddCustomData;


    }
    @Getter
    @Setter
    public static class  RepmtRec{
        @JsonProperty("intereststartDate")private String intereststartDate;
        @JsonProperty("NoOfInstall")private String NoOfInstall;
        @JsonProperty("InstallmentId")private String InstallmentId;
        @JsonProperty("installstartDate")private String installstartDate;
        @JsonProperty("IntStartDt")private String IntStartDt;
        @JsonProperty("InstallStartDt")private String InstallStartDt;
        @JsonProperty("frequency")private String frequency;

    }

    @Getter
    @Setter
    public static class RelPartyRec{
        @JsonProperty("RelPartyType")private String RelPartyType;
        @JsonProperty("RelPartyCode")private String RelPartyCode;
        @JsonProperty("CustId")private String CustId;
        @JsonProperty("Name")private String Name;
        @JsonProperty("TitlePrefix")private String TitlePrefix;
        @JsonProperty("RelPartyContactInfo")private RelPartyContactInfo relPartyContactInfo ;

    }

    @Getter
    @Setter
    public static class RelPartyContactInfo{
        @JsonProperty("EmailAddr")private String EmailAddr;
        @JsonProperty("Addr1")private String Addr1;
        @JsonProperty("Addr2")private String Addr2;
        @JsonProperty("City")private String City;
        @JsonProperty("StateProv")private String StateProv;
        @JsonProperty("PostalCode")private String PostalCode;
        @JsonProperty("AddrType")private String AddrType;

    }

    @Getter
    @Setter
    public static class LoanAcctAdd_CustomData{
        @JsonProperty("free_text5")private String free_text5;
        @JsonProperty("free_text4")private String free_text4;
        @JsonProperty("free_text3")private String free_text3;
        @JsonProperty("free_text2")private String free_text2;
        @JsonProperty("free_text1")private String free_text1;
        @JsonProperty("free_text13")private String free_text13;
        @JsonProperty("free_text14")private String free_text14;
        @JsonProperty("drawing_power_ind")private String drawing_power_ind;
        @JsonProperty("guard_cover_code")private String guard_cover_code;
        @JsonProperty("purpose_of_advn")private String purpose_of_advn;
        @JsonProperty("repay_method")private String repay_method;
        @JsonProperty("repay_oper_acct")private String repay_oper_acct;
        @JsonProperty("repay_holdin_oper_acct_flg")private String repay_holdin_oper_acct_flg;
        @JsonProperty("limit_value")private String limit_value;
        @JsonProperty("limit_currency")private String limit_currency;
        @JsonProperty("free_text12")private String free_text12;
        @JsonProperty("occupation_code")private String occupation_code;
        @JsonProperty("borrower_category_code")private String borrower_category_code;
        @JsonProperty("free_text15")private String free_text15;
        @JsonProperty("drawing_power_currency")private String drawing_power_currency;
        @JsonProperty("limit_sanct_expiry_date")private String limit_sanct_expiry_date;

        @JsonProperty("limit_pen_days")private String limit_pen_days;
        @JsonProperty("limit_desc")private String limit_desc;
        @JsonProperty("acct_limit_entered")private String acct_limit_entered;
        @JsonProperty("free_code10")private String free_code10;
        @JsonProperty("review_date")private String review_date;
        @JsonProperty("mode_of_oper_code")private String mode_of_oper_code;
        @JsonProperty("limit_prefix")private String limit_prefix;
        @JsonProperty("limit_suffix")private String limit_suffix;
        @JsonProperty("limit_sanct_code")private String limit_sanct_code;
        @JsonProperty("channel_id")private String channel_id;
        @JsonProperty("mode_advn")private String mode_advn;
        @JsonProperty("limit_sanct_ref_num")private String limit_sanct_ref_num;
        @JsonProperty("industry_type")private String industry_type;
        @JsonProperty("sanct_date")private String sanct_date;
        @JsonProperty("employee_ppc")private String employee_ppc;
        @JsonProperty("limit_sanct_auth_code")private String limit_sanct_auth_code;
        @JsonProperty("sub_sector_code")private String sub_sector_code;
        @JsonProperty("limit_document_date")private String limit_document_date;
        @JsonProperty("sector_code")private String sector_code;
        @JsonProperty("mis_entered")private String mis_entered;
        @JsonProperty("limit_pen_months")private String limit_pen_months;
        @JsonProperty("type_advn")private String type_advn;
        @JsonProperty("ledger_num")private String ledger_num;
        @JsonProperty("free_code9")private String free_code9;
        @JsonProperty("free_code4")private String free_code4;
        @JsonProperty("acct_mgr_userid")private String acct_mgr_userid;
        @JsonProperty("free_code5")private String free_code5;
        @JsonProperty("free_code6")private String free_code6;
        @JsonProperty("free_code7")private String free_code7;
        @JsonProperty("free_code1")private String free_code1;
        @JsonProperty("free_code2")private String free_code2;
        @JsonProperty("remarks")private String remarks;
        @JsonProperty("nature_advn")private String nature_advn;
        @JsonProperty("free_code3")private String free_code3;
        @JsonProperty("acct_label")private acct_label[] acct_label;

    }

    @Getter
    @Setter
    public static class acct_label{
        @JsonProperty("label_name")private String label_name;
        @JsonProperty("label_value")private String label_value;
    }
}
