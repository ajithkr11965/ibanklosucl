package com.sib.ibanklosucl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sib.ibanklosucl.dto.AnnualIncomeAndBankBalance;
import com.sib.ibanklosucl.dto.program.bsaBankDetails;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.VehicleLoanProgramRepository;
import com.sib.ibanklosucl.repository.program.BSADetailsRepository;
import com.sib.ibanklosucl.repository.program.BsaBankDetailsRepository;
import com.sib.ibanklosucl.service.vlsr.FDAccountService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VehicleLoanProgramService {

    @Autowired
    private VehicleLoanProgramRepository repository;
    @Autowired
    private FDAccountService fdAccountService;
    @Autowired
    private BSADetailsRepository bsaDetailsRepository;
    @Autowired
    private BsaBankDetailsRepository bsaBankDetailsRepository;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private VehicleLoanApplicantService applicantService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${api.integrator}")
    private String integratorEndpoint;
    @Value("${esb.MerchantName}")
    private String merchantName;
    @Value("${esb.MerchantCode}")
    private String merchantCode;
    @Value("${app.dev-mode:true}")
    private boolean devMode;
    @Value("${esb.ChannelID}")
    private String channelID;
    @Value("${bsa.redirectUrl}")
    private String bsaRedirectUrl;
    private Map<String, String> bankDetailsCache = null;

    @Transactional
    public VehicleLoanProgram insertVehicleLoanProgram(VehicleLoanProgram vehicleLoanProgram) {
        vehicleLoanProgram = repository.save(vehicleLoanProgram);
        fdAccountService.updateVehicleLoanFDWithProgram(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram);
        return vehicleLoanProgram;
    }

    @Transactional
    public VehicleLoanProgram updateVehicleLoanProgram(String ApplicantID, VehicleLoanProgram updatedProgram) {
        Optional<VehicleLoanProgram> optionalProgram = repository.findByApplicantIdAndDelFlg(Long.parseLong(ApplicantID), "N");
        if (optionalProgram.isPresent()) {
            VehicleLoanProgram existingProgram = optionalProgram.get();
            // Copy properties from updatedProgram to existingProgram
            existingProgram.setApplicantId(updatedProgram.getApplicantId());
            existingProgram.setReqIpAddr(updatedProgram.getReqIpAddr());
            existingProgram.setIncomeConsidered(updatedProgram.getIncomeConsidered());
            existingProgram.setLoanProgram(updatedProgram.getLoanProgram());
            existingProgram.setDoctype(updatedProgram.getDoctype());
            existingProgram.setDob(updatedProgram.getDob());
            existingProgram.setPan(updatedProgram.getPan());
            existingProgram.setSalSlipMonths(updatedProgram.getSalSlipMonths());
            existingProgram.setAcctStmtMonths(updatedProgram.getAcctStmtMonths());
            existingProgram.setItrMonths(updatedProgram.getItrMonths());
            existingProgram.setNumSalSlipFiles(updatedProgram.getNumSalSlipFiles());
            existingProgram.setNumAcctStmtFiles(updatedProgram.getNumAcctStmtFiles());
            existingProgram.setNumItrFiles(updatedProgram.getNumItrFiles());
            existingProgram.setResidentType(updatedProgram.getResidentType());
            existingProgram.setItrFlg(updatedProgram.getItrFlg());
            existingProgram.setForm16Flg(updatedProgram.getForm16Flg());
            existingProgram.setAvgSal(updatedProgram.getAvgSal());
            existingProgram.setAbb(updatedProgram.getAbb());
            existingProgram.setSanctionDate(updatedProgram.getSanctionDate());
            existingProgram.setDepAmt(updatedProgram.getDepAmt());
            existingProgram.setCmUser(updatedProgram.getCmUser());
            existingProgram.setCmDate(updatedProgram.getCmDate());
            existingProgram.setDelFlg(updatedProgram.getDelFlg());
            existingProgram.setHomeSol(updatedProgram.getHomeSol());
            existingProgram.setVehicleLoanFDList(updatedProgram.getVehicleLoanFDList());
            repository.save(existingProgram);
            fdAccountService.updateVehicleLoanFDWithProgram(updatedProgram.getApplicantId(), updatedProgram);
            return existingProgram;
        } else {
            throw new EntityNotFoundException("Vehicle Loan Program not found");
        }
    }

    @Transactional(readOnly = true)
    public List<VehicleLoanProgram> getVehicleLoanProgram(String wiNum, Long slNo) {
        return repository.findByWiNumAndSlNoAndDelFlg(wiNum, slNo, "N");
    }

    public BigDecimal getSumOfAbbWhereDelFlgIsN(String wiNum, Long slNo) {
        Optional<BigDecimal> amt = repository.findSumOfAbbWhereDelFlgIsN(wiNum, slNo);
        return amt.orElse(BigDecimal.ZERO);
    }

    public BigDecimal getSumOfDepAmtWhereDelFlgIsN(String wiNum, Long slNo) {
        Optional<BigDecimal> amt = repository.findSumOfDepAmtWhereDelFlgIsN(wiNum, slNo);
        return amt.orElse(BigDecimal.ZERO);
    }

    public VehicleLoanProgram findVehicleLoanProgrambyAppID(Long ApplicantID) {
        return repository.findByApplicantIdAndDelFlg(ApplicantID, "N")
                .orElseThrow(() -> new EntityNotFoundException("Vehicle Loan Program not found for ApplicantID: " + ApplicantID));

    }

    public Optional<VehicleLoanProgram> findVehicleLoanProgrambyApplicantId(Long ApplicantID) {
        return repository.findByApplicantIdAndDelFlg(ApplicantID, "N");
    }

    public boolean validateProgram(String winum, Long slno) {
        List<VehicleLoanProgram> vps = getVehicleLoanProgram(winum, slno);
        vps = vps.stream().filter(program -> !"NONE".equals(program.getLoanProgram())).collect(Collectors.toList());
        return vps.stream().map(VehicleLoanProgram::getLoanProgram).distinct().count() == 1;
    }

    public String getProgramName(String winum, Long slno) {
        List<VehicleLoanProgram> vps = getVehicleLoanProgram(winum, slno);
        vps = vps.stream().filter(program -> !"NONE".equals(program.getLoanProgram())).collect(Collectors.toList());
        return vps.stream().map(VehicleLoanProgram::getLoanProgram).distinct().findFirst().orElse(null);
    }

    public AnnualIncomeAndBankBalance getAnnualIncomeAndBankBalance(Long slNo) {
        return repository.findAnnualIncomeAndBankBalance(slNo);
    }

    public Map<String, Object> getProgramDetails(VehicleLoanProgram program, Long slNo) {
        if (program == null) {
            return null;
        }

        Map<String, Object> details = new HashMap<>();
        details.put("loanProgram", program.getLoanProgram());
        details.put("incomeConsidered", program.getIncomeConsidered());
        details.put("doctype", program.getDoctype());
        details.put("residentialStatus", program.getResidentType());


        switch (program.getLoanProgram()) {
            case "INCOME":
                details.put("itrFlg", program.getItrFlg());
                details.put("form16Flg", program.getForm16Flg());
                details.put("vehicleLoanProgramNRIList", program.getVehicleLoanProgramNRIList());
                details.put("vehicleLoanITRList", program.getVehicleLoanITRList());
                //details.put("vehicleLoanProgramSalaryList", program.getVehicleLoanProgramSalaryList());
                details.put("avgSal", program.getAvgSal());
                details.put("docType", program.getDoctype());
                // Format payslip data for resident applicants
                if ("R".equals(program.getResidentType()) && "PAYSLIP".equals(program.getDoctype())) {
                    if (program.getVehicleLoanProgramSalaryList() != null && !program.getVehicleLoanProgramSalaryList().isEmpty()) {
                        List<Map<String, Object>> payslipDetails = new ArrayList<>();
                        for (VehicleLoanProgramSalary salary : program.getVehicleLoanProgramSalaryList()) {
                            // Only include non-deleted records
                            if ("N".equals(salary.getDelFlg())) {
                                Map<String, Object> payslipData = new HashMap<>();
                                payslipData.put("payslipId", salary.getIno());
                                payslipData.put("salMonth", salary.getSalMonth());
                                payslipData.put("salYear", salary.getSalYear());
                                payslipData.put("salAmount", salary.getSalAmount());
                                payslipData.put("salGrossAmount", salary.getSalGrossAmount());
                                payslipData.put("salaryDoc", salary.getSalaryDoc());
                                payslipData.put("fileUploaded", salary.getSalaryDoc() != null && !salary.getSalaryDoc().isEmpty());
                                payslipDetails.add(payslipData);
                            }
                        }
                        details.put("payslipDetails", payslipDetails);
                    }
                }

                if ("N".equals(program.getResidentType())) {
                    Map<String, Object> nriRemittanceDetails = new HashMap<>();

                    // Add monthly salary and averages
                    nriRemittanceDetails.put("monthlySalary", program.getNriNetSalary());
                    nriRemittanceDetails.put("avgTotalRemittance", program.getAvgTotalRemittance());
                    nriRemittanceDetails.put("avgBulkRemittance", program.getAvgBulkRemittance());
                    nriRemittanceDetails.put("avgNetRemittance", program.getAvgNetRemittance());

                    // Format remittance months data for JavaScript
                    if (program.getVehicleLoanProgramNRIList() != null && !program.getVehicleLoanProgramNRIList().isEmpty()) {
                        List<Map<String, Object>> remittanceMonths = new ArrayList<>();
                        for (VehicleLoanProgramNRI nri : program.getVehicleLoanProgramNRIList()) {
                            if ("N".equals(nri.getDelFlg())) {
                                Map<String, Object> monthData = new HashMap<>();
                                // Format as "yyyy-MM" to match JSP format
                                monthData.put("monthYear", String.format("%d-%02d", nri.getRemitYear(), nri.getRemitMonth()));
                                monthData.put("totalRemittance", nri.getTotRemittance());
                                monthData.put("bulkRemittance", nri.getBulkRemittance());
                                monthData.put("netRemittance", nri.getNetRemittance());
                                remittanceMonths.add(monthData);
                            }
                        }
                        nriRemittanceDetails.put("remittanceMonths", remittanceMonths);
                    }

                    details.put("nriRemittanceDetails", nriRemittanceDetails);
                }
                break;
            case "SURROGATE":
                details.put("vehicleLoanBSAList", program.getVehicleLoanBSAList());
                details.put("abb", program.getAbb());
                break;
            case "70/30":
                // Add 70/30 specific details
                break;
            case "LOANFD":
                Map<String, Object> fdAccountResponses = fdAccountService.getAccountDetails(program.getApplicantId(), program.getWiNum(), slNo);
                details.put("vehicleLoanFDList", fdAccountResponses);
                details.put("depAmt", program.getDepAmt());
                break;
        }

        return details;
    }

    public Optional<VehicleLoanProgram> findProgrambyApplicantId(Long ApplicantID) {
        return repository.findByApplicantIdAndDelFlg(ApplicantID, "N");
    }

    public int markNullProgramInoRecordsAsDeleted(Long applicantId, String wiNum) {
        List<String> statementTypes = Arrays.asList("SURROGATE-1", "SURROGATE-2", "SURROGATE-3");
        List<VehicleLoanBSA> recordsWithNullProgramIno = bsaDetailsRepository.findRecordsWithNullProgramIno(
                applicantId,
                wiNum,
                "N", // Only consider records that are not already deleted
                statementTypes
        );

        int updatedCount = 0;

        if (!recordsWithNullProgramIno.isEmpty()) {
            for (VehicleLoanBSA record : recordsWithNullProgramIno) {
                record.setDelFlg("Y");
                record.setDelDate(new Date());
                record.setDelUser(usd.getPPCNo());
                bsaDetailsRepository.save(record);
                updatedCount++;
            }
        }

        return updatedCount;
    }

    @Transactional
    public String handleBSARequest(String txnId, String institutionId, String yearMonthFrom, String yearMonthTo, String loanType, String applicantId, String wiNum, String slno, String statementType) {
        log.info("Handling BSA request for applicant ID: {}", applicantId);
        String uuidVal = wiNum.toLowerCase() + "-" + applicantId;
        if (institutionId == null || yearMonthFrom == null || yearMonthTo == null || loanType == null || applicantId == null || wiNum == null || slno == null) {
            throw new IllegalArgumentException("All parameters are required for BSA request");
        }
        try {
            txnId = generateUniqueIdentifier(wiNum, applicantId);
            String apiResponse = fetchBSAGenLink(txnId, institutionId, yearMonthFrom, yearMonthTo, loanType, "bsaGenLink", applicantId, wiNum, statementType);
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
            bsaDetails.setStatementType(statementType);
            bsaDetailsRepository.save(bsaDetails);
            log.info("BSA request handled successfully for applicant ID: {}", applicantId);
            return apiResponse;
        } catch (Exception e) {
            log.error("Error occured while processing BSA request for applicant ID: {}", applicantId);
            throw new RuntimeException("An error occured while processing the BSA request", e);
        }

    }

    public String fetchLatestCompletedBSATransactionId(String applicantId, String wiNum, String statementType) {
        log.info("Fetching latest completed BSA transaction ID for applicant ID: {}", applicantId);

        // First, try to find the latest record with null perfiosTransactionId
        List<VehicleLoanBSA> latestIncompleteRecords = bsaDetailsRepository.findByApplicantIdAndWiNumAndPerfiosTransactionIdIsNullAndDelFlgAndStatementTypeOrderByTimestampDesc(
                Long.valueOf(applicantId), wiNum, "N", statementType);

        String latestCompletedTransactionId = null;
        VehicleLoanBSA completedBsa = null;

        if (!latestIncompleteRecords.isEmpty()) {
            // Process the latest incomplete record
            VehicleLoanBSA latestIncompleteRecord = latestIncompleteRecords.get(0);
            String response = checkBSATxnStatus(latestIncompleteRecord.getTxnId(), applicantId, wiNum);
            updateBSAWithResponse(response, applicantId, wiNum);
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
            VehicleLoanBSA latestCompletedBsa = bsaDetailsRepository.findTopByApplicantIdAndWiNumAndPerfiosTransactionIdIsNotNullAndDelFlgAndStatementTypeOrderByTimestampDesc(
                    Long.valueOf(applicantId), wiNum, "N", statementType);
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
            List<VehicleLoanBSA> allRecords = bsaDetailsRepository.findByApplicantIdAndWiNumAndDelFlgAndStatementType(Long.valueOf(applicantId), wiNum, "N", statementType);
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
            VehicleLoanBSA vlBSA = bsaDetailsRepository.findTopByApplicantIdAndWiNumAndDelFlgOrderByTimestampDesc(
                    Long.valueOf(applicantId), wiNum, "N").orElseThrow(() -> new RuntimeException("No BSA found for applicant ID: " + applicantId + " and wiNum: " + wiNum));

            // Update the VehicleLoanBSA record
            vlBSA.setPerfiosTransactionId(perfiosTransactionId);
            vlBSA.setPerfiosStatus(status);
            vlBSA.setMessage(reason);
            vlBSA.setErrorCode(errorCode);
            vlBSA.setUpdated(new Date());
            bsaDetailsRepository.save(vlBSA);
            log.info("Successfully updated VehicleLoanBSA for applicant ID: {}", applicantId);
        } catch (JsonProcessingException e) {
            log.error("Error parsing status response for applicant ID: {} and wiNum: {}", applicantId, wiNum, e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while updating VehicleLoanBSA for applicant ID: {}", applicantId, e);
        }
    }

    public String checkBSATxnStatus(String generateLinkId, String applicantId, String wiNum) {
        log.info("Checking BSA transaction status for applicant ID: {}", applicantId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        request.put("merchantCode", merchantCode);
        request.put("merchantName", merchantName);
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

    private String fetchBSAGenLink(String txnId, String institutionId, String yearMonthFrom, String yearMonthTo, String loanType, String apiName, String applicantId, String wiNum, String statementType) {
        log.info("Fetching BSA request for applicant ID: {}", applicantId);
        String uuidVal = wiNum.toLowerCase() + "-" + applicantId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        request.put("merchantCode", merchantCode);
        request.put("merchantName", merchantName);
        request.put("UUID", txnId);
        request.put("service", "initiateTransaction");
        request.put("version", "2");
        request.put("txnId", txnId);
        request.put("loanAmount", "1");
        request.put("loanDuration", "24");
        request.put("loanType", loanType);
        request.put("institutionId", institutionId);
        request.put("processingType", "statement");
        request.put("transactionCompleteCallbackUrl", "https://onlineuat.southindianbank.com/ibanklosext/callback");
        request.put("redirectUrl", bsaRedirectUrl);
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

    private String checkBSAProcessed(Long applicantId, String statementType, String wiNum) {
        log.info("Checking if BSA is processed for applicant ID: {}, statementType: {}", applicantId, statementType);

        VehicleLoanBSA bsa = bsaDetailsRepository.findPerfiosStatementCompleted(applicantId, wiNum, statementType, "N");

        if (bsa != null && bsa.getPerfiosStatus() != null && bsa.getPerfiosStatus().equalsIgnoreCase("success")) {
            log.info("BSA is processed for applicant ID: {}, statementType: {}", applicantId, statementType);
            return "Y";
        } else {
            log.info("BSA is not processed for applicant ID: {}, statementType: {}", applicantId, statementType);
            return "N";
        }
    }
    public String fetchBSAReport(String perfiosTransactionId, String applicantId, String wiNum) {
        log.info("Fetching BSA report for applicant ID: {}", applicantId);
        VehicleLoanApplicant applicant = applicantService.getById(Long.valueOf(applicantId));
        VehicleLoanBSA bsa = bsaDetailsRepository.findByApplicantIdAndWiNumAndPerfiosTransactionId(Long.valueOf(applicantId), wiNum, perfiosTransactionId);
        if (bsa == null) {
            throw new RuntimeException("BSA not found for applicant ID: " + applicantId);
        }
        String bpmFolderName = applicant.getBpmFolderName();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        request.put("merchantCode", merchantCode);
        request.put("merchantName", merchantName);
        request.put("UUID", UUID.randomUUID().toString());
        request.put("perfiosTransactionId", perfiosTransactionId);
        request.put("service", "reportRetrive");
        request.put("version", "3");
        request.put("reportType", "xml,xlsx");
        requestBody.put("request", request);
        requestBody.put("workItemNumber", wiNum);
        requestBody.put("bpmFolderName", bpmFolderName);
        requestBody.put("origin", applicantId);
        requestBody.put("docName", bsa.getStatementType());
        requestBody.put("mock", false);
        requestBody.put("apiName", "bsaReportFetch");
        HttpEntity<String> entity = new HttpEntity<>(convertToJson(requestBody), headers);
        try {
            String response = restTemplate.postForObject(integratorEndpoint, entity, String.class);
            bsa.setFetchResponse(response);
            bsa.setCmUser(usd.getPPCNo());
            bsa.setCmDate(new Date());
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode accountInfo = rootNode.path("accountInfo");
            JsonNode bsaDataNode = rootNode.path("bsaData");
            if (!accountInfo.isMissingNode()) {
                bsa.setAccountType(accountInfo.path("accountType").asText(""));
                bsa.setAccountNo(accountInfo.path("accountNo").asText(""));
                if (accountInfo.has("fullMonthCount")) {
                    bsa.setFullMonthCount(accountInfo.path("fullMonthCount").asText(""));
                }
                if (accountInfo.has("totChqIssues")) {
                    bsa.setTotChqIssues(accountInfo.path("totChqIssues").asText(""));
                }
            } else {
                log.warn("account ino not found in the response");
                bsa.setAccountType("");
                bsa.setAccountNo("");
            }
            if (!bsaDataNode.isMissingNode()) {
                List<String> surrogateStatementPrefixes = Arrays.asList("SURROGATE-1", "SURROGATE-2", "SURROGATE-3");
                int totalInwBounces = bsaDataNode.path("totalInwBounces").asInt(0);
                int totalOutwBounces = bsaDataNode.path("totalOutwBounces").asInt(0);
                BigDecimal averageBankBalance = BigDecimal.ZERO;
                if (surrogateStatementPrefixes.contains(bsa.getStatementType())) {
                    averageBankBalance = calculateAverageBankBalance(Long.valueOf(applicantId), wiNum);
                } else {
                    averageBankBalance = new BigDecimal(bsaDataNode.path("averageBankBalance").asText("0"));
                }
                bsa.setTotalInwBounces(totalInwBounces);
                bsa.setTotalOutwBounces(totalOutwBounces);
                bsa.setAvgBankBalance(averageBankBalance);
                //Update the response with calculated ABB
                ((ObjectNode) bsaDataNode).put("averageBankBalance", averageBankBalance.doubleValue());
            } else {
                log.warn("bsaData not found in the response");
                bsa.setTotalInwBounces(0);
                bsa.setTotalOutwBounces(0);
            }
            response = objectMapper.writeValueAsString(rootNode);
            bsaDetailsRepository.save(bsa);
            log.info("Successfully fetched BSA report for applicant ID: {}", applicantId);
            return response;
        } catch (Exception e) {
            log.error("Error occurred while fetching BSA report for applicant ID: {}", applicantId, e);
            throw new RuntimeException("Failed to fetch BSA report", e);
        }
    }

    @Transactional
    public void updateVLBSAWithProgram(Long applicantId, VehicleLoanProgram vlProgram) {
        log.info("Updating BSA with program for applicant ID: {}, Program: {}", applicantId, vlProgram.getLoanProgram());

        // Find all BSA records for this applicant and work item
        List<VehicleLoanBSA> bsaList = bsaDetailsRepository.findByApplicantIdAndWiNum(applicantId, vlProgram.getWiNum());

        if (bsaList.isEmpty()) {
            log.warn("No BSA records found for applicant ID: {}. Skipping update", applicantId);
            return;
        }

        // Determine statement types to keep based on program
        Set<String> allowedStatementTypes = determineAllowedStatementTypes(vlProgram);

        for (VehicleLoanBSA bsa : bsaList) {
            // If the statement type is in allowed types, link the program
            if (allowedStatementTypes.contains(bsa.getStatementType())) {
                bsa.setVlbsa(vlProgram);
                log.info("Linking BSA record with statement type {} to program", bsa.getStatementType());
            } else {
                // Mark other statement types as deleted
                bsa.setDelFlg("Y");
                bsa.setDelUser(usd.getPPCNo());
                bsa.setDelDate(new Date());
                log.info("Marking BSA record with statement type {} as deleted", bsa.getStatementType());
            }
        }

        bsaDetailsRepository.saveAll(bsaList);
        log.info("Successfully updated BSA with program for applicant ID: {}", applicantId);
    }

    private Set<String> determineAllowedStatementTypes(VehicleLoanProgram program) {
        Set<String> allowedTypes = new HashSet<>();

        switch (program.getLoanProgram()) {
            case "SURROGATE":
                allowedTypes.add("SURROGATE-1");
                allowedTypes.add("SURROGATE-2");
                allowedTypes.add("SURROGATE-3");
                break;
            case "NONFOIR":
                allowedTypes.add("BSA-NONFOIR");
                break;
            default:
                log.warn("Unhandled program type: {}", program.getLoanProgram());
        }

        return allowedTypes;
    }

    private Map<String, Object> getBSAData(Long applicantId, String wiNum, String statementType) {
        log.info("Retrieving BSA data for applicant ID: {}, statementType: {}", applicantId, statementType);

        VehicleLoanBSA bsa = bsaDetailsRepository.findPerfiosStatementCompleted(applicantId, wiNum, statementType, "N");

        if (bsa == null) {
            log.warn("No BSA data found for applicant ID: {}, statementType: {}", applicantId, statementType);
            return null;
        }

        Map<String, Object> bsaData = new HashMap<>();
        bsaData.put("startDate", bsa.getStartDate());
        bsaData.put("endDate", bsa.getEndDate());
        bsaData.put("bank", bsa.getBank());
        bsaData.put("accountNo", bsa.getAccountNo());
        bsaData.put("accountType", bsa.getAccountType());
        bsaData.put("avgBankBalance", bsa.getAvgBankBalance());
        bsaData.put("emiData", bsa.getEmiResponse());
        bsaData.put("monthlyData", bsa.getFetchResponse());
        bsaData.put("bankNameDesc", getBankNameDescription(bsa.getBank()));

        log.info("Successfully retrieved BSA data for applicant ID: {}, statementType: {}", applicantId, statementType);
        return bsaData;
    }

@Transactional
    public void calculateAndSetABBAmountForSurrogateProgram(VehicleLoanProgram program) {
        // Validate program type is SURROGATE
        if (!"SURROGATE".equals(program.getLoanProgram())) {
            log.warn("ABB calculation attempted for non-SURROGATE program");
            return;
        }

        try {
            // Find BSA records specific to Surrogate statement types, as only this needs this calculation. Only max 3 statements allowed.
            List<String> surrogatePrefixes = Arrays.asList("SURROGATE-1", "SURROGATE-2", "SURROGATE-3");
            List<VehicleLoanBSA> bsaRecords = bsaDetailsRepository.findByApplicantIdAndWiNumAndDelFlgAndStatementTypeIn(
                    program.getApplicantId(),
                    program.getWiNum(),
                    "N",
                    surrogatePrefixes
            );

            // Validate records exist
            if (bsaRecords.isEmpty()) {
                log.warn("No SURROGATE BSA records found for applicant ID {} and work item {} with statement types {}",
                        program.getApplicantId(), program.getWiNum(), surrogatePrefixes);
                return;
            }

            // Map to store month-wise summed ABB
            Map<String, BigDecimal> monthABBSumMap = new HashMap<>();

            // Process each BSA record
            for (VehicleLoanBSA bsa : bsaRecords) {
                // Log the statement type being processed
                log.info("Processing `BSA record with statement type: {}", bsa.getStatementType());
                // Parse the fetch response
                JsonNode rootNode = objectMapper.readTree(bsa.getFetchResponse());
                JsonNode monthlyDetailsNode = rootNode.path("monthlyDetails");
                // Process each monthly detail
                for (JsonNode monthDetail : monthlyDetailsNode) {
                    String month = monthDetail.path("month").asText();
                    BigDecimal calculatedABB = new BigDecimal(monthDetail.path("calculatedABB").asText("0"));

                    // Sum ABB for each month, handling overlaps
                    monthABBSumMap.merge(month, calculatedABB, BigDecimal::add);
                }
            }
            // Validate we have at least 12 months of data
            if (monthABBSumMap.size() < 12) {
                log.warn("Insufficient months of data. Found only {} months", monthABBSumMap.size());
                return;
            }
            // Sort month ABB values
            List<BigDecimal> sortedABBValues = new ArrayList<>(monthABBSumMap.values());
            Collections.sort(sortedABBValues);

            // Remove highest and lowest values
            sortedABBValues.remove(0);  // Remove the mnth with lowest balance
            sortedABBValues.remove(sortedABBValues.size() - 1);  // Remove the month with  highest balance
            // Calculate total of remaining 10 months
            BigDecimal totalABB = sortedABBValues.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            // Divide by 10 and round
            BigDecimal abbAmount = totalABB.divide(BigDecimal.valueOf(10), 2, RoundingMode.HALF_UP);
            // Log detailed calculation for audit
            logABBCalculationDetails(monthABBSumMap, totalABB, abbAmount);
            // Set the ABB amount in HLProgram
            program.setAbb(abbAmount);
            repository.save(program);

            log.info("Successfully calculated ABB amount for applicant ID {} and work item {}: {}",
                    program.getApplicantId(), program.getWiNum(), abbAmount);

        } catch (Exception e) {
            log.error("Error calculating ABB amount for applicant ID {} and work item {}",
                    program.getApplicantId(), program.getWiNum(), e);
            throw new RuntimeException("Failed to calculate ABB amount", e);
        }
    }



    private String generateUniqueIdentifier(String wiNum, String applicantId) {
        String baseIdentifier = wiNum.toLowerCase() + "-" + applicantId;
        LocalDateTime now = LocalDateTime.now();
        String timeComponent = String.format("%02d%02d%02d", now.getDayOfMonth(), now.getHour(), now.getMinute());
        return baseIdentifier + "-" + timeComponent;
    }

    private String convertToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Error converting object to JSON", e);
            throw new RuntimeException("Error converting to json", e);
        }
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return sdf.format(date);
    }

    private String getBankNameDescription(String bankId) {
        if (bankId == null || bankId.trim().isEmpty()) {
            return "";
        }

        try {
            if (bankDetailsCache == null) {
                initializeBankDetailsCache();
            }

            return bankDetailsCache.getOrDefault(bankId, "");
        } catch (Exception e) {
            log.error("Error retrieving bank name for bank ID: {}", bankId, e);
            return "";
        }
    }

    private void initializeBankDetailsCache() {
        try {
            List<bsaBankDetails> bankDetailsList = bsaBankDetailsRepository.getBankDetails();
            bankDetailsCache = bankDetailsList.stream()
                    .collect(Collectors.toMap(
                            bsaBankDetails::getInstitutionId,
                            bsaBankDetails::getName,
                            (existing, replacement) -> existing // In case of duplicates, keep existing
                    ));
            log.info("Initialized bank details cache with {} entries", bankDetailsCache.size());
        } catch (Exception e) {
            log.error("Error initializing bank details cache", e);
            bankDetailsCache = new HashMap<>(); // Initialize empty cache to avoid null pointer
        }
    }
    public BigDecimal calculateAverageBankBalance(Long applicantId, String wiNum) {
        try {
            // Find BSA records specific to all statement types
            List<String> statementPrefixes = Arrays.asList("SURROGATE-1", "SURROGATE-2", "SURROGATE-3");
            List<VehicleLoanBSA> bsaRecords = bsaDetailsRepository.findByApplicantIdAndWiNumAndDelFlgAndStatementTypeIn(
                    applicantId,
                    wiNum,
                    "N",
                    statementPrefixes
            );

            // Validate records exist
            if (bsaRecords.isEmpty()) {
                log.warn("No BSA records found for applicant ID {} and work item {} with statement types {}",
                        applicantId, wiNum, statementPrefixes);
                return BigDecimal.ZERO;
            }

            // Map to store month-wise summed ABB
            Map<String, BigDecimal> monthABBSumMap = new HashMap<>();

            // Process each BSA record
            for (VehicleLoanBSA bsa : bsaRecords) {
                // Log the statement type being processed
                log.info("Processing BSA record with statement type: {}", bsa.getStatementType());

                try {
                    // the data from the BSA reponse is parsed here for getting the monthly details
                    JsonNode rootNode = objectMapper.readTree(bsa.getFetchResponse());
                    JsonNode monthlyDetailsNode = rootNode.path("monthlyDetails");

                    // Month wise ABB is proceeded here.
                    for (JsonNode monthDetail : monthlyDetailsNode) {
                        String month = monthDetail.path("month").asText();
                        BigDecimal calculatedABB = new BigDecimal(monthDetail.path("calculatedABB").asText("0"));

                        // Sum ABB for each month will be taken and the overlap will be handled.
                        monthABBSumMap.merge(month, calculatedABB, BigDecimal::add);
                    }
                } catch (Exception e) {
                    log.error("Error processing BSA record for statement type {}: {}",
                            bsa.getStatementType(), e.getMessage());
                    // Continue with other records
                }
            }

            // If no valid month data found
            if (monthABBSumMap.isEmpty()) {
                log.warn("No valid monthly ABB data found for applicant ID: {}", applicantId);
                return BigDecimal.ZERO;
            }

            // Check if we have all 12 months of data
            if (monthABBSumMap.size() >= 12) {
                // Get all ABB values
                List<BigDecimal> abbValues = new ArrayList<>(monthABBSumMap.values());

                // Sort ABB values
                Collections.sort(abbValues);

                // Remove highest and lowest values
                abbValues.remove(0);  // Remove lowest
                abbValues.remove(abbValues.size() - 1);  // Remove highest

                // Calculate total of remaining values
                BigDecimal totalABB = abbValues.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Calculate average (divide by 10 for the remaining months)
                BigDecimal result = totalABB.divide(BigDecimal.valueOf(10), 2, RoundingMode.HALF_UP);

                log.info("Calculated ABB using trimmed mean (12+ months) for applicant ID {}: {}",
                        applicantId, result);
                return result;
            } else {
                // Simple average for less than 12 months
                BigDecimal totalABB = monthABBSumMap.values().stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Calculate average
                BigDecimal result = totalABB.divide(BigDecimal.valueOf(monthABBSumMap.size()), 2, RoundingMode.HALF_UP);

                log.info("Calculated ABB using simple average ({} months) for applicant ID {}: {}",
                        monthABBSumMap.size(), applicantId, result);
                return result;
            }

        } catch (Exception e) {
            log.error("Error calculating average bank balance for applicant ID: {}, wiNum: {}",
                    applicantId, wiNum, e);
            return BigDecimal.ZERO;
        }
    }
    public Map<String, Object> getABBCalculationDetails(Long applicantId, String wiNum) {
        try {
            List<String> statementPrefixes = Arrays.asList("SURROGATE-1", "SURROGATE-2", "SURROGATE-3");
            List<VehicleLoanBSA> bsaRecords = bsaDetailsRepository.findByApplicantIdAndWiNumAndDelFlgAndStatementTypeIn(
                    applicantId, wiNum, "N", statementPrefixes
            );

            Map<String, Object> response = new HashMap<>();

            if (bsaRecords.isEmpty()) {
                response.put("success", false);
                response.put("message", "No  records found");
                return response;
            }

            // Map to store month-wise data for each bank
            Map<String, Map<String, BigDecimal>> bankWiseMonthlyData = new HashMap<>();
            Map<String, BigDecimal> monthABBSumMap = new HashMap<>();
            Map<String, String> bankNames = new HashMap<>();

            // Process each BSA record
            for (VehicleLoanBSA bsa : bsaRecords) {
                try {
                    JsonNode rootNode = objectMapper.readTree(bsa.getFetchResponse());
                    JsonNode monthlyDetailsNode = rootNode.path("monthlyDetails");
                    JsonNode accountInfo = rootNode.path("accountInfo");

                    String bankName = accountInfo.path("instName").asText("Unknown Bank");
                    bankNames.put(bsa.getStatementType(), bankName);

                    Map<String, BigDecimal> monthlyData = new HashMap<>();

                    for (JsonNode monthDetail : monthlyDetailsNode) {
                        String month = monthDetail.path("month").asText();
                        BigDecimal calculatedABB = new BigDecimal(monthDetail.path("calculatedABB").asText("0"));

                        monthlyData.put(month, calculatedABB);
                        monthABBSumMap.merge(month, calculatedABB, BigDecimal::add);
                    }

                    bankWiseMonthlyData.put(bsa.getStatementType(), monthlyData);

                } catch (Exception e) {
                    log.error("Error processing BSA record: {}", e.getMessage());
                }
            }

            // Create calculation breakdown
            List<Map<String, Object>> calculationBreakdown = new ArrayList<>();

            for (String month : monthABBSumMap.keySet()) {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", month);

                BigDecimal surrogate1 = bankWiseMonthlyData.getOrDefault("SURROGATE-1", new HashMap<>()).getOrDefault(month, BigDecimal.ZERO);
                BigDecimal surrogate2 = bankWiseMonthlyData.getOrDefault("SURROGATE-2", new HashMap<>()).getOrDefault(month, BigDecimal.ZERO);
                BigDecimal surrogate3 = bankWiseMonthlyData.getOrDefault("SURROGATE-3", new HashMap<>()).getOrDefault(month, BigDecimal.ZERO);
                BigDecimal combinedTotal = monthABBSumMap.get(month);

                monthData.put("surrogate1", surrogate1);
                monthData.put("surrogate2", surrogate2);
                monthData.put("surrogate3", surrogate3);
                monthData.put("combinedTotal", combinedTotal);

                calculationBreakdown.add(monthData);
            }

            // Sort by combined total for ranking
            calculationBreakdown.sort((a, b) ->
                    ((BigDecimal) a.get("combinedTotal")).compareTo((BigDecimal) b.get("combinedTotal"))
            );

            // Add ranking and trimmed status
            for (int i = 0; i < calculationBreakdown.size(); i++) {
                Map<String, Object> monthData = calculationBreakdown.get(i);
                monthData.put("rank", i + 1);

                // Mark highest and lowest for trimming (if 12+ months)
                if (calculationBreakdown.size() >= 12) {
                    if (i == 0) {
                        monthData.put("trimmedStatus", "REMOVED (Lowest)");
                        monthData.put("included", false);
                    } else if (i == calculationBreakdown.size() - 1) {
                        monthData.put("trimmedStatus", "REMOVED (Highest)");
                        monthData.put("included", false);
                    } else {
                        monthData.put("trimmedStatus", "Included");
                        monthData.put("included", true);
                    }
                } else {
                    monthData.put("trimmedStatus", "Included");
                    monthData.put("included", true);
                }
            }

            // Calculate final ABB
            BigDecimal finalABB = calculateAverageBankBalance(applicantId, wiNum);

            // Calculate sum of included months
            BigDecimal sumIncludedMonths = calculationBreakdown.stream()
                    .filter(monthData -> (Boolean) monthData.get("included"))
                    .map(monthData -> (BigDecimal) monthData.get("combinedTotal"))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            response.put("success", true);
            response.put("calculationBreakdown", calculationBreakdown);
            response.put("bankNames", bankNames);
            response.put("finalABB", finalABB);
            response.put("sumIncludedMonths", sumIncludedMonths);
            response.put("totalMonths", calculationBreakdown.size());
            response.put("includedMonths", calculationBreakdown.stream().mapToInt(m -> (Boolean) m.get("included") ? 1 : 0).sum());

            return response;

        } catch (Exception e) {
            log.error("Error getting ABB calculation details for applicant ID: {}, wiNum: {}", applicantId, wiNum, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving calculation details: " + e.getMessage());
            return errorResponse;
        }
    }
    private void logABBCalculationDetails(
            Map<String, BigDecimal> monthABBSumMap,
            BigDecimal totalABB,
            BigDecimal abbAmount
    ) {
        if (log.isDebugEnabled()) {
            log.debug("ABB Calculation Breakdown:");
            // Sort months by their ABB value for logging
            monthABBSumMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEach(entry ->
                            log.debug("Month: {}, Summed ABB: {}", entry.getKey(), entry.getValue())
                    );
            log.debug("Total ABB Sum: {}", totalABB);
            log.debug("Final ABB Amount (Excluding Highest and Lowest, Divided by 10): {}", abbAmount);
        }
    }

}
