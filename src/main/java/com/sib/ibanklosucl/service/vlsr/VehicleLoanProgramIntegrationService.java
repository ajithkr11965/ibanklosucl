package com.sib.ibanklosucl.service.vlsr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.dto.ResponseDTO;
import com.sib.ibanklosucl.dto.doc.SMSEmailDTO;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.*;
import com.sib.ibanklosucl.repository.program.*;
import com.sib.ibanklosucl.service.iBankService;
import com.sib.ibanklosucl.service.integration.SMSEmailService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class VehicleLoanProgramIntegrationService {
    @Value("${api.integrator}")
    private String integratorEndpoint;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ITRAlertRepository itrAlertRepository;
    @Value("${esb.MerchantName}")
    private String merchantName;
    @Value("${esb.MerchantCode}")
    private String merchantCode;

     @Value("${app.dev-mode:true}")
    private boolean devMode;
    @Value("${esb.ChannelID}")
    private String channelID;
    @Autowired
    private BSADetailsRepository bsaDetailsRepository;
    @Autowired
    private VehicleLoanApplicantRepository vehicleLoanApplicantRepository;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private VehicleLoanProgramRepository vehicleLoanProgramRepository;
    @Autowired
    private VehicleLoanProgramNRIRepository vehicleLoanProgramNriRepository;
    @Autowired
    private VehicleLoanProgramSalaryRepository vehicleLoanProgramSalaryRepository;
    @Autowired
    private VehicleLoanFDRepository vehicleLoanFDRepository;
    @Autowired
    private iBankService bankService;
    @Autowired
    private SMSEmailService mailService;

    @Transactional
    public String handleITRUploadRequest(String mobileNo, String pan, String dob, String mode, String applicantId, String wiNum, String slno, List<String> itrYearsList, List<String> form26asYearsList, List<String> form16YearsList) {
        log.info("Handling ITR upload request for applicant ID: {}", applicantId);
        if (mobileNo == null || pan == null || dob == null || mode == null || applicantId == null || wiNum == null || slno == null) {
            throw new IllegalArgumentException("All parameters are required for ITR upload request");
        }
        try {
            String uuidVal = generateUniqueIdentifier(wiNum, applicantId);
            String apiResponse = fetchITRGenLinkUpload(mobileNo, pan, dob, applicantId, wiNum, itrYearsList, form26asYearsList, form16YearsList,uuidVal);
            Map<String, Object> responseMap = new ObjectMapper().readValue(apiResponse, Map.class);
            Map<String, Object> bodyMap = (Map<String, Object>) ((Map<String, Object>) responseMap.get("Response")).get("Body");
            Map<String, String> message = (Map<String, String>) bodyMap.get("message");
            if (message == null) {
                throw new RuntimeException("Invalid response format from ITR upload API");
            }
            String url = message.get("url");
            String generateLinkId = message.get("transactionId");
            // Generate an alert ID
            String alertId = "ITRUPLOADALERT";
            List<VehicleLoanITR> existingRecords = itrAlertRepository.findByApplicantIdAndWiNumAndDelFlg(Long.valueOf(applicantId), wiNum, "N");

//            for (VehicleLoanITR existingRecord : existingRecords) {
//                existingRecord.setDelFlg("Y");
//                existingRecord.setDelUser(usd.getPPCNo());
//                existingRecord.setDelDate(new Date());
//            }
//            itrAlertRepository.saveAll(existingRecords);

            VehicleLoanITR itrAlert = new VehicleLoanITR();
            itrAlert.setMobileNo(mobileNo);
            itrAlert.setItrMode(mode);
            itrAlert.setUrl(url);
            itrAlert.setAlertId(alertId);
            itrAlert.setGenerateLinkId(generateLinkId);
            itrAlert.setPerfiosTransactionId(generateLinkId);
            itrAlert.setSlno(Long.valueOf(slno));
            itrAlert.setWiNum(wiNum);
            itrAlert.setApplicantId(Long.valueOf(applicantId));
            itrAlert.setTimestamp(new Date());
            itrAlert.setCmUser(usd.getPPCNo());
            itrAlert.setCmDate(new Date());
            itrAlert.setClientTxnId(uuidVal);
            itrAlert.setDelFlg("N");
            itrAlertRepository.save(itrAlert);

            log.info("ITR upload request handled successfully for applicant ID: {}", applicantId);
            return apiResponse;
        } catch (Exception e) {
            log.error("Error occurred while processing ITR upload request for applicant ID: {}", applicantId, e);
            throw new RuntimeException("An error occurred while processing the ITR upload request", e);
        }
    }

    private String extractUrlFromResponse(String apiResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(apiResponse);
            return rootNode.path("Response").path("Body").path("message").path("Success").path("url").asText();
        } catch (Exception e) {
            log.error("Error extracting URL from API response", e);
            return null;
        }
    }

    private String extractTransactionIdFromResponse(String apiResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(apiResponse);
            return rootNode.path("Response").path("Body").path("message").path("Success").path("transactionId").asText();
        } catch (Exception e) {
            log.error("Error extracting URL from API response", e);
            return null;
        }
    }

    private boolean isTransactionCompleted(String statusResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(statusResponse);

            JsonNode statusNode = rootNode.path("Response").path("Body").path("message").path("Status");
            String processing = statusNode.path("processing").asText();
            String partStatus = statusNode.path("Part").path("status").asText();

            return "completed".equalsIgnoreCase(processing) && "success".equalsIgnoreCase(partStatus);
        } catch (Exception e) {
            log.error("Error parsing transaction status response", e);
            return false;
        }
    }

    private String extractPerfiosTransactionId(String statusResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(statusResponse);
            return rootNode.path("Response").path("Body").path("message").path("Status").path("Part").path("perfiosTransactionId").asText();
        } catch (Exception e) {
            log.error("Error extracting perfiosTransactionId from status response", e);
            return null;
        }
    }

    @Transactional
    public String handleITRRequest(String mobileNo, String pan, String dob, String mode, String applicantId, String wiNum, String slno) {
        log.info("Handling ITR request for applicant ID: {}", applicantId);
        if (mobileNo == null || pan == null || dob == null || mode == null || applicantId == null || wiNum == null || slno == null) {
            log.info("Handling ITR request for applicant ID: {} {} {} {} {} {} {}",mobileNo,pan,dob,mode ,applicantId,wiNum,slno);
            throw new IllegalArgumentException("All parameters are required for ITR request");
        }
        try {
            cleanupExpiredLinks(Long.valueOf(applicantId), wiNum);
            if ("sms".equalsIgnoreCase(mode)) {
                // Check for existing valid link
                VehicleLoanITR existingValidLink = findExistingValidLink(Long.valueOf(applicantId), wiNum);
                if (existingValidLink != null) {
                    // Use existing link
                    log.info("Using existing valid link for applicant ID: {}", applicantId);
                    ResponseDTO maildata = sendSMSLink(mobileNo, existingValidLink.getUrl(), applicantId, wiNum, slno, Long.valueOf(applicantId),existingValidLink.getClientTxnId());
                    return "{\"status\":\"success\",\"message\":\"SMS link sent to the mobile using existing link.\"}";
                }
            }
            String uuidVal = generateUniqueIdentifier(wiNum, applicantId);
            String apiResponse = fetchITRGenLink(mobileNo, pan, dob, "itrGenLink", applicantId, wiNum,uuidVal);
            Map<String, Object> responseMap = new ObjectMapper().readValue(apiResponse, Map.class);
            Map<String, Object> bodyMap = (Map<String, Object>) ((Map<String, Object>) responseMap.get("Response")).get("Body");
            Map<String, String> message = (Map<String, String>) bodyMap.get("message");
            if (message == null) {
                throw new RuntimeException("Invalid response format from ITR API");
            }
            String url = message.get("url");
            String generateLinkId = message.get("generateLinkId");
            // Generate an alert ID
            String alertId = "ITRALERT";
//            List<VehicleLoanITR> existingRecords = itrAlertRepository.findByApplicantIdAndWiNumAndDelFlg(Long.valueOf(applicantId), wiNum, "N");
//
//            for (VehicleLoanITR existingRecord : existingRecords) {
//                existingRecord.setDelFlg("Y");
//                existingRecord.setDelUser(usd.getPPCNo());
//                existingRecord.setDelDate(new Date());
//            }
//            itrAlertRepository.saveAll(existingRecords);

            VehicleLoanITR itrAlert = new VehicleLoanITR();
            itrAlert.setMobileNo(mobileNo);
            itrAlert.setItrMode(mode);
            itrAlert.setUrl(url);
            itrAlert.setAlertId(alertId);
            itrAlert.setGenerateLinkId(generateLinkId);
            itrAlert.setSlno(Long.valueOf(slno));
            itrAlert.setWiNum(wiNum);
            itrAlert.setApplicantId(Long.valueOf(applicantId));
            itrAlert.setTimestamp(new Date());
            itrAlert.setCmUser(usd.getPPCNo());
            itrAlert.setCmDate(new Date());
            itrAlert.setClientTxnId(uuidVal);
            itrAlert.setDelFlg("N");
            itrAlertRepository.save(itrAlert);
            if ("sms".equalsIgnoreCase(mode)) {
                ResponseDTO maildata = sendSMSLink(mobileNo, url, applicantId, wiNum, slno, Long.valueOf(applicantId),uuidVal);
                log.info("SMS link sent for applicant ID: {}", applicantId);
                return "{\"status\":\"success\",\"message\":\"SMS link sent to the mobile.\"}";
            } else {
                log.info("ITR request handled successfully for applicant ID: {}", applicantId);
                return apiResponse;
            }
        } catch (Exception e) {
            log.error("Error occurred while processing ITR request for applicant ID: {}", applicantId, e);
            throw new RuntimeException("An error occurred while processing the ITR request", e);
        }
    }

    private String fetchITRGenLinkUpload(String mobileNo, String pan, String dob, String applicantId, String wiNum, List<String> itrYearsList, List<String> form26asYearsList, List<String> form16YearsList,String uuidVal) {
        log.info("Fetching ITR Gen Link Upload for applicant ID: {}", applicantId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        request.put("merchantCode", merchantCode);
        request.put("merchantName", merchantName);
        request.put("UUID", uuidVal);
        request.put("transactionCompleteUrl", "https://onlineuat.southindianbank.com/ibanklosext/callback");
        request.put("pan", pan);
        request.put("redirectionUrl", "https://ibanklos.sib.co.in/ibanklos/sample");
        request.put("service", "initiateTransaction");
        Map<String, Object> optionalConfigParams = new HashMap<>();
        optionalConfigParams.put("latestYearFilesRequired", "false");
        optionalConfigParams.put("acceptancePolicyEnabled", "true");
        optionalConfigParams.put("minAllowedYearsForForm16", 1);
        optionalConfigParams.put("itrvYearsList", itrYearsList);
        optionalConfigParams.put("minAllowedYearsForItr", 2);
        optionalConfigParams.put("mandatoryDocumentTypes", Arrays.asList("ITR_PDF"));
        optionalConfigParams.put("minAllowedYearsForItrv", 2);
        optionalConfigParams.put("itrYearsList", itrYearsList);
        optionalConfigParams.put("form26asYearsList", form26asYearsList);
        optionalConfigParams.put("form16YearsList", form16YearsList);
        optionalConfigParams.put("minAllowedYearsForForm26as", 2);
        request.put("optionalConfigParams", optionalConfigParams);
        request.put("additionalParams", "");
        request.put("scanned", "false");
        request.put("clientTransactionId", uuidVal);
        request.put("type", "IncomeTaxStatementUpload");
        requestBody.put("request", request);
        requestBody.put("mock", false);
        requestBody.put("apiName", "itrGenLinkUpload");
        requestBody.put("workItemNumber", wiNum);
        requestBody.put("origin", applicantId);
        HttpEntity entity = new HttpEntity<>(convertToJson(requestBody), headers);
        try {
            String response = restTemplate.postForObject(
                    integratorEndpoint,
                    entity,
                    String.class
            );
            log.info("Successfully fetched ITR Gen Link Upload for applicant ID: {}", applicantId);
            return response;
        } catch (Exception e) {
            log.error("Error occurred while fetching ITR Gen Link Upload for applicant ID: {}", applicantId, e);
            throw new RuntimeException("Failed to fetch ITR Gen Link Upload", e);
        }
    }


    public String fetchITRGenLink(String mobileNo, String pan, String dob, String apiName, String applicantId, String wiNum,String uuidVal) {
        log.info("Fetching ITR Gen Link for applicant ID: {}", applicantId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        request.put("merchantCode", merchantCode);
        request.put("merchantName", merchantName);
        request.put("UUID", uuidVal);
        request.put("version", "2.0.0");
        request.put("pan", pan);
        request.put("dob", dob);
        if(devMode) {
            request.put("transactionCompleteUrl", "https://onlineuat.southindianbank.com/ibanklosext/callbackITRAPI");
        } else {
            request.put("transactionCompleteUrl", "https://online.southindianbank.com/ibanklosext/ITRcallBack");
        }
        request.put("redirectUrl", "https://online.southindianbank.com/ibanklosext");
        requestBody.put("request", request);
        requestBody.put("mock", false);
        requestBody.put("apiName", apiName);
        requestBody.put("workItemNumber", wiNum);
        requestBody.put("origin", applicantId);
        HttpEntity entity = new HttpEntity<>(convertToJson(requestBody), headers);
        try {
            String response = restTemplate.postForObject(
                    integratorEndpoint,
                    entity,
                    String.class
            );
            log.info("Successfully fetched ITR Gen Link for applicant ID: {}", applicantId);
            return response;
        } catch (Exception e) {
            log.error("Error occurred while fetching ITR Gen Link for applicant ID: {}", applicantId, e);
            throw new RuntimeException("Failed to fetch ITR Gen Link", e);
        }
    }

    public ResponseDTO sendSMSLink(String mobileNo, String url, String emailId, String wiNum, String slno, Long applicantId,String clientTxnId) throws Exception {
          String code = "", desc = "";
        VehicleLoanApplicant applicant = vehicleLoanApplicantRepository.findById(applicantId)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));
        String applicant_emailId = applicant.getBasicapplicants().getEmailId();
        String applicantName = applicant.getApplName();
        log.info("Sending mail to the customer {}", applicant_emailId);
        String content = String.format(
        "M/s Perfios has been appointed by SOUTH INDIAN BANK for extraction and analysis of ITR data.\n\n" +
        "In order to initiate the process, we are appending the link to the Perfios portal which will enable you to login and fetch your ITR details.\n\n" +
        "Perfios reference Number is %s\n" +
        " . <b><a href=\"%s\">Click here to proceed</a> </b>\n\n" +
        "<br/>If the above link is not working, please contact your branch.\n" ,
        wiNum, // Assuming slno is the Perfios reference number
        url
    );
        SMSEmailDTO emailDTO = new SMSEmailDTO();
                emailDTO.setSlno(Long.valueOf(slno));
                emailDTO.setWiNum(wiNum);
                emailDTO.setSentUser(usd.getPPCNo());
                emailDTO.setAlertId("ITRALERT -"+clientTxnId);
                emailDTO.setReqType("E");
                emailDTO.setEmailFrom("sibmailer@sib.bank.in");
                if (!devMode) {
                    emailDTO.setEmailTo(applicant_emailId);
                } else {
                    emailDTO.setEmailTo("infobanksib@gmail.com");
                }
                emailDTO.setEmailBody(content);
                emailDTO.setCustName(applicantName);
                emailDTO.setEmailSubject("ITR Details upload for Application "+wiNum+" – South Indian Bank");
                ResponseDTO email_ = mailService.insertSMSEmail(emailDTO);
                if (email_.getStatus().equalsIgnoreCase("F")) {
                    code = "F";
                    desc = "SMS Sent Successfully," + email_.getMsg();
                } else {
                    code = "S";
                    desc = "SMS and Email Sent Successfully";
                }
                 return new ResponseDTO(code,desc);
    }

    private String convertToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Error converting object to JSON", e);
            throw new RuntimeException("Error converting to json", e);
        }
    }

    private String generateUniqueIdentifier(String wiNum, String applicantId) {
        String baseIdentifier = wiNum.toLowerCase() + "-" + applicantId;
        LocalDateTime now = LocalDateTime.now();
        String timeComponent = String.format("%02d%02d%02d", now.getDayOfMonth(), now.getHour(), now.getMinute());
        return baseIdentifier + "-" + timeComponent;
    }


    public String fetchLatestCompletedITRTransactionId(String applicantId, String wiNum) throws JsonProcessingException {
        log.info("Fetching latest completed ITR transaction ID for applicant ID: {}", applicantId);
        String itrdetails = "";
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> result = new HashMap<>();


        // First, try to find the latest record with null perfiosTransactionId
        List<VehicleLoanITR> latestIncompleteRecords = itrAlertRepository.findByApplicantIdAndWiNumAndPerfiosTransactionIdIsNullOrFetchResponseIsNullAndDelFlgOrderByTimestampDesc(
                Long.valueOf(applicantId), wiNum, "N");

        String latestCompletedTransactionId = null;
        VehicleLoanITR completedITR = null;

        if (!latestIncompleteRecords.isEmpty()) {
            // Process the latest incomplete record
            VehicleLoanITR latestIncompleteRecord = latestIncompleteRecords.get(0);
            String response = "";
            if ("upload".equals(latestIncompleteRecord.getItrMode())) {
                response = checkITRUploadTxnStatus(latestIncompleteRecord.getGenerateLinkId(), applicantId, wiNum,latestIncompleteRecord.getClientTxnId());
            } else if ("sms".equals(latestIncompleteRecord.getItrMode())) {
                response = checkITRTxnStatus(latestIncompleteRecord.getGenerateLinkId(), applicantId, wiNum,latestIncompleteRecord.getClientTxnId());
            }

            try {
                Map<String, Object> responseMap = new ObjectMapper().readValue(response, Map.class);
                Map<String, Object> body = (Map<String, Object>) (responseMap.get("Response") != null ? ((Map<String, Object>) responseMap.get("Response")).get("Body") : null);
                if (body != null && body.containsKey("message")) {
                    List<Map<String, Object>> messages = (List<Map<String, Object>>) body.get("message");
                    for (Map<String, Object> message : messages) {
                        String status = (String) message.get("apiStatus");
                        if ("COMPLETED".equalsIgnoreCase(status)) {
                            String perfiosTransactionId = (String) message.get("perfiosTransactionId");
                            if (perfiosTransactionId != null) {
                                latestCompletedTransactionId = perfiosTransactionId;
                                completedITR = latestIncompleteRecord;
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error processing ITR transaction status for alert ID: {}", latestIncompleteRecord.getIno(), e);
            }
        }

        // If no completed transaction found among incomplete records, find the latest completed record
        if (latestCompletedTransactionId == null) {
            VehicleLoanITR latestCompletedITR = itrAlertRepository.findTopByApplicantIdAndWiNumAndPerfiosTransactionIdIsNotNullAndDelFlgOrderByTimestampDesc(
                    Long.valueOf(applicantId), wiNum, "N");
            if (latestCompletedITR != null) {
                latestCompletedTransactionId = latestCompletedITR.getPerfiosTransactionId();
                completedITR = latestCompletedITR;
            }
        }

        if (completedITR != null) {
            // Update the completed record if it was originally incomplete
            if (completedITR.getPerfiosTransactionId() == null) {
                completedITR.setPerfiosTransactionId(latestCompletedTransactionId);
                itrAlertRepository.save(completedITR);
            }

            // Mark all other records as deleted
            List<VehicleLoanITR> allRecords = itrAlertRepository.findByApplicantIdAndWiNumAndDelFlg(Long.valueOf(applicantId), wiNum, "N");
            for (VehicleLoanITR itr : allRecords) {
                if (!itr.equals(completedITR)) {
                    itr.setDelFlg("Y");
                    itr.setDelUser(usd.getPPCNo());
                    itr.setDelDate(new Date());
                    itrAlertRepository.save(itr);
                }
            }
            if (completedITR.getItrMode().equals("upload")) {
                itrdetails = checkITRUploadTxnStatus(latestCompletedTransactionId, applicantId, wiNum,completedITR.getClientTxnId());
            } else {
                itrdetails = fetchITRReport(latestCompletedTransactionId, applicantId, wiNum);
            }
        } else {
            log.warn("No completed ITR found for the applicant ID: {} and wiNum: {}",applicantId,wiNum);
            result.put("status", "error");
            result.put("message", "No completed ITR found");
            itrdetails=objectMapper.writeValueAsString(result);

        }


        log.info("Latest completed ITR transaction ID for applicant ID {}: {}", applicantId, latestCompletedTransactionId);
        return itrdetails;
    }



    private boolean isITRCompleted(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            return rootNode.has("itrData");
        } catch (Exception e) {
            log.error("Error parsing ITR response", e);
            return false;
        }
    }

    public String checkITRUploadTxnStatus(String perfiosTransactionId, String applicantId, String wiNum,String clientTxnId) {
        log.info("Checking ITR Upload transaction status for applicant ID: {}", applicantId);
         String uuidVal = wiNum.toLowerCase()+"-"+applicantId;

        VehicleLoanApplicant applicant = vehicleLoanApplicantRepository.findById(Long.valueOf(applicantId))
                .orElseThrow(() -> new RuntimeException("Applicant not found"));
        VehicleLoanITR vehicleLoanITR = itrAlertRepository.findByApplicantIdAndWiNumAndPerfiosTransactionId(Long.valueOf(applicantId), wiNum, perfiosTransactionId);
        if (vehicleLoanITR == null) {
            log.warn("VehicleLoanITR not found for perfiosTransactionId: {}. This might be due to a deleted record.", perfiosTransactionId);
            return null;
        }
        String bpmFolderName = applicant.getBpmFolderName();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        request.put("merchantCode", merchantCode);
        request.put("merchantName", merchantName);
        request.put("UUID", clientTxnId);
        request.put("version", "2.0.0");
        request.put("service", "transactionStatus");
        request.put("type", "IncomeTaxStatementUpload");
        request.put("transactionId", perfiosTransactionId);
        requestBody.put("bpmFolderName", bpmFolderName);
        requestBody.put("request", request);
        requestBody.put("mock", false);
        requestBody.put("apiName", "itrUploadReportFetch");
        requestBody.put("workItemNumber", wiNum);
        requestBody.put("origin", applicantId);
        HttpEntity<String> entity = new HttpEntity<>(convertToJson(requestBody), headers);
        try {
            String response = restTemplate.postForObject(integratorEndpoint, entity, String.class);
            updateVehicleLoanITRWithResponse(response, applicantId, wiNum,vehicleLoanITR.getClientTxnId());
            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.has("itrData")) {
                // Completed response
                JsonNode itrDataNode = rootNode.path("itrData");

                // Update current record
                vehicleLoanITR.setFetchResponse(response);
                vehicleLoanITR.setCmUser(usd.getPPCNo());
                vehicleLoanITR.setCmDate(new Date());

                if (!itrDataNode.isMissingNode()) {
                    if (itrDataNode.has("monthlyGrossIncome")) {
                        BigDecimal monthlyGrossIncome = new BigDecimal(itrDataNode.get("monthlyGrossIncome").asText());
                        vehicleLoanITR.setMonthlyGrossIncome(monthlyGrossIncome);
                    }

                    JsonNode itrDataArray = itrDataNode.path("itrData");
                    if (itrDataArray.isArray()) {
                        BigDecimal monthlyTotalIncome = BigDecimal.ZERO;
                        int pritrMonths = Integer.parseInt(bankService.getMisPRM("PRITRMONTHS").getPVALUE());

                        for (JsonNode itr : itrDataArray) {
                            if (itr.has("grossTotalIncome")) {
                                BigDecimal totalIncome = new BigDecimal(itr.get("totalIncome").asText());
                                monthlyTotalIncome = monthlyTotalIncome.add(totalIncome.divide(new BigDecimal(pritrMonths), 2, RoundingMode.HALF_UP));
                            }
                        }

                        vehicleLoanITR.setMonthlyTotalIncome(monthlyTotalIncome);
                    }
                }
                itrAlertRepository.save(vehicleLoanITR);
                log.info("Successfully processed completed ITR report for applicant ID: {}", applicantId);
            } else {
                // Pending response
                JsonNode messageNode = rootNode.path("Response").path("Body").path("message");
                String status = messageNode.path("code").asText();
                log.info("ITR processing status for applicant ID {}: {}", applicantId, status);
            }

            return response;
        } catch (Exception e) {
            log.error("Error occurred while checking ITR transaction status for applicant ID: {}", applicantId, e);
            throw new RuntimeException("Failed to check ITR transaction status", e);
        }
    }
    public boolean hasActiveITRProcess(Long applicantId, String wiNum) {
        log.info("Checking for active ITR process for applicant ID: {} and wiNum: {}", applicantId, wiNum);
        return itrAlertRepository.existsByApplicantIdAndWiNumAndDelFlg(applicantId, wiNum, "N");
    }
    public boolean hasActiveBSAProcess(Long applicantId, String wiNum) {
        log.info("Checking for active BSA process for applicant ID: {} and wiNum: {}", applicantId, wiNum);
        return bsaDetailsRepository.existsByApplicantIdAndWiNumAndDelFlg(applicantId, wiNum, "N");
    }
    @Transactional
    public void resetActiveITRProcess(Long applicantId, String wiNum) {
        log.info("Resetting active ITR process for applicant ID: {} and wiNum: {}", applicantId, wiNum);
        List<VehicleLoanITR> activeITRs = itrAlertRepository.findByApplicantIdAndWiNumAndDelFlg(
                applicantId, wiNum, "N");

        for (VehicleLoanITR itr : activeITRs) {
            itr.setDelFlg("Y");
            itr.setDelUser(usd.getPPCNo());
            itr.setDelDate(new Date());
        }

        itrAlertRepository.saveAll(activeITRs);
        log.info("Reset {} active ITR processes", activeITRs.size());
    }
    @Transactional
    public void resetActiveBSAProcess(Long applicantId, String wiNum) {
        log.info("Resetting active ITR process for applicant ID: {} and wiNum: {}", applicantId, wiNum);
        List<VehicleLoanBSA> activeBSAs = bsaDetailsRepository.findByApplicantIdAndWiNumAndDelFlg(
                applicantId, wiNum, "N");

        for (VehicleLoanBSA bsa : activeBSAs) {
            bsa.setDelFlg("Y");
            bsa.setDelUser(usd.getPPCNo());
            bsa.setDelDate(new Date());
        }

        bsaDetailsRepository.saveAll(activeBSAs);
        log.info("Reset {} active ITR processes", activeBSAs.size());
    }
    public VehicleLoanITR getLatestITRStatus(Long applicantId, String wiNum) {
        return itrAlertRepository.findTopByApplicantIdAndWiNumAndDelFlgOrderByTimestampDesc(applicantId, wiNum, "N")
                .orElse(null);
    }
    public VehicleLoanBSA getLatestBSAStatus(Long applicantId, String wiNum) {
        return bsaDetailsRepository.findTopByApplicantIdAndWiNumAndDelFlgOrderByTimestampDesc(applicantId, wiNum, "N")
                .orElse(null);
    }



    public String checkITRTxnStatus(String generateLinkId, String applicantId, String wiNum,String clientTxnId) {
        log.info("Checking ITR transaction status for applicant ID: {}", applicantId);
         String uuidVal = wiNum.toLowerCase()+"-"+applicantId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        request.put("merchantCode", merchantCode);
        request.put("merchantName", merchantName);
        request.put("UUID", clientTxnId);
        request.put("version", "2.0.0");
        request.put("transactionId", generateLinkId);
        requestBody.put("request", request);
        requestBody.put("mock", false);
        requestBody.put("apiName", "itrTxnStatusCheck");
        requestBody.put("workItemNumber", wiNum);
        requestBody.put("origin", applicantId);
        String clientTxnid="NA";
        HttpEntity<String> entity = new HttpEntity<>(convertToJson(requestBody), headers);
        try {
            String response = restTemplate.postForObject(integratorEndpoint, entity, String.class);
            log.info("Successfully checked ITR transaction status for applicant ID: {}", applicantId);
            updateVehicleLoanITRWithResponse(response, applicantId, wiNum,clientTxnId);
            return response;
        } catch (Exception e) {
            log.error("Error occurred while checking ITR transaction status for applicant ID: {}", applicantId, e);
            throw new RuntimeException("Failed to check ITR transaction status", e);
        }
    }

    public String fetchITRReport(String perfiosTransactionId, String applicantId, String wiNum) {
        log.info("Fetching ITR report for applicant ID: {}", applicantId);
         String uuidVal = wiNum.toLowerCase()+"-"+applicantId;
        VehicleLoanApplicant applicant = vehicleLoanApplicantRepository.findById(Long.valueOf(applicantId))
                .orElseThrow(() -> new RuntimeException("Applicant not found"));
        VehicleLoanITR vehicleLoanITR = itrAlertRepository.findByApplicantIdAndWiNumAndPerfiosTransactionId(Long.valueOf(applicantId), wiNum, perfiosTransactionId);
        if (vehicleLoanITR == null) {
            throw new RuntimeException("VehicleLoanITR not found");
        }
        String bpmFolderName = applicant.getBpmFolderName();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        request.put("merchantCode", merchantCode);
        request.put("merchantName",merchantName);
        request.put("UUID", uuidVal);
        request.put("reportTransactionId", perfiosTransactionId);
        requestBody.put("request", request);
        requestBody.put("workItemNumber", wiNum);
        requestBody.put("bpmFolderName", bpmFolderName);
        requestBody.put("origin", applicantId);
        requestBody.put("mock", false);
        requestBody.put("apiName", "itrReportFetch");
        HttpEntity<String> entity = new HttpEntity<>(convertToJson(requestBody), headers);
        try {
            String response = restTemplate.postForObject(integratorEndpoint, entity, String.class);
            vehicleLoanITR.setFetchResponse(response);
            vehicleLoanITR.setCmUser(usd.getPPCNo());
            vehicleLoanITR.setCmDate(new Date());
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode itrDataNode = rootNode.path("itrData");

            if (!itrDataNode.isMissingNode()) {
                // Set monthlyGrossIncome
                if (itrDataNode.has("monthlyGrossIncome")) {
                    BigDecimal monthlyGrossIncome = new BigDecimal(itrDataNode.get("monthlyGrossIncome").asText());
                    vehicleLoanITR.setMonthlyGrossIncome(monthlyGrossIncome);
                }

                // Calculate monthlyTotalIncome
                JsonNode itrDataArray = itrDataNode.path("itrData");
                if (itrDataArray.isArray()) {
                    BigDecimal monthlyTotalIncome = BigDecimal.ZERO;
                    int pritrMonths = Integer.parseInt(bankService.getMisPRM("PRITRMONTHS").getPVALUE());

                    for (JsonNode itr : itrDataArray) {
                        if (itr.has("grossTotalIncome")) {
                            BigDecimal totalIncome = new BigDecimal(itr.get("totalIncome").asText());
                            monthlyTotalIncome = monthlyTotalIncome.add(totalIncome.divide(new BigDecimal(pritrMonths), 2, RoundingMode.HALF_UP));
                        }
                    }

                    vehicleLoanITR.setMonthlyTotalIncome(monthlyTotalIncome);
                }
            }
            itrAlertRepository.save(vehicleLoanITR);
            log.info("Successfully fetched ITR report for applicant ID: {}", applicantId);
            return response;
        } catch (Exception e) {
            log.error("Error occurred while fetching ITR report for applicant ID: {}", applicantId, e);
            throw new RuntimeException("Failed to fetch ITR report", e);
        }
    }

    public boolean hasITREntries(String applicantId, String wiNum) {
        return !itrAlertRepository.findByApplicantIdAndWiNum(Long.valueOf(applicantId), wiNum).isEmpty();
    }

    @Transactional
    public String handleBSARequest(String txnId, String institutionId, String yearMonthFrom, String yearMonthTo, String loanType, String applicantId, String wiNum, String slno) {
        log.info("Handling BSA request for applicant ID: {}", applicantId);
         String uuidVal = wiNum.toLowerCase()+"-"+applicantId;
        if (institutionId == null || yearMonthFrom == null || yearMonthTo == null || loanType == null || applicantId == null || wiNum == null || slno == null) {
            throw new IllegalArgumentException("All parameters are required for BSA request");
        }
        try {
            String uid = UUID.randomUUID().toString();
            txnId = txnId + uid;
            String apiResponse = fetchBSAGenLink(txnId, institutionId, yearMonthFrom, yearMonthTo, loanType, "bsaGenLink", applicantId, wiNum);
            String url = null;
            Map<String, Object> responseMap = new ObjectMapper().readValue(apiResponse, Map.class);

            if (responseMap != null && responseMap.containsKey("Response")) {
                Map<String, Object> responseSubMap = (Map<String, Object>) responseMap.get("Response");
                if (responseSubMap != null && responseSubMap.containsKey("Body")) {
                    Map<String, Object> bodyMap = (Map<String, Object>) responseSubMap.get("Body");
                    if (bodyMap != null && bodyMap.containsKey("message")) {
                        Map<String, Object> messageMap = (Map<String, Object>) bodyMap.get("message");
                        if (messageMap != null && messageMap.containsKey("Success")) {
                            Map<String, String> successMap = (Map<String, String>) messageMap.get("Success");
                            if (successMap != null && successMap.containsKey("url")) {
                                url = successMap.get("url");
                            }
                        }
                    }
                }
            }
            VehicleLoanBSA bsaDetails = new VehicleLoanBSA();
            String alertId = "BSAALERT";
            bsaDetails.setAlertId(alertId);
            bsaDetails.setTxnId(txnId);
            bsaDetails.setSlno(Long.valueOf(slno));
            bsaDetails.setWiNum(wiNum);
            bsaDetails.setApplicantId(Long.valueOf(applicantId));
            bsaDetails.setTimestamp(new Date());
            bsaDetails.setUrl(url);
            bsaDetails.setDelFlg("N");
            bsaDetails.setBank(institutionId);
            bsaDetails.setStartDate(yearMonthFrom);
            bsaDetails.setEndDate(yearMonthTo);
            bsaDetails.setCmUser(usd.getEmployee().getPpcno());
            bsaDetails.setCmDate(new Date());
            bsaDetailsRepository.save(bsaDetails);
            log.info("BSA request handled successfully for applicant ID: {}", applicantId);
            return apiResponse;
        } catch (Exception e) {
            log.error("Error occured while processing BSA request for applicant ID: {}", applicantId);
            throw new RuntimeException("An error occured while processing the BSA request", e);
        }

    }

    private String fetchBSAGenLink(String txnId, String institutionId, String yearMonthFrom, String yearMonthTo, String loanType, String apiName, String applicantId, String wiNum) {
        log.info("Fetching BSA request for applicant ID: {}", applicantId);
        String uuidVal = wiNum.toLowerCase()+"-"+applicantId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        request.put("merchantCode", merchantCode);
        request.put("merchantName", merchantName);
        request.put("UUID", uuidVal);
        request.put("service", "initiateTransaction");
        request.put("version", "2");
        request.put("txnId", txnId);
        request.put("loanAmount", "1");
        request.put("loanDuration", "24");
        request.put("loanType", loanType);
        request.put("institutionId", institutionId);
        request.put("processingType", "statement");
        request.put("transactionCompleteCallbackUrl", "https://onlineuat.southindianbank.com/ibanklosext/callback");
        request.put("redirectUrl", "https://ibanklos.sib.co.in/ibanklos/sample");
        request.put("acceptancePolicy", "atLeastOneTransactionInRange");
        request.put("uploadingScannedStatements", "false");
        request.put("yearMonthFrom", yearMonthFrom);
        request.put("yearMonthTo", yearMonthTo);
        requestBody.put("request", request);
        requestBody.put("mock", false);
        requestBody.put("apiName", apiName);
        requestBody.put("workItemNumber", wiNum);
        requestBody.put("origin", applicantId);
        HttpEntity entity = new HttpEntity<>(convertToJson(requestBody), headers);
        try {
            String response = restTemplate.postForObject(
                    integratorEndpoint,
                    entity,
                    String.class
            );
            log.info("Successfully fetched BSA Gen Link for applicant ID: {}", applicantId);
            return response;
        } catch (Exception e) {
            log.error("Error occurred while fetching BSA Gen Link for applicant ID: {}", applicantId, e);
            throw new RuntimeException("Failed to fetch BSA Gen Link", e);
        }
    }

    public String fetchLatestCompletedBSATransactionId(String applicantId, String wiNum) {
        log.info("Fetching latest completed BSA transaction ID for applicant ID: {}", applicantId);

        // First, try to find the latest record with null perfiosTransactionId
        List<VehicleLoanBSA> latestIncompleteRecords = bsaDetailsRepository.findByApplicantIdAndWiNumAndPerfiosTransactionIdIsNullAndDelFlgOrderByTimestampDesc(
                Long.valueOf(applicantId), wiNum, "N");

        String latestCompletedTransactionId = null;
        VehicleLoanBSA completedBsa = null;

        if (!latestIncompleteRecords.isEmpty()) {
            // Process the latest incomplete record
            VehicleLoanBSA latestIncompleteRecord = latestIncompleteRecords.get(0);
            String response = checkBSATxnStatus(latestIncompleteRecord.getTxnId(), applicantId, wiNum);
            updateBSAWithResponse(response,applicantId,wiNum);
            try {
                Map<String, Object> responseMap = new ObjectMapper().readValue(response, Map.class);
                Map<String, Object> body = (Map<String, Object>) (responseMap.get("Response") != null ? ((Map<String, Object>) responseMap.get("Response")).get("Body") : null);
                if (body != null && body.containsKey("message")) {
                    Map<String, Object> message = (Map<String, Object>) body.get("message");
                    if (message != null && message.containsKey("Status")) {
                        Map<String, Object> status = (Map<String, Object>) message.get("Status");
                        if (status != null && status.containsKey("processing") && status.containsKey("Part")) {
                            String processing = (String) status.get("processing");
                            Map<String, Object> part = (Map<String, Object>) status.get("Part");
                            if ("completed".equalsIgnoreCase(processing) && part != null && part.containsKey("status") && "success".equalsIgnoreCase((String) part.get("status"))) {
                                String perfiosTransactionId = (String) part.get("perfiosTransactionId");
                                if (perfiosTransactionId != null) {
                                    latestCompletedTransactionId = perfiosTransactionId;
                                    completedBsa = latestIncompleteRecord;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error processing BSA transaction status for alert ID: {}", latestIncompleteRecord.getIno(), e);
            }
        }

        // If no completed transaction found among incomplete records, find the latest completed record
        if (latestCompletedTransactionId == null) {
            VehicleLoanBSA latestCompletedBsa = bsaDetailsRepository.findTopByApplicantIdAndWiNumAndPerfiosTransactionIdIsNotNullAndDelFlgOrderByTimestampDesc(
                    Long.valueOf(applicantId), wiNum, "N");
            if (latestCompletedBsa != null) {
                latestCompletedTransactionId = latestCompletedBsa.getPerfiosTransactionId();
                completedBsa = latestCompletedBsa;
            }
        }

        if (completedBsa != null) {
            // Update the completed record if it was originally incomplete
            if (completedBsa.getPerfiosTransactionId() == null) {
                completedBsa.setPerfiosTransactionId(latestCompletedTransactionId);
                bsaDetailsRepository.save(completedBsa);
            }

            // Mark all other records as deleted
            List<VehicleLoanBSA> allRecords = bsaDetailsRepository.findByApplicantIdAndWiNumAndDelFlg(Long.valueOf(applicantId), wiNum, "N");
            for (VehicleLoanBSA bsa : allRecords) {
                if (!bsa.equals(completedBsa)) {
                    bsa.setDelFlg("Y");
                    bsa.setDelUser(usd.getPPCNo());
                    bsa.setDelDate(new Date());
                    bsaDetailsRepository.save(bsa);
                }
            }
        }

        log.info("Latest completed BSA transaction ID for applicant ID {}: {}", applicantId, latestCompletedTransactionId);
        return latestCompletedTransactionId;
    }
    @Transactional
    public void updateBSAWithResponse(String statusResponse, String applicantId, String wiNum) {
        log.info("Updating BSA details for applicant ID: {} and wiNum: {}", applicantId, wiNum);
        try {
            if (statusResponse == null || statusResponse.isEmpty()) {
                log.warn("Received null or empty status response for applicant ID: {} and wiNum: {}", applicantId, wiNum);
                return;
            }

            JsonNode rootNode = objectMapper.readTree(statusResponse);
            JsonNode messageNode = rootNode.path("Response").path("Body").path("message");
            JsonNode statusNode = messageNode.path("Status");

            String processing = statusNode.path("processing").asText();
            JsonNode partNode = statusNode.path("Part");
            String status = partNode.path("status").asText();
            String perfiosTransactionId = partNode.path("perfiosTransactionId").asText();
            String reason = partNode.path("reason").asText();
            String errorCode = partNode.path("errorCode").asText();

            // Find the VehicleLoanBSA record to update
            VehicleLoanBSA vehicleLoanBSA = bsaDetailsRepository.findTopByApplicantIdAndWiNumAndDelFlgOrderByTimestampDesc(
                    Long.valueOf(applicantId), wiNum, "N").orElseThrow(() -> new RuntimeException("No VehicleLoanBSA found for applicant ID: "+applicantId+" and wiNum: "+ wiNum));

            // Update the VehicleLoanBSA record
            vehicleLoanBSA.setPerfiosTransactionId(perfiosTransactionId);
            vehicleLoanBSA.setPerfiosStatus(status);
            vehicleLoanBSA.setMessage(reason);
            vehicleLoanBSA.setErrorCode(errorCode);
            vehicleLoanBSA.setUpdated(new Date());



            bsaDetailsRepository.save(vehicleLoanBSA);
            log.info("Successfully updated VehicleLoanBSA for applicant ID: {}", applicantId);
        } catch (JsonProcessingException e) {
            log.error("Error parsing status response for applicant ID: {} and wiNum: {}", applicantId, wiNum, e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while updating VehicleLoanBSA for applicant ID: {}", applicantId, e);
        }
    }


    public String fetchLatestCompletedBSATransactionIdEx(String applicantId, String wiNum) {
        log.info("Fetching latest completed BSA transaction ID for applicant ID: {}", applicantId);
        List<VehicleLoanBSA> alerts = bsaDetailsRepository.findByApplicantIdAndWiNumWithNullPerfiosTransactionId(Long.valueOf(applicantId), wiNum);
        String latestCompletedTransactionId = null;
        VehicleLoanBSA completedBsa = null;
        Date latestDate = null;
        for (VehicleLoanBSA alert : alerts) {
            String response = checkBSATxnStatus(alert.getTxnId(), applicantId, wiNum);
            try {
                Map<String, Object> responseMap = new ObjectMapper().readValue(response, Map.class);
                Map<String, Object> body = (Map<String, Object>) (responseMap.get("Response") != null ? ((Map<String, Object>) responseMap.get("Response")).get("Body") : null);
                if (body != null && body.containsKey("message")) {
                    Map<String, Object> message = (Map<String, Object>) body.get("message");
                    if (message != null && message.containsKey("Status")) {
                        Map<String, Object> status = (Map<String, Object>) message.get("Status");
                        if (status != null && status.containsKey("processing") && status.containsKey("Part")) {
                            String processing = (String) status.get("processing");
                            Map<String, Object> part = (Map<String, Object>) status.get("Part");
                            if ("completed".equalsIgnoreCase(processing) && part != null && part.containsKey("status") && "success".equalsIgnoreCase((String) part.get("status"))) {
                                String perfiosTransactionId = (String) part.get("perfiosTransactionId");
                                if (perfiosTransactionId != null) {
                                    if (perfiosTransactionId.equals(alert.getPerfiosTransactionId())) {
                                        log.info("Matching perfiosTransactionId found for the applicant ID and workitem{} : {} - {}", perfiosTransactionId, applicantId, wiNum);
                                        return perfiosTransactionId;
                                    } else {
                                        log.info("New completed perfiosTransactionId found for the applicant ID and workitem{} : {} - {}", perfiosTransactionId, applicantId, wiNum);
                                        latestCompletedTransactionId = perfiosTransactionId;
                                        alert.setPerfiosTransactionId(latestCompletedTransactionId);
                                        completedBsa = alert;
                                        break;
                                    }
                                }
                            } else {
                                alert.setDelFlg("Y");
                                alert.setDelDate(new Date());
                                alert.setDelUser(usd.getPPCNo());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error processing BSA transaction status for alert ID: {}", alert.getIno(), e);
            }
        }
        if (completedBsa != null) {
            // Mark all other records as deleted
            for (VehicleLoanBSA bsa : alerts) {
                if (!bsa.equals(completedBsa)) {
                    bsa.setDelFlg("Y");
                    bsa.setDelUser(usd.getPPCNo());
                    bsa.setDelDate(new Date());
                    bsaDetailsRepository.save(bsa);
                }
            }

            // Update the completed record
            completedBsa.setPerfiosTransactionId(latestCompletedTransactionId);
            bsaDetailsRepository.save(completedBsa);
        }

        log.info("Latest completed BSA transaction ID for applicant ID {}: {}", applicantId, latestCompletedTransactionId);
        return latestCompletedTransactionId;
    }

    public String checkBSATxnStatus(String generateLinkId, String applicantId, String wiNum) {
        log.info("Checking BSA transaction status for applicant ID: {}", applicantId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        request.put("merchantCode", merchantCode);
        request.put("merchantName",merchantName);
        request.put("UUID", UUID.randomUUID().toString());
        request.put("service", "transactionStatus");
        request.put("txnId", generateLinkId);
        requestBody.put("request", request);
        requestBody.put("mock", false);
        requestBody.put("apiName", "bsaTxnStatusCheck");
        requestBody.put("workItemNumber", wiNum);
        requestBody.put("origin", applicantId);
        HttpEntity<String> entity = new HttpEntity<>(convertToJson(requestBody), headers);
        try {
            String response = restTemplate.postForObject(integratorEndpoint, entity, String.class);
            log.info("Successfully checked BSA transaction status for applicant ID: {}", applicantId);
            return response;
        } catch (Exception e) {
            log.error("Error occurred while checking BSA transaction status for applicant ID: {}", applicantId, e);
            throw new RuntimeException("Failed to check BSA transaction status", e);
        }
    }

    public String fetchBSAReport(String perfiosTransactionId, String applicantId, String wiNum) {
        log.info("Fetching BSA report for applicant ID: {}", applicantId);
        VehicleLoanApplicant applicant = vehicleLoanApplicantRepository.findById(Long.valueOf(applicantId))
                .orElseThrow(() -> new RuntimeException("Applicant not found for ID: " + applicantId));
        VehicleLoanBSA vehicleLoanBSA = bsaDetailsRepository.findByApplicantIdAndWiNumAndPerfiosTransactionId(Long.valueOf(applicantId), wiNum, perfiosTransactionId);
        if (vehicleLoanBSA == null) {
            throw new RuntimeException("VehicleLoanBSA not found for applicant ID: " + applicantId);
        }
        String bpmFolderName = applicant.getBpmFolderName();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        request.put("merchantCode", merchantCode);
        request.put("merchantName",merchantName);
        request.put("UUID", UUID.randomUUID().toString());
        request.put("perfiosTransactionId", perfiosTransactionId);
        request.put("service", "reportRetrive");
        request.put("version", "3");
        request.put("reportType", "xml,xlsx");
        requestBody.put("request", request);
        requestBody.put("workItemNumber", wiNum);
        requestBody.put("bpmFolderName", bpmFolderName);
        requestBody.put("origin", applicantId);
        requestBody.put("mock", false);
        requestBody.put("apiName", "bsaReportFetch");
        HttpEntity<String> entity = new HttpEntity<>(convertToJson(requestBody), headers);
        try {
            String response = restTemplate.postForObject(integratorEndpoint, entity, String.class);
            vehicleLoanBSA.setFetchResponse(response);
            vehicleLoanBSA.setCmUser(usd.getPPCNo());
            vehicleLoanBSA.setCmDate(new Date());
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode accountInfo = rootNode.path("accountInfo");
            JsonNode bsaDataNode = rootNode.path("bsaData");
            if (!accountInfo.isMissingNode()) {
                vehicleLoanBSA.setAccountType(accountInfo.path("accountType").asText(""));
                vehicleLoanBSA.setAccountNo(accountInfo.path("accountNo").asText(""));
            } else {
                log.warn("account ino not found in the response");
                vehicleLoanBSA.setAccountType("");
                vehicleLoanBSA.setAccountNo("");
            }
            if (!bsaDataNode.isMissingNode()) {
                int totalInwBounces = bsaDataNode.path("totalInwBounces").asInt(0);
                int totalOutwBounces = bsaDataNode.path("totalOutwBounces").asInt(0);
                BigDecimal averageBankBalance = new BigDecimal(bsaDataNode.path("averageBankBalance").asText("0"));
                vehicleLoanBSA.setTotalInwBounces(totalInwBounces);
                vehicleLoanBSA.setTotalOutwBounces(totalOutwBounces);
                vehicleLoanBSA.setAvgBankBalance(averageBankBalance);
            } else {
                log.warn("bsaData not found in the response");
                vehicleLoanBSA.setTotalInwBounces(0);
                vehicleLoanBSA.setTotalOutwBounces(0);
            }
            bsaDetailsRepository.save(vehicleLoanBSA);
            log.info("Successfully fetched BSA report for applicant ID: {}", applicantId);
            return response;
        } catch (Exception e) {
            log.error("Error occurred while fetching BSA report for applicant ID: {}", applicantId, e);
            throw new RuntimeException("Failed to fetch BSA report", e);
        }
    }

    public boolean hasBSAEntries(String applicantId, String wiNum) {
        return !bsaDetailsRepository.findByApplicantIdAndWiNum(Long.valueOf(applicantId), wiNum).isEmpty();
    }

    public void updateVehicleLoanITRWithProgram(Long applicantId, VehicleLoanProgram vehicleLoanProgram) {
        log.info("Updating VehicleLoanITR with program for applicant ID: {}", applicantId);
        List<VehicleLoanITR> vehicleLoanITRList = itrAlertRepository.findByApplicantIdAndWiNum(applicantId, vehicleLoanProgram.getWiNum());
        for (VehicleLoanITR vehicleLoanITR : vehicleLoanITRList) {
            vehicleLoanITR.setVlitr(vehicleLoanProgram);
        }
        itrAlertRepository.saveAll(vehicleLoanITRList);
        log.info("Successfully updated VehicleLoanITR with program for applicant ID: {}", applicantId);
    }

    public void updateVehicleLoanBSAWithProgram(Long applicantId, VehicleLoanProgram vehicleLoanProgram) {
        log.info("Updating VehicleLoanBSA with program for applicant ID: {}", applicantId);
        List<VehicleLoanBSA> vehicleLoanBSAList = bsaDetailsRepository.findByApplicantIdAndWiNum(applicantId, vehicleLoanProgram.getWiNum());
        for (VehicleLoanBSA vehicleLoanBSA : vehicleLoanBSAList) {
            vehicleLoanBSA.setVlbsa(vehicleLoanProgram);
        }
        bsaDetailsRepository.saveAll(vehicleLoanBSAList);
        log.info("Successfully updated VehicleLoanBSA with program for applicant ID: {}", applicantId);
    }

    public void updateVehicleLoanNRIWithProgram(Long applicantId, VehicleLoanProgram vehicleLoanProgram) {
        log.info("Updating VehicleLoanProgramNRI with program for applicant ID: {}", applicantId);
        List<VehicleLoanProgramNRI> vehicleLoanNRIList = vehicleLoanProgramNriRepository.findByApplicantIdAndWiNumAndDelFlg(applicantId, vehicleLoanProgram.getWiNum(), "N");
        for (VehicleLoanProgramNRI vehicleLoanNRI : vehicleLoanNRIList) {
            vehicleLoanNRI.setVlnri(vehicleLoanProgram);
        }
        vehicleLoanProgramNriRepository.saveAll(vehicleLoanNRIList);
        log.info("Successfully updated VehicleLoanProgramNRI with program for applicant ID: {}", applicantId);
    }

    public void updateVehicleLoanSalaryWithProgram(Long applicantId, VehicleLoanProgram vehicleLoanProgram) {
        log.info("Updating VehicleLoanProgramSalary with program for applicant ID: {}", applicantId);
        List<VehicleLoanProgramSalary> vehicleLoanProgramSalaryList = vehicleLoanProgramSalaryRepository.findByApplicantIdAndWiNumAndDelFlg(applicantId, vehicleLoanProgram.getWiNum(), "N");
        for (VehicleLoanProgramSalary vehicleLoanProgramSalary : vehicleLoanProgramSalaryList) {
            vehicleLoanProgramSalary.setVlprogramSal(vehicleLoanProgram);
        }
        vehicleLoanProgramSalaryRepository.saveAll(vehicleLoanProgramSalaryList);
        log.info("Successfully updated VehicleLoanProgramSalary with program for applicant ID: {}", applicantId);
    }

    @Transactional
    public void deleteAndSaveVehicleLoanProgramSalary(Long applicantId, String wiNum, List<VehicleLoanProgramSalary> vehicleLoanProgramSalaryList) {
        log.info("Deleting and saving VehicleLoanProgramSalary for applicant ID: {}", applicantId);
        Optional<VehicleLoanProgram> optionalProgram = vehicleLoanProgramRepository.findByApplicantIdAndDelFlg(Long.parseLong(String.valueOf(applicantId)), "N");
        if (!optionalProgram.isPresent()) {
            throw new RuntimeException("VehicleLoanProgram not found for applicant ID: " + applicantId);
        }
        Optional<VehicleLoanProgram> vehicleLoanProgram = vehicleLoanProgramRepository.findByApplicantIdAndDelFlg(applicantId, "N");
        if (optionalProgram.isPresent()) {
            VehicleLoanProgram existingProgram = optionalProgram.get();
            vehicleLoanProgramSalaryRepository.deleteByVlprogramSal(existingProgram);
            for (VehicleLoanProgramSalary vehicleLoanProgramSalary : vehicleLoanProgramSalaryList) {
                if (vehicleLoanProgramSalary == null) {
                    log.warn("Null VehicleLoanProgramSalary object found in the list for applicant ID: {}", applicantId);
                    continue;
                }
                vehicleLoanProgramSalary.setVlprogramSal(existingProgram);
            }
            try {
                vehicleLoanProgramSalaryRepository.saveAll(vehicleLoanProgramSalaryList);
                log.info("Successfully saved VehicleLoanProgramSalary for applicant ID: {}", applicantId);
            } catch (Exception e) {
                log.error("Error occurred while saving VehicleLoanProgramSalary for applicant ID: {}", applicantId, e);
                throw new RuntimeException("Failed to save VehicleLoanProgramSalary", e);
            }
        }

    }

    @Transactional
    public void disableProgram(String wiNum, Long applicantId) {
        disableChildRecords(wiNum, applicantId);
        log.info("Delete the  childs of VehicleLoanProgram for applicant ID: {} wiNum:{}", applicantId, wiNum);
        log.info("Disable the  VehicleLoanProgram for applicant ID: {} wiNum:{}", applicantId, wiNum);
        VehicleLoanProgram program = vehicleLoanProgramRepository.findByApplicantIdAndDelFlg(applicantId, "N")
                .orElse(null);
        if (program != null) {
            vehicleLoanProgramRepository.delete(program);
        }
    }

    private void disableChildRecords(String wiNum, Long applicantId) {
        log.info("Disable the  FD Details for applicant ID: {} wiNum:{}", applicantId, wiNum);
        List<VehicleLoanFD> fdList = vehicleLoanFDRepository.findByApplicantIdAndWiNum(applicantId, wiNum);
        vehicleLoanFDRepository.deleteAll(fdList);

        log.info("Disable the  ITR Details for applicant ID: {} wiNum:{}", applicantId, wiNum);
        List<VehicleLoanITR> itrList = itrAlertRepository.findALLITRByApplicantIdAndWiNum(applicantId, wiNum);
        itrAlertRepository.deleteAll(itrList);

        log.info("Disable the  BSA for applicant ID: {} wiNum:{}", applicantId, wiNum);
        List<VehicleLoanBSA> bsaList = bsaDetailsRepository.findALLBSAByApplicantIdAndWiNum(applicantId, wiNum);
        bsaDetailsRepository.deleteAll(bsaList);

        log.info("Disable the NRI details for applicant ID: {} wiNum:{}", applicantId, wiNum);
        List<VehicleLoanProgramNRI> nriList = vehicleLoanProgramNriRepository.findByApplicantIdAndWiNum(applicantId, wiNum);
        vehicleLoanProgramNriRepository.deleteAll(nriList);

        log.info("Disable the Salary details for applicant ID: {} wiNum:{}", applicantId, wiNum);
        List<VehicleLoanProgramSalary> salaryList = vehicleLoanProgramSalaryRepository.findByApplicantIdAndWiNum(applicantId, wiNum);
        vehicleLoanProgramSalaryRepository.deleteAll(salaryList);
    }

    private void cleanupExpiredLinks(Long applicantId, String wiNum) {
        Date currentTime = new Date();
        Date fortyEightHoursAgo = new Date(currentTime.getTime() - (48 * 60 * 60 * 1000));

        List<VehicleLoanITR> expiredLinks = itrAlertRepository.findByApplicantIdAndWiNumAndDelFlgAndTimestampBeforeAndPerfiosTransactionIdIsNull(
                applicantId, wiNum, "N", fortyEightHoursAgo);

        for (VehicleLoanITR expiredLink : expiredLinks) {
            expiredLink.setDelFlg("Y");
            expiredLink.setDelUser(usd.getPPCNo());
            expiredLink.setDelDate(new Date());
        }

        itrAlertRepository.saveAll(expiredLinks);
        log.info("Cleaned up {} expired links for applicant ID: {}", expiredLinks.size(), applicantId);
    }

    private VehicleLoanITR findExistingValidLink(Long applicantId, String wiNum) {
        Date currentTime = new Date();
        Date fortyEightHoursAgo = new Date(currentTime.getTime() - (48 * 60 * 60 * 1000));

        List<VehicleLoanITR> recentLinks = itrAlertRepository.findByApplicantIdAndWiNumAndDelFlgAndTimestampAfterAndPerfiosTransactionIdIsNullOrderByTimestampDesc(
                applicantId, wiNum, "N", fortyEightHoursAgo);

        return recentLinks.isEmpty() ? null : recentLinks.get(0);
    }
    @Transactional
    public void updateVehicleLoanITRWithResponse(String statusResponse, String applicantId, String wiNum, String clientTxnId) {
        log.info("Updating itr details of  applicant ID: {} and wiNum: {}", applicantId, wiNum);
    try {
        if (statusResponse == null || statusResponse.isEmpty()) {
            log.warn("Received null or empty status response for applicant ID: {} and wiNum: {}", applicantId, wiNum);
            return;
        }

        JsonNode rootNode = objectMapper.readTree(statusResponse);
        JsonNode messageNode = rootNode.path("Response").path("Body").path("message");

        ResponseInfo responseInfo = new ResponseInfo();

        // Handle different response structures
        if (messageNode.isArray() && messageNode.size() > 0) {
            JsonNode firstMessage = messageNode.get(0);
            extractInfoFromNode(firstMessage, rootNode, responseInfo);
        } else if (messageNode.isObject()) {
            extractInfoFromNode(messageNode, rootNode, responseInfo);
        } else {
            log.warn("Unexpected 'message' node structure in response for applicantId: {} and wiNum: {}", applicantId, wiNum);
            return;
        }

        // Find the VehicleLoanITR record to update
        VehicleLoanITR vehicleLoanITR = findVehicleLoanITR(applicantId, wiNum, responseInfo.perfiosTransactionId,clientTxnId);

        if (vehicleLoanITR == null) {
            log.warn("No VehicleLoanITR found for applicant ID: {} and wiNum: {}", applicantId, wiNum);
            return;
        }

        updateVehicleLoanITR(vehicleLoanITR, responseInfo);

        itrAlertRepository.save(vehicleLoanITR);
        log.info("Successfully updated VehicleLoanITR for applicant ID: {}", applicantId);
    } catch (JsonProcessingException e) {
        log.error("Error parsing status response for applicant ID: {} and wiNum: {}", applicantId, wiNum, e);
    } catch (Exception e) {
        log.error("Unexpected error occurred while updating VehicleLoanITR for applicant ID: {}", applicantId, e);
    }
}

private void extractInfoFromNode(JsonNode node, JsonNode rootNode, ResponseInfo responseInfo) {
    responseInfo.perfiosTransactionId = node.path("perfiosTransactionId").asText(null);
    responseInfo.message = node.path("message").asText(null);
    responseInfo.updatedTimestamp = node.path("updated").asText(null);
    responseInfo.apiStatus = node.path("apiStatus").asText(null);
    responseInfo.errorCode = node.path("errorCode").asText(null);

    // If updatedTimestamp is not in the message node, try to get it from the header
    if (responseInfo.updatedTimestamp == null) {
        responseInfo.updatedTimestamp = rootNode.path("Response").path("Header").path("Timestamp").asText(null);
    }

    // If apiStatus is not present, try to get it from the code field
    if (responseInfo.apiStatus == null) {
        responseInfo.apiStatus = node.path("code").asText(null);
    }

    // If message is not present, try to get it from the description field
    if (responseInfo.message == null) {
        responseInfo.message = node.path("desc").asText(null);
    }
}

private VehicleLoanITR findVehicleLoanITR(String applicantId, String wiNum, String perfiosTransactionId,String clientTxnId) {
//    if (perfiosTransactionId != null) {
//        return itrAlertRepository.findByApplicantIdAndWiNumAndPerfiosTransactionId(
//                Long.valueOf(applicantId), wiNum, perfiosTransactionId);
//    } else {
        return itrAlertRepository.findTopByApplicantIdAndWiNumAndClientTxnIdAndDelFlgOrderByTimestampDesc(
                Long.valueOf(applicantId), wiNum,clientTxnId, "N");
//    }
}

private void updateVehicleLoanITR(VehicleLoanITR vehicleLoanITR, ResponseInfo responseInfo) {
    if (responseInfo.message != null) {
        vehicleLoanITR.setMessage(responseInfo.message);
    }
    if (responseInfo.perfiosTransactionId != null) {
        vehicleLoanITR.setPerfiosTransactionId(responseInfo.perfiosTransactionId);
    }
    if (responseInfo.apiStatus != null) {
        vehicleLoanITR.setPerfiosStatus(responseInfo.apiStatus);
        if(vehicleLoanITR.getMessage()==null && vehicleLoanITR.getPerfiosStatus().equals("IN_PROGRESS")) {
            vehicleLoanITR.setMessage("ITR Details processing in progress");
        } else if (vehicleLoanITR.getMessage()==null && vehicleLoanITR.getPerfiosStatus().equals("Completed")) {
            vehicleLoanITR.setMessage("ITR Details processing completed");
        }
    }

    // Parse and set the updated timestamp
    try {
        Date parsedDate;
        if (responseInfo.updatedTimestamp != null) {
            if (responseInfo.updatedTimestamp.length() == 14) {
                // Format: YYYYMMDDHHmmss
                parsedDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(responseInfo.updatedTimestamp);
            } else {
                // Format: MMM d, yyyy h:mm:ss a
                parsedDate = new SimpleDateFormat("MMM d, yyyy h:mm:ss a").parse(responseInfo.updatedTimestamp);
            }
        } else {
            // If no timestamp provided, use current time
            parsedDate = new Date();
        }
        vehicleLoanITR.setUpdated(parsedDate);
    } catch (ParseException e) {
        log.error("Error parsing updated timestamp. Received timestamp: {}", responseInfo.updatedTimestamp, e);
        // Use current time if parsing fails
        vehicleLoanITR.setUpdated(new Date());
    }
}

// Helper class to store extracted information
    @Getter
    @Setter
private static class ResponseInfo {
    String perfiosTransactionId;
    String message;
    String updatedTimestamp;
    String apiStatus;
    String errorCode;
}

}
