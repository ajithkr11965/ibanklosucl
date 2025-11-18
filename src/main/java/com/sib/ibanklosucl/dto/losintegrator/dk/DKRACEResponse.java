package com.sib.ibanklosucl.dto.losintegrator.dk;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DKRACEResponse {
    private String status;
    private String msg;
    private String errorreason;
    private String score;
    private String raceScore;
    private String delinquencyAnalysis;
    private String bureau_liability;
    private String max_mob_cc_age_crosstab;
    private String avg_mob_al_max_mob_all_crosstab;
    private String Geographic_Risk_Ranking;
    private String util_curr_uns_avg_util_curr_all;
    private String Latest_Bank_Type;
    private String product_holding;
    private String Max_Sanc_Amt_comuns;
    private String Min_MOB_Secured;
    private String max_dpd_life_nbr_0_24m_crosstab;
    private String util_curr_tw_al_crosstab;
    private String customer_age;
    private String ltv_req;
    private String asset_class;
    private String customer_type;
    private String Manufacturer_Collateral_bin;
    private String grade;
    private String vehicle_category;
    private String max_mob_al_age_crosstab;
    private String mon_since30_avg_mob_sec_crosstab;
    private String laetst_bank_acc_type_crosstab;
    private String Max_MOB_Regular_bin;
    private String max_sanc_amt_uns_cc_crosstab;
    private String util_curr_agri_min_mob_pl_cd_tw_crosstab;
    private String max_dpd_24m_mon_since0_crosstab;
    private String nbr_0_24m_nbr_accts_open_l12m_crosstab;
    private String TotConsINC_Delinq_36M_Regular_bin;
    private String max_sanc_amt_secmov_util_curr_al_crosstab;
    private String nbr_dist_acc_pincode_crosstab;
    private String avg_util_curr_uns_util_curr_hllap_crosstab;
    private String scoreflag;
    private List<Liability> liabilityList;
    private String applicantName;
    private String applicantType;

    @Data
    public static class Liability {
        private String bankName;
        private BigDecimal limit;
        private BigDecimal emiAmount;
        private String natureOfLimit;
        private BigDecimal outStandingBal;
    }
}
