package com.sib.ibanklosucl.service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.dto.ResponseDTO;
import com.sib.ibanklosucl.dto.acopn.RepayAcctDTO;
import com.sib.ibanklosucl.dto.acopn.SanctionDetailsDTO;
import com.sib.ibanklosucl.dto.doc.SMSEmailDTO;
import com.sib.ibanklosucl.model.NACHMandate;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.integations.NACHMandateRepository;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@Slf4j
public class NACHMandateService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;
    @Autowired
    private NACHMandateRepository nachMandateRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${esb.MerchantName}")
    private String merchantName;
    @Value("${esb.MerchantCode}")
    private String merchantCode;
    @Value("${esb.ChannelID}")
    private String channelID;
    @Value("${api.integrator}")
    private String integratorEndpoint;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Value("${app.dev-mode:true}")
    private boolean devMode;
    @Autowired
    private SMSEmailService emailService;
    @Autowired
    private UserSessionData usd;

    public Map<String, Object> sendNachMandate(Long slno, String mode) {
        Optional<NACHMandate> existingMandate = nachMandateRepository.findBySlnoAndDelFlg(slno, "N");
        if (existingMandate.isPresent()) {
            NACHMandate mandate = existingMandate.get();
            if ("MANUAL".equals(mode)) {
                mandate.setDelFlg("Y");
                mandate.setDelUser("SYSTEM");
                mandate.setDelDate(new Date());
                nachMandateRepository.save(mandate);
            } else {
                if ("Authorization Request Rejected".equals(mandate.getStatus()) || "Rejected By NPCI".equals(mandate.getStatus())) {
                    mandate.setDelFlg("Y");
                    mandate.setDelUser(usd.getPPCNo());
                    mandate.setDelDate(new Date());
                    nachMandateRepository.save(mandate);
                    log.info("Marked existing rejected mandate as deleted for slno: {}", slno);
                } else {
                    // Return a message saying active mandate request is present
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Active mandate request is present");
                    return response;
                }
            }
        }
        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterService.findBySlno(slno);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> instalmentDates = fetchRepository.getInstalmentDates(slno);
        SanctionDetailsDTO sanctionDetails = fetchRepository.fetchSanctionDetailsFinal(slno);
        RepayAcctDTO repaymentDetails = fetchRepository.getRepaymentAcctDetails(slno);
        String pan = "", email = "", mobile = "";
        List<VehicleLoanApplicant> applicants = vehicleLoanMaster.getApplicants().stream()
                .filter(fd -> "N".equals(fd.getDelFlg()))
                .toList();
        for (VehicleLoanApplicant applicant : applicants) {
            if ("A".equals(applicant.getApplicantType())) {
                pan = applicant.getKycapplicants().getPanNo();
                email = applicant.getBasicapplicants().getEmailId();
                mobile = applicant.getBasicapplicants().getMobileNo();
            }
        }
        long roundedAmount = Math.round(Double.parseDouble(sanctionDetails.getSancEmi()) * 2);
        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
        Date now = new Date();
        String timeString = formatter.format(now);
        String refNo = slno.toString() + timeString;
        if ("MANUAL".equals(mode)) {
            return createManualNachMandate(slno, pan, email, mobile, sanctionDetails, refNo);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("UUID", vehicleLoanMaster.getWiNum());
        requestBody.put("merchantCode", merchantCode);
        requestBody.put("merchantName", merchantName);

        Map<String, Object> mandateReq = new HashMap<>();
        mandateReq.put("referenceNumber", refNo);
        mandateReq.put("utilityCode", "NACH00000000000114");
        mandateReq.put("categoryCode", "L001");
        mandateReq.put("schmNm", "Vehicle Loan");
        mandateReq.put("consRefNo", vehicleLoanMaster.getAccNumber());
        mandateReq.put("seqTp", "RCUR");
        mandateReq.put("frqcy", "MNTH");
        mandateReq.put("frstColltnDt", instalmentDates.get("instalmentStartDate"));
        mandateReq.put("fnlColltnDt", instalmentDates.get("instalmentEndDate"));
        mandateReq.put("amountTp", "MAXA");
        mandateReq.put("colltnAmt", roundedAmount);
        // removing special character from the name as this throws error from NPCI
        String customerName = vehicleLoanMaster.getCustName();
        customerName = customerName.replaceAll("[^a-zA-Z0-9]", "");
        mandateReq.put("dbtrNm", customerName);
        mandateReq.put("phone", "");
        mandateReq.put("mobile", mobile);
        mandateReq.put("email", email);
        mandateReq.put("pan", pan);
        mandateReq.put("bnkId", repaymentDetails.getBankId());
        mandateReq.put("dbtrAccTp", "SAVINGS");
        mandateReq.put("dbtrAccNo", repaymentDetails.getAccountNumber());
        requestBody.put("ManadateReq", mandateReq);

        Map<String, Object> finalRequestBody = new HashMap<>();
        finalRequestBody.put("request", requestBody);
        finalRequestBody.put("mock", false);
        finalRequestBody.put("apiName", "nachMandate");
        finalRequestBody.put("workItemNumber", vehicleLoanMaster.getWiNum());
        finalRequestBody.put("origin", slno.toString());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(finalRequestBody, headers);
        try {
            String response = restTemplate.postForObject(integratorEndpoint, entity, String.class);
            log.debug("Raw response from API for send mandate: {}", response);
            Map<String, Object> responseMap = parseJsonResponse(response);
            log.debug("Parsed response map for send mandate: {}", responseMap);

            // Get the Status Code from response
            String statusCode = getNestedString(responseMap, "Response", "Status", "Code");

            if ("406".equals(statusCode)) {
                // Get error details
                Map<String, Object> manadateReq = getNestedMap(responseMap, "Response", "Body", "ManadateReq");
                String errorMessage = (String) manadateReq.get("moreInfo");
                String errorCode = (String) manadateReq.get("code");

                log.error("NACH mandate creation failed. Error code: {}, Message: {}", errorCode, errorMessage);

                // Return error response
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", errorMessage);
                errorResponse.put("status", "error");
                errorResponse.put("code", errorCode);
                return errorResponse;
            }

            // Only process successful responses
            if ("200".equals(statusCode)) {
                processNachMandateResponse(responseMap, slno, pan, email, mobile, instalmentDates,
                        sanctionDetails, vehicleLoanMaster.getWiNum(), refNo);
            }

            Map<String, Object> responseVal = checkMandateStatus(slno);
            return responseMap;
        } catch (Exception e) {
            log.error("Error sending NACH Mandate request", e);
            throw new RuntimeException("Error sending NACH Mandate request", e);
        }

    }

    public Map<String, Object> checkMandateStatus(Long slno) {
        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterService.findBySlno(slno);
        Optional<NACHMandate> mandate = nachMandateRepository.findBySlnoAndDelFlg(slno, "N");
        String loanAccNo = vehicleLoanMaster.getAccNumber();
        if (mandate.isPresent()) {
            Map<String, Object> requestBody = createMandateStatusRequestBody(mandate.get(), slno);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, createHeaders());

            try {
                String response = restTemplate.postForObject(integratorEndpoint, entity, String.class);
                log.debug("Raw response from API for check mandate status: {}", response);
//                if (devMode) {
//                    response = "{\"Response\":{\"Header\":{\"Timestamp\":\"20240912150936\",\"APIName\":\"nach-mandate-status-check-api\",\"APIVersion\":\"2.0.0\",\"Interface\":\"NACH_MandateStatusCheck\"},\"Status\":{\"Code\":\"200\",\"Desc\":\"Success\"},\"Body\":{\"UUID\":\"VLR_000000998\",\"statusCheck\":{\"timestamp\":\"2024-09-12T09:39:36.868083506Z\",\"status\":\"200\",\"code\":\"200\",\"message\":{\"mndtType\":\"Onmag\",\"referenceNumber\":\"bff32c34a842cc9df7dad1158dc4ebj\",\"utilityCode\":\"NACH00000000000025\",\"categoryCode\":\"C001\",\"schmNm\":\"Vehicle Loan\",\"consRefNo\":\"12345678rr\",\"seqTp\":\"RCUR\",\"frqcy\":\"MNTH\",\"frstColltnDt\":\"2024-01-20\",\"fnlColltnDt\":\"2024-10-31\",\"amountTp\":\"FIXA\",\"colltnAmt\":4,\"dbtrNm\":\"raghu\",\"phone\":\"040-27777777\",\"mobile\":\"8077571608\",\"email\":\"nikhilmgopi@sib.bank.in\",\"pan\":\"BRAPG5632G\",\"bnkId\":\"ONMG\",\"dbtrAccTp\":\"SAVINGS\",\"dbtrAccNo\":\"123456\",\"actualColltnAmt\":null,\"debitDay\":null,\"autoExecute\":null,\"addnlRefNb1\":null,\"addnlRefNb2\":null,\"addnlRefNb3\":null,\"orgnlRefNb\":null,\"imageData\":null,\"imageName\":null,\"requestStatus\":\"Authorized\",\"mndtId\":\"ONMG7021204247001024\",\"reasonCode\":\"000\",\"reasonDesc\":\"N/A\",\"surl\":\"https://enachuat.southindianbank.com/hg/surl/rDlfyV\"},\"moreInfo\":\"mandate request found.\",\"reference\":\"b84ad5a6-0386-4ec5-9671-25aeb68418ad\"}}}}";
//                }

                Map<String, Object> responseMap = parseJsonResponse(response);
                log.debug("Parsed response map for check mandate status: {}", responseMap);

                updateMandateStatus(mandate.get(), responseMap, loanAccNo);

                return responseMap;
            } catch (Exception e) {
                log.error("Error checking mandate status", e);
                throw new RuntimeException("Error checking mandate status", e);
            }
        } else {
            throw new RuntimeException("No active NACH Mandate found for this loan application");
        }
    }

    public Map<String, Object> cancelMandate(Long slno) {
        Optional<NACHMandate> mandate = nachMandateRepository.findBySlnoAndDelFlg(slno, "N");
        if (mandate.isPresent()) {
            Map<String, Object> requestBody = createMandateCancelRequestBody(mandate.get(), slno);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, createHeaders());

            try {
                String response = restTemplate.postForObject(integratorEndpoint, entity, String.class);
                log.debug("Raw response from API for cancel mandate: {}", response);

                Map<String, Object> responseMap = parseJsonResponse(response);
                log.debug("Parsed response map for cancel mandate: {}", responseMap);

                processCancellationResponse(mandate.get(), responseMap);

                return responseMap;
            } catch (Exception e) {
                log.error("Error cancelling mandate", e);
                throw new RuntimeException("Error cancelling mandate", e);
            }
        } else {
            throw new RuntimeException("No active NACH Mandate found for this loan application");
        }
    }

    public Optional<NACHMandate> getNACHMandateBySlno(Long slno) {
        return nachMandateRepository.findBySlnoAndDelFlg(slno, "N");
    }

    public boolean nachMandateExists(Long slno) {
        return nachMandateRepository.existsBySlnoAndDelFlg(slno, "N");
    }

    private Map<String, Object> parseJsonResponse(String response) throws JsonProcessingException {
        try {
            return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON response", e);
            throw e;
        }
    }

    private void processNachMandateResponse(Map<String, Object> responseMap, Long slno, String pan, String email, String mobile, Map<String, String> instalmentDates, SanctionDetailsDTO sanctionDetails, String wiNum, String referenceNumber) {
        Map<String, Object> body = getNestedMap(responseMap, "Response", "Body");
        Map<String, Object> message = getNestedMap(body, "message");
        Map<String, Object> mandateReq = getNestedMap(body, "ManadateReq");

        if (message == null) {
            log.error("Message details are null");
            // throw new RuntimeException("Unable to process NACH Mandate response: message map is null");
        }
        NACHMandate nachMandate = new NACHMandate();
        nachMandate.setWinum(getNestedString(responseMap, "Response", "Body", "UUID"));
        nachMandate.setSlno(slno);
        nachMandate.setWinum(wiNum);
        nachMandate.setPan(pan);
        nachMandate.setMobile(mobile);
        nachMandate.setEmail(email);
        nachMandate.setReferenceNumber(referenceNumber);
        nachMandate.setMandateMode("DIGITAL");
        nachMandate.setCollectionAmount(Double.parseDouble(sanctionDetails.getSancAmountRecommended()));
        nachMandate.setInstalmentStartDate(LocalDate.parse(instalmentDates.get("instalmentStartDate")));
        nachMandate.setInstalmentEndDate(LocalDate.parse(instalmentDates.get("instalmentEndDate")));
        nachMandate.setTenor(Integer.parseInt(instalmentDates.get("tenor")));
        String reference = "", status = "", timestampStr = "", surl = "";
        if (message != null) {
            reference = (String) message.get("referenceNumber");
            status = (String) message.get("requestStatus");
            timestampStr = getNestedString(responseMap, "Response", "Header", "Timestamp");
            surl = (String) message.get("surl");
        } else {
            reference = (String) mandateReq.get("reference");
            status = (String) mandateReq.get("status");
        }
        nachMandate.setReference(reference);
        nachMandate.setStatus(status);
        nachMandate.setDelFlg("N");
        nachMandate.setSurl(surl);
        if (timestampStr != null && !timestampStr.isEmpty()) {
            try {
                LocalDateTime timestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                nachMandate.setTimestamp(timestamp);
            } catch (DateTimeParseException e) {
                log.error("Error parsing timestamp: " + timestampStr, e);
                nachMandate.setTimestamp(LocalDateTime.now());
            }
        } else {
            nachMandate.setTimestamp(LocalDateTime.now());
        }
        nachMandateRepository.save(nachMandate);
        log.info("NACH Mandate saved successfully for slno: {}", slno);
    }

    private void insertIntoEnachMandate(NACHMandate nachMandate, Map<String, Object> message, String loanAccNo) {
        // First, check if the record already exists
        String checkSql = "SELECT COUNT(*) FROM CUSTOM.ENACH_MANDATE@finacle10 WHERE UMRN = ? AND REFERENCE_ACCOUNT_NUMBER = ?";

        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, nachMandate.getMndtId(), loanAccNo);

        if (count == 0) {
            // If the record doesn't exist, proceed with the insert
            String insertSql = "INSERT INTO CUSTOM.ENACH_MANDATE@finacle10 (UICODE, UMRN, DATE_OF_MANDATE, MANDATE_START_DATE, MANDATE_END_DATE, CANCELLED, DEBIT_TYPE, FREQUENCY, AMOUNT, MANDATE_STATUS, REFERENCE_ACCOUNT_NUMBER, REFERENCE_ACCOUNT_TYPE, NEXT_MANDATE_DATE, SUCCESS,CHANNEL_ID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

            jdbcTemplate.update(insertSql,
                    nachMandate.getUtilityCode(),
                    nachMandate.getMndtId(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    java.sql.Date.valueOf(nachMandate.getInstalmentStartDate()),
                    java.sql.Date.valueOf(nachMandate.getInstalmentEndDate()),
                    "N",
                    "MAXIMUM",
                    "MONTHLY",
                    nachMandate.getCollectionAmount(),
                    "A",
                    loanAccNo,
                    "Loan",
                    null,
                    null,
                    "IBANKLOS"
            );

            log.info("Record inserted successfully.");
        } else {
            log.info("Record already exists. No insertion performed.");
        }
    }


    private int calculateTenor(LocalDate startDate, LocalDate endDate) {
        return (int) (endDate.toEpochDay() - startDate.toEpochDay()) / 30;
    }

    private void updateMandateStatus(NACHMandate mandate, Map<String, Object> responseMap, String loanAccNo) {
        Map<String, Object> message = getNestedMap(responseMap, "Response", "Body", "statusCheck", "message");
        if (message == null) {
            log.error("Message details are null");
            throw new RuntimeException("Unable to update status details message map is null");
        }
        String newStatus = (String) message.get("requestStatus");
        String timestampStr = "", surl = "";
        if (newStatus != null) {
            mandate.setStatus(newStatus);
            mandate.setMndtId((String) message.get("mndtId"));
            mandate.setUtilityCode((String) message.get("utilityCode"));
            mandate.setBnkId((String) message.get("bnkId"));
            mandate.setDebit_type((String) message.get("amountTp"));
            mandate.setFrequency((String) message.get("frqcy"));
            mandate.setInstalmentStartDate(LocalDate.parse((String) message.get("frstColltnDt")));
            mandate.setInstalmentEndDate(LocalDate.parse((String) message.get("fnlColltnDt")));
            mandate.setCollectionAmount(Double.parseDouble(message.get("colltnAmt").toString()));
            mandate.setTenor(calculateTenor(mandate.getInstalmentStartDate(), mandate.getInstalmentEndDate()));
            mandate.setMoreInfo(getNestedString(responseMap, "Response", "Body", "moreInfo"));
            timestampStr = getNestedString(responseMap, "Response", "Header", "Timestamp");
            surl = (String) message.get("surl");
            mandate.setSurl(surl);
            if (timestampStr != null && !timestampStr.isEmpty()) {
                try {
                    LocalDateTime timestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                    mandate.setTimestamp(timestamp);
                } catch (DateTimeParseException e) {
                    log.error("Error parsing timestamp: " + timestampStr, e);
                    mandate.setTimestamp(LocalDateTime.now());
                }
            } else {
                mandate.setTimestamp(LocalDateTime.now());
            }
            nachMandateRepository.save(mandate);

            // Insert into CUSTOM.ENACH_MANDATE@finacle10 if requestStatus is "Authorized"
            if ("Authorized".equals(newStatus)) {
                insertIntoEnachMandate(mandate, message, loanAccNo);
            }
            log.info("Updated mandate status to: {} for slno: {}", newStatus, mandate.getSlno());
        } else {
            log.error("Unable to update mandate status. requestStatus is null.");
        }
    }

    private void processCancellationResponse(NACHMandate mandate, Map<String, Object> responseMap) {
        Map<String, Object> forceInitiate = getNestedMap(responseMap, "Response", "Body", "forceIntiate");
        String status = (String) forceInitiate.get("status");
        if ("200".equals(status)) {
            mandate.setStatus("status");

            nachMandateRepository.save(mandate);
            log.info("Mandate cancelled successfully for slno: {}", mandate.getSlno());
        } else {
            log.warn("Mandate cancellation not successful. Status: {} for slno: {}", status, mandate.getSlno());
        }
    }

    private Map<String, Object> createMandateStatusRequestBody(NACHMandate mandate, Long slno) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("UUID", mandate.getWinum());
        requestBody.put("merchantCode", merchantCode);
        requestBody.put("merchantName", merchantName);
        requestBody.put("referenceNumber", mandate.getReferenceNumber());

        Map<String, Object> finalRequestBody = new HashMap<>();
        finalRequestBody.put("request", requestBody);
        finalRequestBody.put("mock", false);
        finalRequestBody.put("apiName", "nachMandateStatus");
        finalRequestBody.put("workItemNumber", mandate.getWinum());
        finalRequestBody.put("origin", slno.toString());

        return finalRequestBody;
    }

    private Map<String, Object> createMandateCancelRequestBody(NACHMandate mandate, Long slno) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("UUID", mandate.getWinum());
        requestBody.put("merchantCode", merchantCode);
        requestBody.put("merchantName", merchantName);
        requestBody.put("referenceNumber", mandate.getReferenceNumber());
        requestBody.put("forceInitiate", Map.of(
                "utilityCode", "NACH00000000000114",
                "force", "Yes"
        ));

        Map<String, Object> finalRequestBody = new HashMap<>();
        finalRequestBody.put("request", requestBody);
        finalRequestBody.put("mock", false);
        finalRequestBody.put("apiName", "nachMandateCancel");
        finalRequestBody.put("workItemNumber", mandate.getWinum());
        finalRequestBody.put("origin", slno.toString());
        return finalRequestBody;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getNestedMap(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            if (map != null && map.containsKey(key)) {
                Object value = map.get(key);
                if (value instanceof Map) {
                    map = (Map<String, Object>) value;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
        return map;
    }

    private String getNestedString(Map<String, Object> map, String... keys) {
        Map<String, Object> nestedMap = getNestedMap(map, Arrays.copyOf(keys, keys.length - 1));
        if (nestedMap != null && nestedMap.containsKey(keys[keys.length - 1])) {
            Object value = nestedMap.get(keys[keys.length - 1]);
            if (value instanceof String) {
                return (String) value;
            }
        }
        return null;
    }

    //resend nach URL
    public ResponseDTO resendNachDetails(Long slno) throws Exception {
        Optional<NACHMandate> mandateOpt = nachMandateRepository.findBySlnoAndDelFlg(slno, "N");
        if (mandateOpt.isEmpty()) {
            throw new RuntimeException("No active NACH Mandate found for this loan application");
        }

        NACHMandate mandate = mandateOpt.get();
        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterService.findBySlno(slno);

        String content = String.format(
                "<html><body>" +
                        "<p>Here are your NACH mandate details for loan application %s:</p>" +
                        "<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse;'>" +
                        "<tr><th align='left'>Detail</th><th align='left'>Value</th></tr>" +
                        "<tr><td>Reference Number</td><td>%s</td></tr>" +
                        "<tr><td>Collection Amount</td><td>%.2f</td></tr>" +
                        "<tr><td>Instalment Start Date</td><td>%s</td></tr>" +
                        "<tr><td>Instalment End Date</td><td>%s</td></tr>" +
                        "<tr><td>Status</td><td>%s</td></tr>" +
                        "</table>" +
                        "<p>To complete your NACH mandate, please click on the following link:</p>" +
                        "<p><a href='%s'>Complete NACH Mandate</a></p>" +
                        "<p>If you have any questions, please contact your branch.</p>" +
                        "</body></html>",
                vehicleLoanMaster.getWiNum(),
                mandate.getReferenceNumber(),
                mandate.getCollectionAmount(),
                mandate.getInstalmentStartDate(),
                mandate.getInstalmentEndDate(),
                mandate.getStatus(),
                mandate.getSurl()
        );


        SMSEmailDTO emailDTO = new SMSEmailDTO();
        emailDTO.setWiNum(vehicleLoanMaster.getWiNum());
        emailDTO.setReqType("E");
        emailDTO.setSlno(slno);
        emailDTO.setAlertId("NACHALERT");
        emailDTO.setEmailFrom("sibmailer@sib.bank.in");
        if (!devMode) {
            emailDTO.setEmailTo(mandate.getEmail());
            emailDTO.setEmailCc("albgenach@sib.bank.in");
        } else {
            emailDTO.setEmailTo("infobanksib@gmail.com");
            emailDTO.setEmailCc("ajithkr@sib.bank.in");
        }
        emailDTO.setEmailBody(content);
        emailDTO.setEmailSubject("NACH Mandate RefNo-" + mandate.getReferenceNumber() + " for Loan Application " + vehicleLoanMaster.getWiNum());
        ResponseDTO response = emailService.insertSMSEmail(emailDTO);
        log.info("NACH details resend attempt for slno: {}, status: {}", slno, response.getStatus());
        return response;
    }

    public Map<String, Object> createManualNachMandate(Long slno, String pan, String email, String mobile, SanctionDetailsDTO sanctionDetails, String refNo) {
        Optional<NACHMandate> existingMandate = nachMandateRepository.findBySlnoAndDelFlg(slno, "N");
        if (existingMandate.isPresent()) {
            NACHMandate mandate = existingMandate.get();
            mandate.setDelFlg("Y");
            mandate.setDelUser(usd.getPPCNo());
            mandate.setDelDate(new Date());
            nachMandateRepository.save(mandate);
        }


        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterService.findBySlno(slno);
        Map<String, String> instalmentDates = fetchRepository.getInstalmentDates(slno);
        RepayAcctDTO repaymentDetails = fetchRepository.getRepaymentAcctDetails(slno);

        NACHMandate mandate = new NACHMandate();
        mandate.setSlno(slno);
        mandate.setWinum(vehicleLoanMaster.getWiNum());
        mandate.setMandateMode("MANUAL");
        mandate.setStatus("Manual Authorized");
        mandate.setMoreInfo("Manul nach mandate created");
        mandate.setBnkId(repaymentDetails.getBankId());
        mandate.setMobile(mobile);
        mandate.setEmail(email);
        mandate.setPan(pan);
        mandate.setCollectionAmount(Double.parseDouble(sanctionDetails.getSancAmountRecommended()));
        mandate.setReference(refNo);
        mandate.setReferenceNumber(refNo);
        mandate.setCollectionAmount(0.0);
        mandate.setInstalmentStartDate(LocalDate.parse(instalmentDates.get("instalmentStartDate")));
        mandate.setInstalmentEndDate(LocalDate.parse(instalmentDates.get("instalmentEndDate")));
        mandate.setTenor(Integer.parseInt(instalmentDates.get("tenor")));
        mandate.setDelFlg("N");
        mandate.setTimestamp(LocalDateTime.now());

        nachMandateRepository.save(mandate);

        return Map.of(
                "success", true,
                "message", "Manual NACH mandate created successfully"
        );
    }


}
