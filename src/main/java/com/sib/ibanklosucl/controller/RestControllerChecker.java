package com.sib.ibanklosucl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.config.menuaccess.RequiresMenuAccess;
import com.sib.ibanklosucl.dto.RBCPCMakerSave;
import com.sib.ibanklosucl.dto.ResponseDTO;
import com.sib.ibanklosucl.dto.experian.ExperianRequest;
import com.sib.ibanklosucl.dto.losintegrator.HunterResponseDTO;
import com.sib.ibanklosucl.dto.losintegrator.blacklist.BlacklistRequest;
import com.sib.ibanklosucl.dto.losintegrator.blacklist.BlacklistResult;
import com.sib.ibanklosucl.dto.losintegrator.blacklist.PartialBlacklistRequest;
import com.sib.ibanklosucl.dto.losintegrator.blacklist.PartialBlacklistResult;
import com.sib.ibanklosucl.dto.losintegrator.cif.VehicleLoanCifRequest;
import com.sib.ibanklosucl.dto.losintegrator.cif.VehicleLoanCifResult;
import com.sib.ibanklosucl.dto.losintegrator.dk.DKScoreResponse;
import com.sib.ibanklosucl.service.*;
import com.sib.ibanklosucl.service.eligibility.EligibilityDetailsService;
import com.sib.ibanklosucl.service.impl.BOGSaveImpl;
import com.sib.ibanklosucl.service.impl.BrCheckerSaveImpl;
import com.sib.ibanklosucl.service.integration.BlacklistService;
import com.sib.ibanklosucl.service.integration.DKScoreService;
import com.sib.ibanklosucl.service.integration.ExperianHunterService;
import com.sib.ibanklosucl.service.integration.VehicleLoanBREService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanLockService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanTatService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api/checker")
@Slf4j
public class RestControllerChecker {
    @Autowired
    private BlacklistService blacklistService;
    @Autowired
    private VehicleLoanApplicantService vehicleLoanApplicantService;
    @Autowired
    private VehicleLoanBREService breService;
    @Autowired
    private ExperianHunterService experianHunterService;
    @Autowired
    private VLCheckerValidationService validationService;
    @Autowired
    private EligibilityDetailsService eligibilityDetailsService;
    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;
    @Autowired
    private VehicleLoanTatService vehicleLoanTatService;
    @Autowired
    private CRTSaveService crtsave;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private VehicleLoanLockService vehicleLoanLockService;

    @Autowired
    private VehicleLoanCifService vehicleLoanCifService;
    @Autowired
    private VehicleLoanWIService vehicleLoanWIService;
    @Autowired
    private DKScoreService dkScoreService;
    @Autowired
    private BrCheckerSaveImpl brCheckerSave;

    @Autowired
    BOGSaveImpl bogSave;

    @PostMapping("/checkBlacklist")
    public BlacklistResult performBlacklistCheck(@RequestBody BlacklistRequest blacklistRequest, HttpServletRequest request) {
        String reqIP = CommonUtils.getClientIp(request);
        return blacklistService.performBlacklistCheck(blacklistRequest, reqIP);
    }
    @PostMapping("/checkPartialBlacklist")
    public PartialBlacklistResult performPartialBlacklistCheck(@RequestBody PartialBlacklistRequest blacklistRequest, HttpServletRequest request) {
        String reqIP = CommonUtils.getClientIp(request);
        if(usd == null || usd.getEmployee() == null){
            return new PartialBlacklistResult("406",null);
        }
        return blacklistService.performPartialBlacklistCheck(blacklistRequest, reqIP);
    }

