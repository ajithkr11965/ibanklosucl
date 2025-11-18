package com.sib.ibanklosucl.service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.dto.dedup.*;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanSingleDedupe;
import com.sib.ibanklosucl.repository.integations.VLSingleDedupeRepository;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VLDedupeService {
    private final VLSingleDedupeRepository vlDedupeRepository;
    private final DedupeApiClient dedupeApiClient;
    private final UserSessionData usd;
    private final ObjectMapper objectMapper;
    private final VehicleLoanApplicantService vehicleLoanApplicantService;

    @Value("${esb.MerchantName}")
    private String merchantName;

    @Value("${esb.MerchantCode}")
    private String merchantCode;

    @Autowired
    public VLDedupeService(VLSingleDedupeRepository vlDedupeRepository,
                          DedupeApiClient dedupeApiClient,
                          UserSessionData usd,
                          ObjectMapper objectMapper,
                          VehicleLoanApplicantService vehicleLoanApplicantService) {
        this.vlDedupeRepository = vlDedupeRepository;
        this.dedupeApiClient = dedupeApiClient;
        this.usd = usd;
        this.objectMapper = objectMapper;
        this.vehicleLoanApplicantService = vehicleLoanApplicantService;
    }

    @Transactional
    public DedupeResponse performDedupeCheck(DedupeRequest dedupeRequest, String reqIP) {
        log.info("Performing dedupe check for applicant ID: {}", dedupeRequest.getOrigin());
        dedupeRequest.getRequest().setMerchantCode(merchantCode);
        dedupeRequest.getRequest().setMerchantName(merchantName);
        validateDedupeRequest(dedupeRequest);

        try {
            DedupeResponse dedupeResponse = dedupeApiClient.performDedupeCheck(dedupeRequest);
            DedupeResult dedupeResult = processDedupeResponse(dedupeResponse);
            updateExistingDedupe(dedupeRequest.getOrigin());
            saveNewDedupe(dedupeRequest, dedupeResponse, dedupeResult, reqIP);
            return dedupeResponse;
        } catch (Exception e) {
            log.error("Error during dedupe check for applicant ID: {}", dedupeRequest.getOrigin(), e);
            throw new RuntimeException("Error performing dedupe check", e);
        }
    }

    private void validateDedupeRequest(DedupeRequest dedupeRequest) {
        if (dedupeRequest == null || dedupeRequest.getOrigin() == null ||
            dedupeRequest.getWorkItemNumber() == null || dedupeRequest.getRequest() == null ||
            dedupeRequest.getRequest().getMobileNumber() == null || dedupeRequest.getRequest().getEmail() == null) {
            log.error("Invalid DedupeRequest: {}", dedupeRequest);
            throw new IllegalArgumentException("All parameters are required for Dedupe API check");
        }
    }

    private DedupeResult processDedupeResponse(DedupeResponse dedupeResponse) {
    DedupeResult dedupeResult = new DedupeResult();
    String statusCode = dedupeResponse.getResponse().getStatus().getCode();
    log.debug("Dedupe API response status code: {}", statusCode);
    if ("201".equals(statusCode)) {
        // Check for the "NO DATA" message
        String message = dedupeResponse.getResponse().getBody().getMessage();
        if ("NO DATA".equalsIgnoreCase(message)) {
            dedupeResult.setMatched(false);
            dedupeResult.setMatchReason("No duplicates found");
            return dedupeResult;
        }
    }


    if ("201".equals(statusCode) || "200".equals(statusCode)) {
        dedupeResult.setMatched(true);
        List<DedupeMatch> matches = dedupeResponse.getResponse().getBody().getCustomer().stream()
            .map(customer -> {
                DedupeMatch match = new DedupeMatch();
                match.setCustomerId(customer.getCustomerid());
                match.setPhone(customer.getMobilephone());
                match.setEmail(customer.getEmailid());
                match.setDob(customer.getDob());
                match.setPan(customer.getPan());
                match.setName(customer.getName());
                match.setVoterId(customer.getVoterid());
                match.setAadharRefNo(customer.getAadhar_ref_no());
                match.setPassport(customer.getPassportno());
                return match;
            })
            .collect(Collectors.toList());
        dedupeResult.setMatches(matches);
    } else {
        dedupeResult.setMatched(false);
        dedupeResult.setMatchReason("No matches found");
    }
    return dedupeResult;
}


    private void updateExistingDedupe(String origin) {
        VehicleLoanSingleDedupe existingDedupe = vlDedupeRepository.findByApplicantIdAndDelFlg(Long.valueOf(origin), "N")
                .orElse(null);
        if (existingDedupe != null) {
            existingDedupe.setDelFlg("Y");
            vlDedupeRepository.save(existingDedupe);
            log.debug("Updated existing dedupe entry to deleted for applicant ID: {}", origin);
        }
    }

    private void saveNewDedupe(DedupeRequest dedupeRequest, DedupeResponse dedupeResponse,
                               DedupeResult dedupeResult, String reqIP) throws JsonProcessingException {
        VehicleLoanSingleDedupe newDedupe = createNewDedupeEntry(dedupeRequest, dedupeResponse, dedupeResult, reqIP);
        vlDedupeRepository.save(newDedupe);
        log.info("Saved new dedupe entry for applicant ID: {}", dedupeRequest.getOrigin());
    }

    private VehicleLoanSingleDedupe createNewDedupeEntry(DedupeRequest dedupeRequest, DedupeResponse dedupeResponse,
                                          DedupeResult dedupeResult, String reqIP) throws JsonProcessingException {
        VehicleLoanSingleDedupe newDedupe = new VehicleLoanSingleDedupe();
        newDedupe.setApplicantId(Long.valueOf(dedupeRequest.getOrigin()));
        VehicleLoanApplicant applicant = vehicleLoanApplicantService.findByApplicantIdAndDelFlg(Long.valueOf(dedupeRequest.getOrigin()));
        newDedupe.setWiNum(dedupeRequest.getWorkItemNumber());
        newDedupe.setSlNo(Long.valueOf(dedupeRequest.getSlno()));
        newDedupe.setPhone(dedupeRequest.getRequest().getMobileNumber());
        newDedupe.setEmail(dedupeRequest.getRequest().getEmail());
        newDedupe.setCheckDate(new Date());
        newDedupe.setFetchResponse(objectMapper.writeValueAsString(dedupeResponse));
        newDedupe.setCheckResult(dedupeResult.isMatched() ? "Match Found" : "No Match");
        newDedupe.setCmUser(usd.getEmployee().getPpcno());
        newDedupe.setCmDate(new Date());
        newDedupe.setReqIpAddr(reqIP);
        newDedupe.setVldedupe(applicant);
        newDedupe.setDelFlg("N");

        // If there are matches, set the first match details
        if (dedupeResult.isMatched() && !dedupeResult.getMatches().isEmpty()) {
            DedupeMatch match = dedupeResult.getMatches().get(0);
            newDedupe.setCustomerId(match.getCustomerId());
            newDedupe.setName(match.getName());
            newDedupe.setDob(match.getDob());
            newDedupe.setPan(match.getPan());
            newDedupe.setVoterId(match.getVoterId());
            newDedupe.setAadharRefNo(match.getAadharRefNo());
            newDedupe.setPassport(match.getPassport());
        }

        return newDedupe;
    }
    @Transactional
public void updateDedupeRelation(Long applicantId, String relation, String reqIP) {
    log.info("Updating dedupe relation for applicant ID: {} with relation: {}", applicantId, relation);

    VehicleLoanSingleDedupe dedupe = vlDedupeRepository.findByApplicantIdAndDelFlg(applicantId, "N")
            .orElseThrow(() -> new RuntimeException("No active dedupe check found"));

    dedupe.setRelation(relation);
    dedupe.setReqIpAddr(reqIP);
    dedupe.setCmUser(usd.getEmployee().getPpcno());
    dedupe.setCmDate(new Date());

    vlDedupeRepository.save(dedupe);
    log.info("Successfully updated dedupe relation for applicant ID: {}", applicantId);
}

@Transactional
public void rejectDedupeMatch(Long applicantId, String remarks, String reqIP) {
    log.info("Rejecting dedupe match for applicant ID: {}", applicantId);

    VehicleLoanSingleDedupe dedupe = vlDedupeRepository.findByApplicantIdAndDelFlg(applicantId, "N")
            .orElseThrow(() -> new RuntimeException("No active dedupe check found"));

    // Mark current record as deleted
    dedupe.setDelFlg("Y");
    vlDedupeRepository.save(dedupe);

    // Create new rejected record
    VehicleLoanSingleDedupe rejectedDedupe = new VehicleLoanSingleDedupe();
    BeanUtils.copyProperties(dedupe, rejectedDedupe, "ino", "delFlg", "cmUser", "cmDate", "remarks", "checkResult");

    rejectedDedupe.setRemarks(remarks);
    rejectedDedupe.setCheckResult("Rejected");
    rejectedDedupe.setDelFlg("N");
    rejectedDedupe.setReqIpAddr(reqIP);
    rejectedDedupe.setCmUser(usd.getEmployee().getPpcno());
    rejectedDedupe.setCmDate(new Date());

    vlDedupeRepository.save(rejectedDedupe);
    log.info("Successfully rejected dedupe match for applicant ID: {}", applicantId);
}

@Transactional
public void submitDedupeResults(List<DedupeResultSubmission> results, String reqIP) {
    log.info("Submitting dedupe results for {} applicants", results.size());

    for (DedupeResultSubmission result : results) {
        VehicleLoanSingleDedupe dedupe = vlDedupeRepository.findByApplicantIdAndDelFlg(result.getApplicantId(), "N")
                .orElseThrow(() -> new RuntimeException("No active dedupe check found for applicant: " + result.getApplicantId()));

        dedupe.setRelation(result.getRelation());
        dedupe.setCheckResult("COMPLETED");
        dedupe.setReqIpAddr(reqIP);
        dedupe.setCmUser(usd.getEmployee().getPpcno());
        dedupe.setCmDate(new Date());

        vlDedupeRepository.save(dedupe);
    }

    log.info("Successfully submitted dedupe results");
}
@Transactional
public List<Map<String, Object>> getPendingDedupeRelations(String wiNum, String slNo) {
    List<Map<String, Object>> pendingRelations = new ArrayList<>();

    List<VehicleLoanSingleDedupe> dedupes = vlDedupeRepository.findByWiNumAndSlNoAndDelFlg(
        wiNum, Long.valueOf(slNo), "N");

    for (VehicleLoanSingleDedupe dedupe : dedupes) {
        if ("Match Found".equals(dedupe.getCheckResult()) &&
            (dedupe.getRelation() == null || dedupe.getRelation().trim().isEmpty())) {

            VehicleLoanApplicant applicant = dedupe.getVldedupe();
            Map<String, Object> pendingRecord = new HashMap<>();
            pendingRecord.put("applicantId", dedupe.getApplicantId());
            pendingRecord.put("applicantName", applicant.getBasicapplicants().getApplicantName());
            pendingRecord.put("applicantType", applicant.getApplicantType());

            pendingRelations.add(pendingRecord);
        }
    }

    return pendingRelations;
}

}
