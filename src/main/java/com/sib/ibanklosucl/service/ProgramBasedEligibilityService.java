package com.sib.ibanklosucl.service;
import com.sib.ibanklosucl.dto.CheckEligibilityRequest;
import com.sib.ibanklosucl.service.eligibility.EligibilityHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProgramBasedEligibilityService {

    private final EligibilityHelperService eligibilityHelperService;
    @Autowired
    private VLBREservice vlbrEngineservice;

    public CheckEligibilityRequest.LoanEligibleDto getProgramBasedEligibility(String winum, Long slno, int tenor, BigDecimal cardRate, BigDecimal TotalLtv) {
        return eligibilityHelperService.getProgramBasedEligibility(winum, slno, tenor, cardRate,TotalLtv);
    }

    public boolean checkEligibilityValidations(String winum, Long slno) throws Exception {
        String response = vlbrEngineservice.getAmberDatas(winum, slno);
       //  response = "{\"status\":\"SUCCESS\",\"eligibilityFlag\":\"green\",\"breFlag\":\"amber\",\"breData\":{\"AMB001\":{\"breCode\":\"AMB001\",\"breDesc\":\"LTV% is manually entered\",\"generic\":\"Y\",\"color\":\"green\"},\"AMB002\":{\"breCode\":\"AMB002\",\"breDesc\":\"Loan amount is outside STP range\",\"color\":\"green\",\"generic\":\"N\",\"breSub\":[{\"applicantId\":149,\"applicantType\":\"A\",\"color\":\"green\",\"currentValue\":\"1200000\",\"masterValue\":\"100000 - 10000000\"}]},\"AMB003\":{\"breCode\":\"AMB003\",\"breDesc\":\"Maximum age limit breached\",\"color\":\"green\",\"generic\":\"N\",\"breSub\":[{\"applicantId\":149,\"applicantType\":\"A\",\"color\":\"green\",\"currentValue\":30,\"masterValue\":\"65\"}]},\"AMB004\":{\"breCode\":\"AMB004\",\"breDesc\":\"Bureau score outside of STP range\",\"color\":\"amber\",\"generic\":\"N\",\"breSub\":[{\"applicantId\":149,\"applicantType\":\"A\",\"color\":\"amber\",\"currentValue\":965,\"masterValue\":\"650 - 950\"}]},\"AMB007\":{\"breCode\":\"AMB007\",\"breDesc\":\"Tenor is outside STP range\",\"color\":\"green\",\"generic\":\"N\",\"breSub\":[{\"applicantId\":149,\"applicantType\":\"A\",\"color\":\"green\",\"currentValue\":\"60\",\"masterValue\":\"12 - 60\"}]},\"AMB012\":{\"breCode\":\"AMB012\",\"breDesc\":\"Average monthly income is manually entered\",\"generic\":\"N\",\"color\":\"green\",\"breSub\":[{\"applicantId\":149,\"applicantType\":\"A\",\"color\":\"green\",\"currentValue\":\"ITR\",\"masterValue\":\"ITR or ABB or LOANFD\"}]}}}";

       // response = "{\"status\":\"SUCCESS\",\"eligibilityFlag\":\"red\",\"eligibilityData\":{\"status\":\"failure\",\"ELI001\":{\"eliCode\":\"ELI001\",\"eliDesc\":\"Income/ABB criterion each applicant/co-appl whose income considered\\u003dYes is not met\",\"color\":\"red\",\"generic\":\"N\",\"eliSub\":[{\"applicantId\":101,\"applicantType\":\"A\",\"color\":\"red\",\"applicantName\":\"Lorel Ipsum\",\"currentValue\":\"45000\",\"masterValue\":\"50000 - 1000000\"},{\"applicantId\":101,\"applicantType\":\"A\",\"color\":\"red\",\"applicantName\":\"Lorel Ipsum2\",\"currentValue\":\"4500\",\"masterValue\":\"50000 - 1000000\"}]},\"ELI002\":{\"eliCode\":\"ELI002\",\"eliDesc\":\"Bureau score of all applicants/co-app and guarantors not within the stipulated range\",\"color\":\"red\",\"generic\":\"N\",\"title\":\"Credit Score Range\",\"explanation\":\"Your credit score must fall within our acceptable range. This score is a key indicator of creditworthiness and helps us assess lending risk.\",\"eliSub\":[{\"applicantId\":101,\"applicantType\":\"A\",\"color\":\"red\",\"applicantName\":\"Lorel Ipsum\",\"currentValue\":\"620\",\"masterValue\":\"650 - 850\"}]},\"ELI003\":{\"eliCode\":\"ELI003\",\"eliDesc\":\"Loan amount not within the stipulated range\",\"color\":\"red\",\"generic\":\"N\",\"title\":\"Loan Amount Range\",\"explanation\":\"The requested loan amount must be within our stipulated range. This ensures the loan aligns with our lending policies and your financial situation.\",\"eliSub\":[{\"applicantId\":101,\"applicantType\":\"A\",\"color\":\"red\",\"applicantName\":\"Lorel Ipsum\",\"currentValue\":\"1500000\",\"masterValue\":\"100000 - 1000000\"}]},\"ELI004\":{\"eliCode\":\"ELI004\",\"eliDesc\":\"Loan tenor not within the stipulated range\",\"color\":\"red\",\"generic\":\"N\",\"eliSub\":[{\"applicantId\":101,\"applicantType\":\"A\",\"color\":\"red\",\"currentValue\":\"72\",\"masterValue\":\"12 - 60\"}]},\"ELI005\":{\"eliCode\":\"ELI005\",\"eliDesc\":\"Age of all applicants, co-applicants and guarantors not within the stipulated range\",\"color\":\"red\",\"generic\":\"N\",\"eliSub\":[{\"applicantId\":101,\"applicantType\":\"A\",\"color\":\"red\",\"currentValue\":\"65\",\"masterValue\":\"21 - 60\"}]},\"ELI006\":{\"eliCode\":\"ELI006\",\"eliDesc\":\"Minimum age of atleast 1 applicant or co-applicant not met\",\"color\":\"red\",\"generic\":\"N\",\"eliSub\":[{\"applicantId\":101,\"applicantType\":\"A\",\"color\":\"red\",\"currentValue\":\"17\",\"masterValue\":\"18\"},{\"applicantId\":102,\"applicantType\":\"C\",\"color\":\"red\",\"currentValue\":\"17\",\"masterValue\":\"18\"}]},\"ELI007\":{\"eliCode\":\"ELI007\",\"eliDesc\":\"Minimum total employment/business experience tenure is not met\",\"color\":\"red\",\"generic\":\"N\",\"title\":\"Minimum Employment/Business Experience\",\"explanation\":\"The total employment or business experience must meet our minimum requirement. This helps us assess the stability of your income source.\",\"eliSub\":[{\"applicantId\":101,\"applicantType\":\"A\",\"color\":\"red\",\"currentValue\":\"18\",\"masterValue\":\"24\"}]},\"ELI008\":{\"eliCode\":\"ELI008\",\"eliDesc\":\"Min current employment tenure is not met\",\"color\":\"red\",\"generic\":\"N\",\"eliSub\":[{\"applicantId\":101,\"applicantType\":\"A\",\"color\":\"red\",\"currentValue\":\"9\",\"masterValue\":\"12\"}]},\"ELI009\":{\"eliCode\":\"ELI009\",\"eliDesc\":\"Min duration of stay in current residence is not met by the applicant\",\"color\":\"red\",\"generic\":\"N\",\"eliSub\":[{\"applicantId\":101,\"applicantType\":\"A\",\"color\":\"red\",\"currentValue\":\"6\",\"masterValue\":\"12\"}]}},\"breData\":\"\"}";
        return eligibilityHelperService.parseEligibilityData(response);
      //  return eligibilityHelperService.checkEligibilityValidations(winum, slno);
    }

    public BigDecimal getEligibleEmi(BigDecimal roi, BigDecimal amount, int tenor) {
        return eligibilityHelperService.calculateEmi(roi, amount, tenor);
    }
}
