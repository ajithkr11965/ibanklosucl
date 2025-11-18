package com.sib.ibanklosucl.controller;

import com.sib.ibanklosucl.dto.RBCPCCheckerSave;
import com.sib.ibanklosucl.dto.RBCPCMakerSave;
import com.sib.ibanklosucl.dto.ResponseDTO;
import com.sib.ibanklosucl.dto.bre.AmberDeviationUpdateRequest;
import com.sib.ibanklosucl.dto.losintegrator.HunterReviewRequest;
import com.sib.ibanklosucl.model.VehicleLoanFcvCpvCfr;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.service.*;
import com.sib.ibanklosucl.service.impl.RBCPCMakerSaveImpl;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanLockService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanTatService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apicpc")
@Slf4j
public class CPCController {
    @Autowired
    private UserSessionData usd;
    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;
    @Autowired
    private VLSaveServiceFactory vlSaveServiceFactory;
    @Autowired
    private VehicleLoanAmberService vehicleLoanAmberService;
    @Autowired
    private VehicleLoanTatService vehicleLoanTatService;

    @Autowired
    private VehicleLoanFcvCpvCfrService fcvCpvCfrService;
    @Autowired
    private VehicleLoanLockService vehicleLoanLockService;
    @Autowired
    private RMMakerValidationService rmMakerValidationService;
        @Autowired
    private RBCPCMakerSaveImpl rbcpcMakerSave;
        @Autowired
        private FetchRepository fetchRepository;

    @PostMapping("/update-amber-deviations")
    public ResponseEntity<?> updateAmberDeviations(@RequestBody AmberDeviationUpdateRequest request, HttpServletRequest httpServletRequest) {
        try {
            vehicleLoanAmberService.updateAmberDeviations(request);
            return ResponseEntity.ok("Deviations updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating deviations: " + e.getMessage());
        }
    }

    @PostMapping("/rbcCheckerSave")
    public ResponseEntity<ResponseDTO> RBCCSave(@RequestBody @Validated RBCPCCheckerSave rbc, HttpServletRequest request) {
        try {
            VlCommonTabService vl = vlSaveServiceFactory.getTabService("RBCPCCHECKER");
            return ResponseEntity.ok(vl.saveRBCChecker(rbc, request));
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO("F", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/saveHunterReview")
    @ResponseBody
    public ResponseDTO saveHunterReview(@RequestBody HunterReviewRequest request, HttpServletRequest httpRequest) {
        try {
            ResponseDTO response = rbcpcMakerSave.saveHunterApproval(request, httpRequest);
             return new ResponseDTO("S", "Hunter review saved and application moved to RM queue");
        } catch (Exception e) {
            log.error("Error in saveHunterReview", e);
            return new ResponseDTO("F", "Error: " + e.getMessage());
        }
    }


    @PostMapping("/update-checker-deviations")
    public ResponseEntity<?> updateCheckerDeviations(@RequestBody AmberDeviationUpdateRequest request) {
        try {
            vehicleLoanAmberService.updateCheckerDeviations(request);
            return ResponseEntity.ok("Deviations updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating deviations: " + e.getMessage());
        }
    }

    @PostMapping("/deactivate-amber-deviation")
    @ResponseBody
    public Map<String, Object> deactivateAmberDeviation(@RequestBody Map<String, Long> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long amberId = request.get("amberId");
            vehicleLoanAmberService.deactivateAmberDeviation(amberId);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }


    @PostMapping("/fcv-cpv-cfr-save")
    public ResponseEntity<?> saveFcvCpvCfrDetails(
            @RequestParam String fcvStatus,
            @RequestParam String cpvStatus,
            @RequestParam String cfrFound,
            @RequestParam String wiNum,
            @RequestParam Long slno,
            @RequestParam(value = "fileUploadFcv", required = false) MultipartFile fcvFile,
            @RequestParam(value = "fileUploadCpv", required = false) MultipartFile cpvFile,
            @RequestParam(value = "fileUploadCfr", required = false) MultipartFile cfrFile,
            HttpServletRequest request) {
        try {
            VehicleLoanFcvCpvCfr details = new VehicleLoanFcvCpvCfr();
            details.setFcvStatus(fcvStatus);
            details.setCpvStatus(cpvStatus);
            details.setCfrStatus(cfrFound);
            details.setWiNum(wiNum);
            details.setSlno(slno);
            VehicleLoanFcvCpvCfr savedDetails = fcvCpvCfrService.save(details, request, fcvFile, cpvFile, cfrFile);
            return ResponseEntity.ok(savedDetails);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while saving the details: " + e.getMessage());
        }
    }

    @PostMapping("/rbcpc-maker-submit22")
    public ResponseEntity<?> submitApplicationRBCPCMaker2(@RequestParam Long slno,
                                                         @RequestParam String winum,
                                                         @RequestParam String decision,
                                                         @RequestParam(required = false) String preDisbursementCondition,
                                                         @RequestParam(required = false) String remarks,
                                                         HttpServletRequest request) {
        try {
            if ("SB".equals(decision)) {
                if (remarks == null || remarks.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("Remarks are mandatory for sendback");
                }
                vehicleLoanMasterService.sendbackApplication(slno, winum, remarks, request);
                vehicleLoanTatService.updateTat(slno, usd.getEmployee().getPpcno(), winum, "BS");
                return ResponseEntity.ok("Application sent back successfully.");
            } else {
                if (preDisbursementCondition == null || preDisbursementCondition.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("Pre-disbursement condition is mandatory for forwarding");
                }
                String forwardedQueue = vehicleLoanMasterService.forwardApplicationRBCPCMaker(slno, winum, decision, preDisbursementCondition, request);
                vehicleLoanTatService.updateTat(slno, usd.getEmployee().getPpcno(), winum, forwardedQueue);
                return ResponseEntity.ok("Application forwarded successfully.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error processing application: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing application: " + e.getMessage());
        } finally {
            vehicleLoanLockService.ReleaseLock(slno, usd.getPPCNo());
        }
    }

     @PostMapping("/rbcpc-maker-submit")
    public ResponseEntity<?> submitApplicationRBCPCMaker(@RequestBody RBCPCMakerSave rbs,
                                                         HttpServletRequest request) {
        try {
            ResponseDTO response = rbcpcMakerSave.saveRBCMaker(rbs, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error processing application: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing application: " + e.getMessage());
        }
    }


    @PostMapping("/api/validate-rm-maker")
    public ResponseEntity<?> validateRMMakerSubmission(@RequestParam String winum, @RequestParam Long slno,@RequestParam String decision) {
        Map<String, List<String>> structuredErrors = rmMakerValidationService.validateRMMakerSubmission(winum, slno,decision);
        Map<String, Object> response = new HashMap<>();
        response.put("valid", structuredErrors.isEmpty());
        response.put("errors", structuredErrors);
        return ResponseEntity.ok(response);
    }
     @GetMapping("/ppcs/{levelName}")
    public ResponseEntity<?> getPPCsByLevel(@PathVariable String levelName) {
        try {
            List<Map<String, Object>> ppcs = fetchRepository.getGroupedPPCsByLevel(levelName);
            return ResponseEntity.ok(ppcs);
        } catch (Exception e) {
            log.error("Error fetching PPCs for level: {}", levelName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Error fetching PPCs"));
        }
    }

}
