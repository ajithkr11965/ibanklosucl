package com.sib.ibanklosucl.service.impl;

import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.EligibilityDetailsRepository;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.service.*;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class CreditDtSaveImp implements VlSaveService {
    @Autowired
    private VehicleLoanApplicantService repository;
    @Autowired
    private VLCreditService vlCreditservice;
    @Autowired
    private VLFinliabService VLFinliabservice;

    @Autowired
    private VLFinastService VLFinastservice;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private EligibilityDetailsRepository eligibilityDetailsRepository;

    @Autowired
    private VehicleLoanMasterService masterService;
    @Autowired
    private VLBREservice vlbrEservice;

    @Override
    @SneakyThrows
    @Transactional(rollbackOn = Exception.class)
    public TabResponse executeSave(FormSave fs) {
        log.info("Starting credit save operation for slno: {}, appid: {}", fs.getBody().getSlno(), fs.getBody().getAppid());
        String slno = fs.getBody().getSlno();
        String appid = fs.getBody().getAppid();
        String vlfinliab = "", vlfinast = "";
        VehicleLoanApplicant applicant = repository.getById(Long.valueOf(appid));
        VLCredit vl = vlCreditservice.findByApplicantIdAndDelFlg(applicant.getApplicantId());
        Long liabilityAsPerPayslip = 0L;


        if (vl == null) {
            log.warn("Credit information not found for applicant: {}", applicant.getApplicantId());
//            vl=new VLCredit();
//            vl.setReqIpAddr(CommonUtils.getIP(fs.getReqip()));
//            vl.setSlno(Long.valueOf(slno));
//            vl.setWiNum(fs.getBody().getWinum());
//            vl.setApplicantId(applicant.getApplicantId());
//            vl.setDelFlg("N");
            throw new ValidationException(ValidationError.COM001, "Kindly  Run Experian!");
        }

        Map<String, String> vep = fetchRepository.getEmpProgramforApp(Long.valueOf(appid));
        if (vep == null) {
            log.warn("Employment program information not found for application: {}", appid);
            throw new ValidationException(ValidationError.COM001, "Kindly Save Income & Employment Details!!");
        }
        log.debug("Employment program details - loan program: {}, employment type: {}",
                vep.get("loan_program"), vep.get("employment_type"));

        Boolean stat = vlbrEservice.getDpdDaysStat(Long.valueOf(appid), vep.get("loan_program"), vep.get("employment_type"), applicant.getSlno(), applicant.getWiNum(), "BM");
        if (stat) {
            throw new ValidationException(ValidationError.COM001, "Scorecard didnt pass Days Past Due Validation");
        }
        if (!vl.isDkFlag()) {
            throw new ValidationException(ValidationError.COM001, "Kindly Run Experian (ScoreCard Not Arrived)!");
        }

        if (!vl.isExperianFlag()) {
            throw new ValidationException(ValidationError.COM001, "Kindly Run Experian (Score Not Arrived)!");
        }

        vl.setHomeSol(usd.getSolid());
        for (DataItem data : fs.getBody().getData()) {
            switch (data.getKey().trim()) {
                case "bureauScore":
                    //vl.setBureauScore(Long.valueOf(data.getValue()));
                    break;
                case "vlfinliab":
                    vlfinliab = data.getValue();
                    break;
                case "vlfinast":
                    vlfinast = data.getValue();
                    break;
                case "liabilityAsPerPayslip":
                    if (data.getValue() != null && !data.getValue().isEmpty()) {
                        liabilityAsPerPayslip = Long.valueOf(data.getValue());
                        vl.setPayslipLiablity(liabilityAsPerPayslip);
                    }
                    break;
            }
        }

        applicant.setCreditComplete("Y");
        Long obligations = 0L;

        repository.resetLoanFlg(Long.valueOf(slno));
        Optional<EligibilityDetails> eligibilityDetails_ = eligibilityDetailsRepository.findBySlnoAndDelFlg(applicant.getSlno(), "N");
        EligibilityDetails eligibilityDetails = null;
        if (eligibilityDetails_.isPresent()) {
            eligibilityDetails = eligibilityDetails_.get();
            eligibilityDetails.setEligibilityFlg("N");
            eligibilityDetails.setProceedFlag("N");
            eligibilityDetailsRepository.save(eligibilityDetails);
        }

        VLCredit savedVLCredit = vlCreditservice.save(vl);
        Long savedCreditIno = savedVLCredit.getIno();

        if (vlfinliab != null) {
            List<VLFinliab> vlfinliablist = VLFinliabservice.convertStringToVLEmploymentempList(vlfinliab);
            log.debug("Parsed {} financial liabilities", vlfinliablist.size());

            if (vlfinliablist.stream().anyMatch(t -> t.getBankName() != null && !t.getBankName().isEmpty())) {
                log.info("---idndtgs8-" + vlfinliablist);
                vl.setVlLiabList(vlfinliablist);
                BigDecimal emiObligations = vlfinliablist.stream().map(t -> new BigDecimal(t.getModifiedEmi())).reduce(BigDecimal.ZERO, BigDecimal::add).round(new MathContext(0, RoundingMode.HALF_EVEN));
                log.debug("Total EMI obligations: {}", emiObligations);


                //obligations=vlfinliablist.stream().map(t->new BigDecimal(t.getModifiedEmi())).reduce(BigDecimal.ZERO, BigDecimal::add).round(new MathContext(0, RoundingMode.HALF_EVEN)).longValue();
                obligations = emiObligations.add(new BigDecimal(liabilityAsPerPayslip)).round(new MathContext(0, RoundingMode.HALF_EVEN)).longValue();
                log.info("Total obligations (EMI + payslip): {}", obligations);
                VLFinliabservice.saveAll(vlfinliablist, vl);
            } else {
                obligations = liabilityAsPerPayslip;
                log.info("No valid liabilities found, using only payslip liability: {}", obligations);
                VLFinliabservice.updateDelFlg(vl.getApplicantId(), vl.getWiNum(), "N");
            }
        } else {
            obligations = liabilityAsPerPayslip;
            log.info("No valid liabilities found, using only payslip liability: {}", obligations);
            VLFinliabservice.updateDelFlg(vl.getApplicantId(), vl.getWiNum(), "N");
        }

        if (vlfinast != null) {
            List<VLFinasset> vlfinastlist = VLFinastservice.convertStringToVLEmploymentempList(vlfinast);
            if (vlfinastlist.stream().anyMatch(t -> t.getAssetType() != null && !t.getAssetType().isEmpty())) {
                vl.setVlAstList(vlfinastlist);
                VLFinastservice.saveAll(vlfinastlist, vl);
            } else {
                VLFinastservice.updateDelFlg(vl.getApplicantId(), vl.getWiNum(), "N");
            }

        } else {
            VLFinastservice.updateDelFlg(vl.getApplicantId(), vl.getWiNum(), "N");
        }

        vl.setTotObligations(obligations);
        vlCreditservice.save(vl);
        VehicleLoanMaster master = masterService.findById(Long.valueOf(slno));
        master.setCurrentTab(fs.getBody().getCurrenttab());
        masterService.saveLoan(master);
        repository.saveApplicant(applicant);
        log.info("Credit save operation completed successfully for applicant: {}", applicant.getApplicantId());
        return new TabResponse("S", "", applicant.getApplicantId().toString());
    }

    @Override
    public TabResponse fetchData(FormData fs) {
        try {
            log.info("Fetching credit data for request: {}", fs);
            return new TabResponse("S", "");
        } catch (Exception e) {
            log.error("Error fetching credit data: {}", e.getMessage(), e);
            return new TabResponse("F", e.getMessage());
        }
    }
}
