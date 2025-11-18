package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.DedupValidationResultDTO;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.*;
import com.sib.ibanklosucl.service.vlsr.FinacleLosDedupeService;
import com.sib.ibanklosucl.service.vlsr.LosDedupeService;
import com.sib.ibanklosucl.utilies.DedupValidationMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RMMakerValidationService {

    @Autowired
    private VehicleLoanMasterRepository vehicleLoanMasterRepository;

    @Autowired
    private FinacleLosDedupeService finacleLosDedupeService;

    @Autowired
    private LosDedupeService losDedupeService;

    @Autowired
    private VehicleLoanFcvCpvCfrRepository fcvCpvCfrRepository;

    @Autowired
    private EligibilityDetailsRepository eligibilityDetailsRepository;

    @Autowired
    private VehicleLoanAmberService vehicleLoanAmberService;

    @Autowired
    private VehicleLoanDetailsRepository vehicleLoanDetailsRepository;
        @Autowired
    private FetchRepository fetchRepository;

    public Map<String, List<String>> validateRMMakerSubmission(String wiNum, Long slno,String decision) {
        Map<String, List<String>> structuredErrors = new HashMap<>();
         boolean onlySendbackAllowed = false;
        structuredErrors.put("FCV/CPV/CFR", new ArrayList<>());
        structuredErrors.put("Eligibility Details", new ArrayList<>());
        structuredErrors.put("Deviation details", new ArrayList<>());
        structuredErrors.put("Other", new ArrayList<>());
         if(!fetchRepository.isvalidVehicleAccount(String.valueOf(slno))){
              structuredErrors.get("Other").add("Dealer Account Missing in Vehicle Details !!");
            }

        // Validate Dedup
        DedupValidationResultDTO dedupResult = validateDedupDetailsForVehicleLoan(slno);
        if (!dedupResult.isValid()) {
            structuredErrors.get("Other").add(DedupValidationMessageUtil.generateErrorMessage(dedupResult));
        }

        // Validate VehicleLoanFcvCpvCfr
        Optional<VehicleLoanFcvCpvCfr> fcvCpvCfr = fcvCpvCfrRepository.findLatestBySlno(slno);
        if (!fcvCpvCfr.isPresent()) {
            structuredErrors.get("FCV/CPV/CFR").add("FCV/CPV/CFR details are missing.");
        } else {
            VehicleLoanFcvCpvCfr fcvCpvCfrDetails = fcvCpvCfr.get();
            if ("Y".equals(fcvCpvCfrDetails.getDelFlg())) {
                structuredErrors.get("FCV/CPV/CFR").add("FCV/CPV/CFR details are not saved.");
            }
            if ("Negative".equals(fcvCpvCfrDetails.getFcvStatus()) || "Negative".equals(fcvCpvCfrDetails.getCpvStatus())) {
                onlySendbackAllowed = true;
                if (!"SB".equals(decision)) {
                    structuredErrors.get("FCV/CPV/CFR").add("Only sendback is allowed in decision when FCV or CPV status is Negative.");
                }
            }
        }

        // Validate EligibilityDetails
        Optional<EligibilityDetails> eligibilityDetails = eligibilityDetailsRepository.findByWiNumAndSlno(wiNum, slno);
        if (!eligibilityDetails.isPresent()) {
            structuredErrors.get("Eligibility Details").add("Eligibility details are missing.");
        } else {
            EligibilityDetails details = eligibilityDetails.get();
            if (details.getLoanAmountRecommendedCPC() == null || details.getLoanAmountRecommendedCPC().compareTo(BigDecimal.ZERO) <= 0) {
                structuredErrors.get("Eligibility Details").add("Eligibility recommendation amount not updated.");
            }
            if (details.getRoiRecommendedCPC() == null || details.getRoiRecommendedCPC().compareTo(BigDecimal.ZERO) <= 0) {
                structuredErrors.get("Eligibility Details").add("Eligibility recommendation ROI not updated.");
            }
            if (!"Y".equals(details.getProceedFlag())) {
                structuredErrors.get("Eligibility Details").add("Eligibility recommendation not updated.");
            }
            if (!"N".equals(details.getDelFlg())) {
                structuredErrors.get("Eligibility Details").add("Eligibility details are not active.");
            }
        }

        // Validate VehicleLoanAmber
        List<VehicleLoanAmber> amberDeviations = vehicleLoanAmberService.getPendingAmberDeviations(wiNum, slno);
        if (amberDeviations.isEmpty()) {
           // structuredErrors.get("Deviation details").add("No active amber deviations found.");
        } else {
            boolean hasValidAmberDeviation = false;
            boolean hasEmptyApprovingAuth = false;

            for (VehicleLoanAmber deviation : amberDeviations) {
                if ("amber".equalsIgnoreCase(deviation.getColour())) {
                    hasValidAmberDeviation = true;
                    if (deviation.getApprovingAuth() == null || deviation.getApprovingAuth().trim().isEmpty()) {
                        hasEmptyApprovingAuth = true;
                        break;
                    }
                }
            }
            if (hasEmptyApprovingAuth) {
                structuredErrors.get("Deviation details").add("Approving authority is not entered for one or more amber deviations.");
            }
        }

        // Remove empty categories
        structuredErrors.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        return structuredErrors;
    }

    public List<String> validateRMMakerSubmission2(String wiNum, Long slno) {
        List<String> errors = new ArrayList<>();

        // Validate Dedup
        DedupValidationResultDTO dedupResult = validateDedupDetailsForVehicleLoan(slno);
        if (!dedupResult.isValid()) {
            errors.add(DedupValidationMessageUtil.generateErrorMessage(dedupResult));
        }

        // Validate VehicleLoanFcvCpvCfr
        Optional<VehicleLoanFcvCpvCfr> fcvCpvCfr = fcvCpvCfrRepository.findLatestBySlno(slno);
        if (!fcvCpvCfr.isPresent()) {
            errors.add("FCV/CPV/CFR details are missing.");
        } else {
            VehicleLoanFcvCpvCfr fcvCpvCfrDetails = fcvCpvCfr.get();
            if ("Y".equals(fcvCpvCfrDetails.getDelFlg())) {
                errors.add("FCV/CPV/CFR details are not saved.");
            }
        }

        // Validate EligibilityDetails
        Optional<EligibilityDetails> eligibilityDetails = eligibilityDetailsRepository.findByWiNumAndSlno(wiNum, slno);
        if (!eligibilityDetails.isPresent()) {
            errors.add("Eligibility details are missing.");
        } else {
            EligibilityDetails details = eligibilityDetails.get();
            if (details.getLoanAmountRecommendedCPC() == null || details.getLoanAmountRecommendedCPC().compareTo(BigDecimal.ZERO) <= 0) {
                errors.add("Eligibility recommendation amount not updated.");
            }
            if (details.getRoiRecommendedCPC() == null || details.getRoiRecommendedCPC().compareTo(BigDecimal.ZERO) <= 0) {
                errors.add("Eligibility recommendation ROI not updated.");
            }
            if (!"Y".equals(details.getProceedFlag())) {
                errors.add("Eligibility recommendation not updated.");
            }
            if (!"N".equals(details.getDelFlg())) {
                errors.add("Eligibility details are not active.");
            }
        }

        // Validate VehicleLoanAmber

        List<VehicleLoanAmber> amberDeviations = vehicleLoanAmberService.getPendingAmberDeviations(wiNum, slno);
        if (amberDeviations.isEmpty()) {
            errors.add("No active amber deviations found.");
        } else {
            boolean hasValidAmberDeviation = false;
            boolean hasEmptyApprovingAuth = false;

            for (VehicleLoanAmber deviation : amberDeviations) {
                if ("amber".equalsIgnoreCase(deviation.getColour())) {
                    hasValidAmberDeviation = true;
                    if (deviation.getApprovingAuth() == null || deviation.getApprovingAuth().trim().isEmpty()) {
                        hasEmptyApprovingAuth = true;
                        break;  // We can stop checking once we find an empty approvingAuth
                    }
                }
            }
            if (hasEmptyApprovingAuth) {
                errors.add("Deviation details pending. Approving authority is not entered for one or more amber deviations.");
            }
        }


        // Validate VehicleLoanDetails

        // Additional checks can be added here as needed

        return errors;
    }

    public DedupValidationResultDTO validateDedupDetailsForVehicleLoan(Long slno) {
        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterRepository.findBySlnoWithApplicants(slno);
        List<VehicleLoanApplicant> applicants = vehicleLoanMaster.getApplicants();
        List<FinDedupEntity> allFinacleDedupDetails = finacleLosDedupeService.getFinDupByID(vehicleLoanMaster.getSlno());
        List<LosDedupeEntity> allLosDedupDetails = losDedupeService.getLosByID(vehicleLoanMaster.getSlno());

        DedupValidationResultDTO result = new DedupValidationResultDTO();
        result.setValid(true);
        result.setApplicantStatuses(applicants.stream()
                .map(applicant -> validateApplicant(applicant, allFinacleDedupDetails, allLosDedupDetails, vehicleLoanMaster.getQueueDate()))
                .collect(Collectors.toList()));

        if (result.getApplicantStatuses().stream().anyMatch(status -> !status.isFinacleDedupDone() || !status.isLosDedupDone())) {
            result.setValid(false);
        }

        return result;
    }

    private DedupValidationResultDTO.ApplicantDedupStatus validateApplicant(VehicleLoanApplicant applicant,
                                                                            List<FinDedupEntity> allFinacleDedupDetails,
                                                                            List<LosDedupeEntity> allLosDedupDetails,
                                                                            Date queueDate) {
        DedupValidationResultDTO.ApplicantDedupStatus status = new DedupValidationResultDTO.ApplicantDedupStatus();
        status.setApplicantId(applicant.getApplicantId());
        status.setApplicantName(applicant.getApplName());
        status.setFinacleDedupDone(isValidFinacleDedupForApplicant(applicant, allFinacleDedupDetails, queueDate));
        status.setLosDedupDone(isValidLosDedupForApplicant(applicant, allLosDedupDetails, queueDate));
        return status;
    }

    private boolean isValidFinacleDedupForApplicant(VehicleLoanApplicant applicant, List<FinDedupEntity> allDedupDetails, Date queueDate) {
        List<FinDedupEntity> applicantDedupDetails = allDedupDetails.stream()
                .filter(dedup -> dedup.getApplicantId().equals(applicant.getApplicantId()))
                .collect(Collectors.toList());

        return !applicantDedupDetails.isEmpty() &&
                applicantDedupDetails.stream().anyMatch(dedup -> isValidFinacleDedupDetail(dedup, queueDate));
    }

    private boolean isValidLosDedupForApplicant(VehicleLoanApplicant applicant, List<LosDedupeEntity> allDedupDetails, Date queueDate) {
        List<LosDedupeEntity> applicantDedupDetails = allDedupDetails.stream()
                .filter(dedup -> dedup.getApplicantId().equals(applicant.getApplicantId()))
                .collect(Collectors.toList());

        return !applicantDedupDetails.isEmpty() &&
                applicantDedupDetails.stream().anyMatch(dedup -> isValidLosDedupDetail(dedup, queueDate));
    }

    private boolean isValidFinacleDedupDetail(FinDedupEntity dedupDetail, Date queueDate) {
        return dedupDetail.getCmdate() != null &&
                queueDate != null &&
                dedupDetail.getCmdate().after(queueDate) &&
                "Y".equals(dedupDetail.getActiveFlag()) &&
                "N".equals(dedupDetail.getDelFlag());
    }

    private boolean isValidLosDedupDetail(LosDedupeEntity dedupDetail, Date queueDate) {
        return dedupDetail.getCmdate() != null &&
                queueDate != null &&
                dedupDetail.getCmdate().after(queueDate) &&
                "Y".equals(dedupDetail.getActiveFlag()) &&
                "N".equals(dedupDetail.getDelFlag());
    }
}