    @PostMapping("/updateBlacklistOption")
    public ResponseEntity<ResponseDTO> updateVLMasterDetails(@RequestBody String requestData) {
        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(requestData, Map.class);
            String identifier = requestMap.get("identifier");
            String updValue = requestMap.get("updValue");
            String wiNum = requestMap.get("wiNum");
            String slno = requestMap.get("slno");
            ResponseDTO responseDTO = vehicleLoanApplicantService.updateApplicantIntegrationDetails(wiNum, slno, identifier, updValue);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return (ResponseEntity<ResponseDTO>) ResponseEntity.badRequest();
        }

    }

    @PostMapping("/fetchBRE")
    public Map<String, Object> fetchBREDetails(@RequestBody String requestData, HttpServletRequest request) {
        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(requestData, Map.class);
            String wiNum = requestMap.get("wiNum");
            String slno = requestMap.get("slno");
            String reqIpNo = CommonUtils.getClientIp(request);
            return breService.getAmberData(wiNum, slno, reqIpNo);
        } catch (Exception e) {
            log.error("BRE Fetching exception occured", e);
        }
        return null;
    }

    @PostMapping("/experian-hunter")
    public ResponseEntity<HunterResponseDTO> callExperianHunter(@RequestBody String requestData, HttpServletRequest request) {
        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(requestData, Map.class);
            String wiNum = requestMap.get("wiNum");
            String slno = requestMap.get("slno");
            String applicantId = requestMap.get("applicantId");
            String reqIpNo = CommonUtils.getClientIp(request);
            HunterResponseDTO response = experianHunterService.callExperianHunterApi(
                    Long.valueOf(applicantId), wiNum, Long.valueOf(slno));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error calling Experian Hunter API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/runRaceScore")
    public ResponseEntity<String> runRaceScore(@RequestBody String requestData, HttpServletRequest request) {
        try {
            JSONObject reqdata = new JSONObject(requestData);
            JSONObject racescore= new JSONObject();
            racescore.put("score","752");
            racescore.put("color","Green");

            vehicleLoanApplicantService.updateApplicantRaceScore(Long.parseLong(reqdata.getString("applicantId")),racescore.getString("score"));
            return ResponseEntity.ok(racescore.toString());
        } catch (Exception e) {
            log.error("Error calling Race Score API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/saveRaceScoreAmber")
    public ResponseEntity<String> saveRaceScoreAmber(@RequestBody String requestData, HttpServletRequest request) {
        try {
            String resp = crtsave.crtRaceScoreFinalSave(requestData,request);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            log.error("Error calling Race Score API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }


    @PostMapping("/validate")
    public ResponseEntity<?> validateVehicleLoan(@RequestParam Long vehicleLoanMasterId) {
        String validationError = validationService.getValidationErrorMessage(vehicleLoanMasterId);

        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-recommended-amount")
    public ResponseEntity<?> updateRecommendedAmount(@RequestParam String wiNum,
                                                     @RequestParam Long slno,
                                                     @RequestParam BigDecimal recommendedAmount) {
        if (recommendedAmount == null) {
            return ResponseEntity.badRequest().body("Recommended amount is required.");
        }

        return eligibilityDetailsService.updateRecommendedLoanAmount(wiNum, slno, recommendedAmount);
    }

    @PostMapping("/validateandsavecrt")
    public ResponseEntity<String> validateandsavecrt(@RequestParam String wiNum,
                                                     @RequestParam Long slno,
                                                     @RequestParam String remarks,
                                                        @RequestParam String action,
                                                        HttpServletRequest request) {

        String response=crtsave.crtFinalSave(wiNum,slno,remarks,action,request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/process")
    public ResponseEntity<String> processVehicleLoan(@RequestParam Long vehicleLoanMasterId) {
        String validationError = validationService.getValidationErrorMessage(vehicleLoanMasterId);

        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }
        return ResponseEntity.ok("Vehicle loan processed successfully.");
    }

//    @PostMapping("/forward")
//    public ResponseEntity<?> forwardApplication(@RequestParam String action,
//                                                @RequestParam String slno,
//                                                @RequestParam String winum,
//                                                @RequestParam String lockflg,
//                                                @RequestParam String remarks, HttpServletRequest request) {
//        try {
//            List<String> errors = validationService.performFinalChecks(winum, slno,request);
//            if (!errors.isEmpty()) {
//                return ResponseEntity.badRequest().body(errors);
//            }
//            String forwardedQue = vehicleLoanMasterService.forwardApplication(Long.valueOf(slno), winum, remarks);
//            vehicleLoanTatService.updateTat(Long.valueOf(slno), usd.getEmployee().getPpcno(), winum, forwardedQue, request);
//            vehicleLoanLockService.ReleaseLock(Long.valueOf(slno), usd.getPPCNo());
//            return ResponseEntity.ok("Application forwarded successfully.");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error forwarding application: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/reject")
//    public ResponseEntity<?> rejectApplication(@RequestParam Long slno,
//                                               @RequestParam String winum,
//                                               @RequestParam String remarks, HttpServletRequest request) {
//        try {
//            vehicleLoanMasterService.rejectApplication(slno, winum, remarks);
//            vehicleLoanTatService.updateTat(Long.valueOf(slno), usd.getEmployee().getPpcno(), winum, "NIL", request);
//            vehicleLoanLockService.ReleaseLock(Long.valueOf(slno), usd.getPPCNo());
//            return ResponseEntity.ok("Application rejected successfully.");
//        } catch (Exception e) {
//            log.error("Error rejecting application: ", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error rejecting application: " + e.getMessage());
//        }
//    }

    @PostMapping("/sendback")
    public ResponseEntity<?> sendbackApplication(@RequestParam Long slno,
                                                 @RequestParam String winum,
                                                 @RequestParam String remarks, HttpServletRequest request) {
        try {
            vehicleLoanMasterService.sendbackApplication(slno, winum, remarks,request);
            vehicleLoanTatService.updateTat(Long.valueOf(slno), usd.getEmployee().getPpcno(), winum, "BS");
            vehicleLoanLockService.ReleaseLock(Long.valueOf(slno), usd.getPPCNo());
            return ResponseEntity.ok("Application sent back successfully.");
        } catch (Exception e) {
            log.error("Error sending back application: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending back application: " + e.getMessage());
        }
    }


    @PostMapping("/cifCreation")
    @RequiresMenuAccess(menuIds = {"BOGQUEUE"})
    public VehicleLoanCifResult performCifCreation(@RequestBody VehicleLoanCifRequest vehicleLoanCifRequest, HttpServletRequest request) {
        try {
            String reqIP = CommonUtils.getClientIp(request);
            if(usd == null || usd.getEmployee() == null){
                return new VehicleLoanCifResult("406","Session timed out","");
            }
            VehicleLoanCifResult result = new VehicleLoanCifResult();
            if(vehicleLoanCifRequest.getAction().equals("REJECT")){
                return vehicleLoanCifService.rejectVLCifRequest(vehicleLoanCifRequest, reqIP);
            }else if(vehicleLoanCifRequest.getAction().equals("APPROVE")){
                return vehicleLoanCifService.approveVLCifRequest(vehicleLoanCifRequest, reqIP);
            }else {
                return new VehicleLoanCifResult("406","Action is not valid","");
            }
        } catch (Exception e) {
            return  new VehicleLoanCifResult("406",e.getMessage(),"");
        }
    }
    @PostMapping("/dk-score")
    @ResponseBody
    public ResponseEntity<?> runDKScore(@RequestBody ExperianRequest request) {
        try {
            DKScoreResponse response = dkScoreService.runDKScoreForAllApplicants(request.getWorkItemNumber(), request.getSlno());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
           return new ResponseEntity<>(new ResponseDTO("F", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
     @PostMapping("/checker-submit")
    public ResponseEntity<?> forwardApplication(@RequestBody RBCPCMakerSave rbs, HttpServletRequest request) {
        try {
            List<String> errors=new ArrayList<>();
            if("FW".equals(rbs.getDecision())) {
                errors = validationService.performFinalChecks(rbs.getWinum(), String.valueOf(rbs.getSlno()), request);
                if (!errors.isEmpty()) {
                    return ResponseEntity.badRequest().body(errors);
                }
            }
            ResponseDTO response  = brCheckerSave.saveChecker(rbs,request);
            if(!response.getStatus().equalsIgnoreCase("S")){
                errors.add(response.getMsg());
                return ResponseEntity.badRequest().body(errors);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error forwarding application: " + e.getMessage());
        }
    }


    @PostMapping("/wibogassetrej")
    @RequiresMenuAccess(menuIds = {"ACOPN"})
    public ResponseEntity<ResponseDTO> wibogassetrej(@RequestParam String winum,
                                                           @RequestParam Long slno,
                                                           @RequestParam String remarks, HttpServletRequest request) {
        try {
            if(usd == null || usd.getEmployee() == null){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO("F","Session timed out"));
            }

            ResponseDTO response  = bogSave.wiBogAssetRej(winum,slno,remarks, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseDTO("F",e.getMessage()));
        }
    }

    @PostMapping("/wibogassetpd")
    @RequiresMenuAccess(menuIds = {"ACOPN"})
    public ResponseEntity<ResponseDTO> wibogassetPostDisb(@RequestParam String winum,
                                                           @RequestParam Long slno,
                                                           @RequestParam String remarks, HttpServletRequest request) {
        try {
            if(usd == null || usd.getEmployee() == null){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO("F","Session timed out"));
            }

            ResponseDTO response  = bogSave.wibogassetPostDisb(winum,slno,remarks, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseDTO("F",e.getMessage()));
        }
    }
}

