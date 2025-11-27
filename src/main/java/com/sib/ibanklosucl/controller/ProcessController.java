package com.sib.ibanklosucl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.service.VehicleLoanProgramService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequestMapping("/process")
@RestController
@Slf4j
@RequiredArgsConstructor
public class ProcessController {
     private boolean devMode;
      private final UserSessionData usd;
    private final CommonUtils cm;
    private final ObjectMapper objectMapper;
    private final VehicleLoanProgramService vlProgramService;
    private final VehicleLoanMasterService masterService;

    @PostMapping("/fetchBSA")
    public String submitBSAFormData(@RequestBody String requestData, HttpServletRequest request) {
        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(requestData, Map.class);
            String txnId = requestMap.get("txnId");
            String institutionId = requestMap.get("institutionId");
            String yearMonthFrom = requestMap.get("yearMonthFrom");
            String yearMonthTo = requestMap.get("yearMonthTo");
            String loanType = "HL";
            String applicantId = requestMap.get("applicantId");
            String wiNum = requestMap.get("wiNum");
            String slno = requestMap.get("slno");
            String statementType = requestMap.get("statementType");

            return vlProgramService.handleBSARequest(txnId, institutionId, yearMonthFrom, yearMonthTo, loanType, applicantId, wiNum, slno, statementType);
        } catch (Exception exception) {
            return "{\"error\":\"An error occurred while processing the request.\"}";
        }
    }
    @PostMapping("/getLatestCompletedBSATransactionId")
    public String getLatestCompletedBSATransactionId(@RequestBody String requestData) {
        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(requestData, Map.class);
            String applicantId = requestMap.get("applicantId");
            String wiNum = requestMap.get("wiNum");
            String statementType = requestMap.get("statementType");
            return vlProgramService.fetchLatestCompletedBSATransactionId(applicantId, wiNum, statementType);
        } catch (Exception e) {
            return "{\"error\":\"An error occurred while processing the request.\"}";
        }
    }

    @PostMapping("/fetchBSAReport")
    public String fetchBSAReport(@RequestBody Map<String, String> requestMap) {
        String perfiosTransactionId = requestMap.get("perfiosTransactionId");
        String applicantId = requestMap.get("applicantId");
        String wiNum = requestMap.get("wiNum");
        return vlProgramService.fetchBSAReport(perfiosTransactionId, applicantId, wiNum);
    }
}
