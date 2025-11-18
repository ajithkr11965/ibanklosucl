package com.sib.ibanklosucl.service.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.dto.losintegrator.HunterResponseDTO;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.integrations.MatchScheme;
import com.sib.ibanklosucl.model.integrations.Rule;
import com.sib.ibanklosucl.model.integrations.Warning;
import com.sib.ibanklosucl.model.integrations.VLHunterDetails;
import com.sib.ibanklosucl.repository.integations.ExperianHunterResponseRepository;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExperianHunterService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.integrator}")
    private String experianHunterApiUrl;

    @Autowired
    private VehicleLoanApplicantService vehicleLoanApplicantService;

    @Autowired
    private ObjectMapper objectMapper;
    @Value("${esb.MerchantName}")
    private String merchantName;
    @Value("${esb.MerchantCode}")
    private String merchantCode;
    @Value("${esb.ChannelID}")
    private String channelID;
    @Autowired
    private ExperianHunterResponseRepository experianHunterResponseRepository;
    @Autowired
    private UserSessionData usd;

    @Value("${app.dev-mode}")
    private boolean devMode;





@Transactional
    public HunterResponseDTO callExperianHunterApi(Long applicantId, String wiNum, Long slno) {
        log.info("Calling Experian Hunter API for applicant ID: {}, wiNum: {}, slno: {}", applicantId, wiNum, slno);
        try {
            Map<String, Object> requestBody = createHunterRequestBody(applicantId, wiNum);
            log.info("Experian Hunter API request: {} {}", experianHunterApiUrl,objectMapper.writeValueAsString(requestBody));

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    experianHunterApiUrl,
                    requestBody,
                    String.class
            );
            String responseBodyStr = responseEntity.getBody();
            log.info("Experian Hunter API response: {}", responseBodyStr);

            return saveExperianHunterResponse(responseBodyStr, wiNum, slno, applicantId, usd.getEmployee().getPpcno());
        } catch (Exception e) {
            log.error("Error calling Experian Hunter API: {}", e.getMessage(), e);
            throw new RuntimeException("Error calling Experian Hunter API", e);
        }
    }



    public Map<String, Object> createHunterRequestBody(Long applicantId, String wiNum) {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> innerRequest = new HashMap<>();

        // Assuming you have a service to fetch applicant details dynamically
        VehicleLoanApplicant applicant = vehicleLoanApplicantService.findByApplicantIdAndDelFlg(applicantId);

        // Fetching and formatting date values dynamically
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String applDate = applicant.getLastModDate() != null ? dateFormat.format(applicant.getLastModDate()) : "";
        String dob = applicant.getKycapplicants() != null && applicant.getKycapplicants().getPanDob() != null
                ? dateFormat.format(applicant.getKycapplicants().getPanDob()) : "";
        String currDate = dateFormat.format(new Date());
        String applicantGender ="";
        if("M".equals(applicant.getBasicapplicants().getGender())) {
            applicantGender="MALE";
        } else if("F".equals(applicant.getBasicapplicants().getGender())) {
             applicantGender="FEMALE";
        } else {
            applicantGender=applicant.getBasicapplicants().getGender();
        }
          String martialFlag = applicant.getBasicapplicants().getMaritalStatus();
        String maritalStatus = "SINGL".equals(martialFlag) ? "SINGLE" : "MARRIED";

        // Constructing the inner request map dynamically from the applicant data
        innerRequest.put("PRODUCT", "RBCPC_VL_I");  // Assuming this is static, otherwise pass it as a parameter
        innerRequest.put("DATE", currDate);  // Could be the system date or passed in dynamically
        innerRequest.put("CLASSIFICATION", "UNKNOWN");
        innerRequest.put("IDENTIFIER", wiNum);  // Work item number passed in as a parameter
        innerRequest.put("APP_DTE", applDate);
        innerRequest.put("ORG_NME", applicant.getApplName());
        innerRequest.put("CONSTIT", "");
        innerRequest.put("DAT_INC", "");
        innerRequest.put("MA_GNDR",  applicantGender);
        innerRequest.put("MA_MAR_STT", maritalStatus);

        innerRequest.put("MA_PMA_ADD", applicant.getBasicapplicants().getAddr1() + applicant.getBasicapplicants().getAddr2() + applicant.getBasicapplicants().getAddr3());
        innerRequest.put("MA_PMA_CTY", applicant.getBasicapplicants().getCitydesc());
        innerRequest.put("MA_PMA_STE", applicant.getBasicapplicants().getStatedesc());
        innerRequest.put("MA_PMA_CTRY", "INDIA");
        innerRequest.put("MA_PMA_PIN", applicant.getBasicapplicants().getPin());
        innerRequest.put("MA_TEL_NO", String.valueOf(applicant.getBasicapplicants().getMobileNo()));
        innerRequest.put("MA_EMA_ADD", applicant.getBasicapplicants().getEmailId());
        innerRequest.put("MA_PAN", applicant.getKycapplicants() != null ? applicant.getKycapplicants().getPanNo() : "");
        innerRequest.put("MA_FST_NME", applicant.getApplName());
        innerRequest.put("MA_LST_NME", applicant.getApplName());  // Last name is not available, so leaving it as empty
        innerRequest.put("MA_DOB", dob);
        innerRequest.put("MA_ADD", applicant.getBasicapplicants().getComAddr1() + applicant.getBasicapplicants().getComAddr2() + applicant.getBasicapplicants().getComAddr3());
        innerRequest.put("MA_CTY", applicant.getBasicapplicants().getComCityedesc());
        innerRequest.put("MA_STE", applicant.getBasicapplicants().getComStatedesc());
        innerRequest.put("MA_CTRY", "INDIA");
        innerRequest.put("MA_PIN", applicant.getBasicapplicants().getComPin());
        innerRequest.put("MA_DOC_TYP", "PAN CARD");
        innerRequest.put("MA_DOC_NO", applicant.getKycapplicants() != null ? applicant.getKycapplicants().getPanNo() : "");

        // Adding the inner request to the main request body
        requestBody.put("request", innerRequest);
        if(devMode){
            requestBody.put("mock", true);// Can toggle this value based on your need
        }else{
            requestBody.put("mock", false);// Can toggle this value based on your need
        }

        requestBody.put("apiName", "hunterCheckBPM");
        requestBody.put("workItemNumber", wiNum);
        requestBody.put("origin", "APPLICANT");  // Assuming this is static, can pass as a parameter if needed

        return requestBody;
    }
    private Map<String, Object> fetchPersonDetails(Long applicantId) {
        Map<String, Object> personDetails = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        VehicleLoanApplicant applicant = vehicleLoanApplicantService.findByApplicantIdAndDelFlg(applicantId);
        Date dob = applicant.getKycapplicants().getPanDob();
        String applicantDob = dob != null ? new SimpleDateFormat("yyyy-MM-dd").format(dob) : "1900-01-01";
        LocalDate birthDate = LocalDate.parse(applicantDob, formatter);
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(birthDate, currentDate).getYears();
        int birthyear = birthDate.getYear();

        String qualificationType = "OTHER";
        String occupancyStatus = applicant.getBasicapplicants().getResidenceType() != null ? applicant.getBasicapplicants().getResidenceType() : "UNKNOWN";
        String martialFlag = applicant.getBasicapplicants().getMaritalStatus();
        String maritalStatus = "SINGL".equals(martialFlag) ? "SINGLE" : "MARRIED";
        personDetails.put("dateOfBirth", applicantDob);
        personDetails.put("yearOfBirth", birthyear);
        personDetails.put("age", age);
        personDetails.put("maritalStatus", maritalStatus);
        personDetails.put("occupancyStatus", occupancyStatus);
        personDetails.put("qualificationType", qualificationType);
        return personDetails;
    }

    private String fetchApplicantName(Long applicantId) {
        VehicleLoanApplicant applicant = vehicleLoanApplicantService.findByApplicantIdAndDelFlg(applicantId);
        return applicant.getBasicapplicants().getApplicantName() != null ? applicant.getBasicapplicants().getApplicantName() : "UNKNOWN";
    }

    private Map<String, Object> fetchAddress(Long applicantId) {
        Map<String, Object> addressDetails = new HashMap<>();
        VehicleLoanApplicant applicant = vehicleLoanApplicantService.findByApplicantIdAndDelFlg(applicantId);
        String address1 = applicant.getBasicapplicants().getAddr1();
        String address2 = applicant.getBasicapplicants().getAddr2();
        String address3 = applicant.getBasicapplicants().getAddr3();
        String address = (address1 + " " + address2 + " " + address3).trim();
        String city = applicant.getBasicapplicants().getCitydesc();
        String state = applicant.getBasicapplicants().getStatedesc();
        String pinCode = applicant.getBasicapplicants().getPin();

        addressDetails.put("id", "ADDRESS_1");
        addressDetails.put("addressType", "CURRENT");
        addressDetails.put("buildingName", "XXXX");
        addressDetails.put("street", "XXXX");
        addressDetails.put("street2", address.isEmpty() ? "UNKNOWN" : address);
        addressDetails.put("postTown", city != null ? city : "XXXX");
        addressDetails.put("stateProvinceCode", "XXXX");
        addressDetails.put("postal", 0);
        //addressDetails.put("postal", pinCode != null ? pinCode : "000000");
        addressDetails.put("countryCode", "IND");
        addressDetails.put("country", "India");
        addressDetails.put("propertyType", "XXXX");

        return addressDetails;
    }

    private Long fetchPhoneNumber(Long applicantId) {
        VehicleLoanApplicant applicant = vehicleLoanApplicantService.findByApplicantIdAndDelFlg(applicantId);
        String mobileNo = applicant.getBasicapplicants().getMobileNo();
        return mobileNo != null ? Long.valueOf(mobileNo) : 0L;
    }

    private String fetchEmailAddress(Long applicantId) {
        VehicleLoanApplicant applicant = vehicleLoanApplicantService.findByApplicantIdAndDelFlg(applicantId);
        return applicant.getBasicapplicants().getEmailId() != null ? applicant.getBasicapplicants().getEmailId() : "unknown@example.com";
    }

    @Transactional
    public HunterResponseDTO saveExperianHunterResponse(String responseJson, String wiNum, Long slno, Long applicantId, String cmUser) {
        log.info("Saving Experian Hunter response for applicant ID: {}, wiNum: {}, slno: {}", applicantId, wiNum, slno);
        try {
            JsonNode rootNode = objectMapper.readTree(responseJson);

            String status = rootNode.path("status").asText();
            int errorCount = rootNode.path("errorCount").asInt();
            if (errorCount > 0) {
            log.error("Hunter check failed for the applicant. Error count: {}. Kindly try again after some time.", errorCount);
            HunterResponseDTO errorResponse = new HunterResponseDTO();
            errorResponse.setStatus("FAILURE");
            errorResponse.setDecision("Error");
            errorResponse.setErrorMessage("Hunter check failed. Please retry after some time.");
            return errorResponse;
        }

            if ("SUCCESS".equalsIgnoreCase(status)) {
                setDelFlagForExistingEntries(applicantId);
            } else if ("FAILURE".equalsIgnoreCase(status)) {
                log.error("Hunter check failed for the applicant. Kindly try after sometime");

            }

            VLHunterDetails hunterDetails = experianHunterResponseRepository
                    .findByWiNumAndApplicantIdAndDelFlg(wiNum,applicantId,"N")
                    .orElse(new VLHunterDetails());

            updateBasicDetails(hunterDetails, rootNode, wiNum, slno, applicantId, cmUser);

            // Clear existing data
            hunterDetails.getMatchSchemes().clear();
            hunterDetails.getRules().clear();
            hunterDetails.getWarnings().clear();

            // Update collections
            updateWarnings(hunterDetails, rootNode.path("warnings"));
            updateMatchSchemes(hunterDetails, rootNode.path("matchSchemes"));
            updateRules(hunterDetails, rootNode.path("rules"));

            // Set other fields
            hunterDetails.setWarningCount(rootNode.path("warningCount").asInt());
            hunterDetails.setMatches(rootNode.path("matches").asInt());
            hunterDetails.setTotalMatchScore(rootNode.path("totalMatchScore").asText());
            if(!rootNode.path("totalMatchScore").asText().equals("")) {
                hunterDetails.setScore(Integer.valueOf(rootNode.path("totalMatchScore").asText()));
            }
            hunterDetails.setErrorCount(rootNode.path("errorCount").asInt());
            hunterDetails.setStatusCode(status);
            hunterDetails.setFullResponse(responseJson);
            if(hunterDetails.getMatches()==0) {
                hunterDetails.setDecision("Passed");
            } else {
                hunterDetails.setDecision("Pending");
            }

            // Save the updated entity
            if (!"FAILURE".equalsIgnoreCase(status)) {
                hunterDetails = experianHunterResponseRepository.save(hunterDetails);
            }

            return createHunterResponseDTO(hunterDetails);
        } catch (Exception e) {
            log.error("Error saving Experian Hunter response: ", e);
            throw new RuntimeException("Error saving Experian Hunter response", e);
        }
    }
    private void updateBasicDetails(VLHunterDetails hunterDetails, JsonNode rootNode, String wiNum, Long slno, Long applicantId, String cmUser) {
        hunterDetails.setWiNum(wiNum);
        hunterDetails.setSlno(slno);
        hunterDetails.setApplicantId(applicantId);
        hunterDetails.setCmUser(cmUser);
        hunterDetails.setCmDate(new Date());
        hunterDetails.setDelFlg("N");
        hunterDetails.setDecision(rootNode.path("decision").asText());
        hunterDetails.setScore(rootNode.path("score").asInt());
    }

    private void updateWarnings(VLHunterDetails hunterDetails, JsonNode warningsNode) {
        for (JsonNode warningNode : warningsNode) {
            Warning warning = new Warning();
            warning.setMessage(warningNode.path("message").asText());
            hunterDetails.addWarning(warning);
        }
    }

    private void updateMatchSchemes(VLHunterDetails hunterDetails, JsonNode matchSchemesNode) {
        for (JsonNode schemeNode : matchSchemesNode) {
            MatchScheme matchScheme = new MatchScheme();
            matchScheme.setScore(schemeNode.path("score").asInt());
            matchScheme.setSchemeId(schemeNode.path("schemeID").asInt());
            hunterDetails.addMatchScheme(matchScheme);
        }
    }

    private void updateRules(VLHunterDetails hunterDetails, JsonNode rulesNode) {
        for (JsonNode ruleNode : rulesNode) {
            Rule rule = new Rule();
            rule.setScore(ruleNode.path("score").asInt());
            rule.setRuleCount(ruleNode.path("ruleCount").asInt());
            rule.setRuleId(ruleNode.path("ruleID").asText());
            hunterDetails.addRule(rule);
        }
    }

    private HunterResponseDTO createHunterResponseDTO(VLHunterDetails hunterDetails) {
        HunterResponseDTO dto = new HunterResponseDTO();
        dto.setStatus(hunterDetails.getStatusCode());
        dto.setDecision(hunterDetails.getDecision());
        if (hunterDetails.getReviewDate() != null || hunterDetails.getReviewUser() != null) {
            dto.setReviewed(true);
        } else {
            dto.setReviewed(false);
        }
        if (hunterDetails.getMatches() > 0) {
            dto.setMatchfound(true);
        } else {
            dto.setMatchfound(false);
        }
        dto.setScore(hunterDetails.getScore());
        dto.setTotalMatchScore(hunterDetails.getTotalMatchScore());
        dto.setWarnings(hunterDetails.getWarnings().stream().map(Warning::getMessage).collect(Collectors.toList()));
        dto.setRuleIds(hunterDetails.getRules().stream().map(Rule::getRuleId).collect(Collectors.toList()));
        if (hunterDetails.getErrorCount() > 0) {
             dto.setDecision("Failed");
            dto.setErrorMessage("Hunter check encountered errors. Please retry after some time.");
        }

        return dto;
    }

    private void setDelFlagForExistingEntries(Long applicantId) {
        List<VLHunterDetails> existingEntries = experianHunterResponseRepository.findAllByApplicantIdAndDelFlg(applicantId, "N");
        for (VLHunterDetails entry : existingEntries) {
            entry.setDelFlg("Y");
            entry.setDelUser(usd.getEmployee().getPpcno());
            entry.setDelDate(new Date());
        }
        experianHunterResponseRepository.saveAll(existingEntries);
    }
 public boolean getLatestHunterCheck(String wiNum) {
        return experianHunterResponseRepository.findByWiNumAndDelFlg(wiNum, "N").isPresent();
    }
}
