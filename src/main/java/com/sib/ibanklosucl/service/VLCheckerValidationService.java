package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.DedupValidationResultDTO;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.model.integrations.VLBlackList;
import com.sib.ibanklosucl.model.integrations.VLHunterDetails;
import com.sib.ibanklosucl.model.integrations.VehicleLoanBREDetails;
import com.sib.ibanklosucl.repository.VehicleLoanMasterRepository;
import com.sib.ibanklosucl.service.integration.BlacklistService;
import com.sib.ibanklosucl.service.integration.DKScoreService;
import com.sib.ibanklosucl.service.integration.ExperianHunterService;
import com.sib.ibanklosucl.service.integration.VehicleLoanBREService;
import com.sib.ibanklosucl.service.vlsr.FinacleLosDedupeService;
import com.sib.ibanklosucl.service.vlsr.LosDedupeService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.DedupValidationMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VLCheckerValidationService {
   @Autowired
    private VehicleLoanMasterRepository vehicleLoanMasterRepository;

    @Autowired
    private FinacleLosDedupeService finacleLosDedupeService;

    @Autowired
    private LosDedupeService losDedupeService;
    @Autowired
    private VehicleLoanBREService vehicleLoanBREService;

    @Autowired
    private ExperianHunterService experianHunterService;

    @Autowired
    private BlacklistService blacklistService;
    @Autowired
    private VehicleLoanApplicantService vehicleLoanApplicantService;
    @Autowired
    private DKScoreService dkScoreService;

    public DedupValidationResultDTO validateDedupDetailsForVehicleLoan(Long vehicleLoanMasterId) {
        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterRepository.findById(vehicleLoanMasterId)
                .orElseThrow(() -> new EntityNotFoundException("VehicleLoanMaster not found"));

        List<VehicleLoanApplicant> applicants =  vehicleLoanMaster.getApplicants().stream()
							.filter(fd -> "N".equals(fd.getDelFlg()))
							.toList();
        List<FinDedupEntity> allFinacleDedupDetails = finacleLosDedupeService.getFinDupByID(vehicleLoanMaster.getSlno());
        List<LosDedupeEntity> allLosDedupDetails = losDedupeService.getLosByID(vehicleLoanMaster.getSlno());

        DedupValidationResultDTO result = new DedupValidationResultDTO();
        result.setValid(true);
        result.setApplicantStatuses(applicants.stream()
            .map(applicant -> validateApplicant(applicant, allFinacleDedupDetails, allLosDedupDetails, vehicleLoanMaster.getQueueDate()))
            .collect(Collectors.toList()));

        // If any applicant's checks are not done, the overall result is not valid
        if (result.getApplicantStatuses().stream().anyMatch(status -> !status.isFinacleDedupDone() || !status.isLosDedupDone())) {
            result.setValid(false);
        }

        return result;
    }

    public String getValidationErrorMessage(Long vehicleLoanMasterId) {
        DedupValidationResultDTO result = validateDedupDetailsForVehicleLoan(vehicleLoanMasterId);
        if (!result.isValid()) {
            return DedupValidationMessageUtil.generateErrorMessage(result);
        }
        return null;
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
     public List<String> performFinalChecks(String winum, String slno, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        try {
            // Run BRE again to see if any change occured
            //Map<String, Object>  fetchBRE = vehicleLoanBREService.getAmberData(winum, slno, CommonUtils.getClientIp(request));
            // Check BRE status
            Optional<VehicleLoanBREDetails> breDetailsOpt = vehicleLoanBREService.getLatestBREDetails(winum, Long.valueOf(slno));
            if (!breDetailsOpt.isPresent()) {
                errors.add("BRE check has not been performed.");
            } else {
                Map<String, Object>  fetchBRE = vehicleLoanBREService.getAmberData(winum, slno, CommonUtils.getClientIp(request));
                 Optional<VehicleLoanBREDetails> breDetailsOptFin = vehicleLoanBREService.getLatestBREDetails(winum, Long.valueOf(slno));
                 VehicleLoanBREDetails breDetails = breDetailsOptFin.get();
                if (!"green".equalsIgnoreCase(breDetails.getEligibilityFlag())) {
                    errors.add("BRE eligibility flag is not green.");
                }
                if (!("green".equalsIgnoreCase(breDetails.getBreFlag()) || "amber".equalsIgnoreCase(breDetails.getBreFlag()))) {
                    errors.add("BRE flag is neither green nor amber.");
                }
            }

            // Check Hunter status and score, and Blacklist status for all applicants
            List<VehicleLoanApplicant> applicants = vehicleLoanApplicantService.findBySlnoAndDelFlg(Long.valueOf(slno));
            List<Map<String, Object>> dkScoreData = dkScoreService.fetchDKScoreDataConsideringQueueDate(Long.valueOf(slno));
            for (VehicleLoanApplicant applicant : applicants) {
                // Hunter check

                if(!"G".equalsIgnoreCase(applicant.getApplicantType())) {
                    List<VLHunterDetails> hunterDetails = applicant.getVlHunterDetailsList();
                    if (hunterDetails == null || hunterDetails.isEmpty()) {
                        errors.add("Hunter check not performed for applicant " + applicant.getApplName());
                    } else {
                        boolean validHunterCheck = hunterDetails.stream()
                                .filter(detail -> "N".equals(detail.getDelFlg()))
                                .anyMatch(detail -> detail.getErrorCount() != null && detail.getErrorCount()>0);

                        if (validHunterCheck) {
                            errors.add("Hunter check failed for applicant " + applicant.getApplName());
                        }
                    }
                }
                // Blacklist check
                Optional<VLBlackList> blacklistResultOpt = blacklistService.findByApplicantIdAndDelFlg(applicant.getApplicantId(), "N");
                if (!blacklistResultOpt.isPresent()) {
                    errors.add("Blacklist check not performed for applicant " + applicant.getApplicantId());
                } else {
                    VLBlackList blacklistResult = blacklistResultOpt.get();
                    if (!"Not Blacklisted".equals(blacklistResult.getBlCheckResult())) {
                        errors.add("Blacklist check failed for applicant " + applicant.getApplicantId());
                    }
                }
                // DK Score check
                 Map<String, Object> dkScoreInfo = null;
                if (dkScoreData != null && !dkScoreData.isEmpty()) {
                    dkScoreInfo = dkScoreData.stream()
                            .filter(Objects::nonNull)  // Filter out null elements
                            .filter(data -> applicant.getApplicantId() != null &&
                                    applicant.getApplicantId().equals(data.get("applicantId")))
                            .findFirst()
                            .orElse(null);
                }

                if (dkScoreInfo == null) {
                    errors.add("Race Score not found for the applicant " + applicant.getApplName() + ". Fetch Race score to proceed.");
                } else {
                    String status = (String) dkScoreInfo.get("status");
                     String dkColor = (String) dkScoreInfo.get("color");
                    Long raceScore = (Long) dkScoreInfo.get("raceScore");

                    if ("red".equals(dkColor)) {
                        errors.add("RACE Score check not successful for applicant " + applicant.getApplicantId());
                    }

                    if (raceScore == null) {
                        errors.add("RACE Score (Race Score) is empty for applicant " + applicant.getApplicantId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error performing the final checks for wiNum {} and slno {}",winum,slno,e);
                errors.add("Errors occured while final validations"+e.getMessage());
        }
        return errors;
    }
}
