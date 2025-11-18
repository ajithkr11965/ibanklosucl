package com.sib.ibanklosucl.controller;

import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanProgram;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.service.OctDetailsService;
import com.sib.ibanklosucl.service.VehicleLoanProgramService;
import com.sib.ibanklosucl.service.bpmsr.BpmService;
import com.sib.ibanklosucl.service.esbsr.CIFViewService;
import com.sib.ibanklosucl.service.esbsr.DedupService;
import com.sib.ibanklosucl.service.program.ProgramConstraintService;
import com.sib.ibanklosucl.service.vlsr.MisrctService;
import com.sib.ibanklosucl.service.vlsr.PincodeMasterService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/fetch")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ibanklos application APIs", description = "APIs for Ibanklos")
public class FetchController {
    private final MisrctService misrctService;
    private final FetchRepository fetchRepository;
    private final VehicleLoanApplicantService applicantService;
    private final UserSessionData usd;
    private final PincodeMasterService pincodeMasterService;
    private final BpmService bpmService;
    private final CIFViewService cifViewService;
    private final DedupService dedupService;
    private final OctDetailsService octDetailsService;
    private final VehicleLoanMasterService masterService;

    private final VehicleLoanProgramService vlProgramService;
    private final ProgramConstraintService programConstraintService;

    @PostMapping("/get-program-details")
    public ResponseEntity<?> getProgramDetails(@RequestBody Map<String, String> requestBody) {
        String applicantId = requestBody.get("applicantId");
        String wiNum = requestBody.get("wiNum");
        String slno = requestBody.get("slno");

        try {
            // Fetch the applicant
            VehicleLoanApplicant applicant = applicantService.findByApplicantIdAndDelFlag(Long.valueOf(applicantId));
            if (applicant == null) {
                return ResponseEntity.badRequest().body(Map.of("status", "E", "message", "Applicant not found"));
            }
            vlProgramService.markNullProgramInoRecordsAsDeleted(Long.valueOf(applicantId), wiNum);
            // Get the existing program
            Optional<VehicleLoanProgram> programOpt = vlProgramService.findProgrambyApplicantId(Long.valueOf(applicantId));
            if (programOpt.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "status", "S",
                        "message", "No program found for this applicant.",
                        "programDetails", ""
                ));
            }
            VehicleLoanProgram hlProgram = programOpt.get();

            // Fetch program details using the service
            Map<String, Object> programDetails = vlProgramService.getProgramDetails(hlProgram, Long.valueOf(slno));


            return ResponseEntity.ok(Map.of(
                    "status", "S",
                    "message", "Program details fetched successfully",
                    "programDetails", programDetails
            ));
        } catch (Exception e) {
            log.error("Error fetching program details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "E", "message", "Error fetching program details: " + e.getMessage()));
        }
    }
    @PostMapping("/validate-program-selection")
    public ResponseEntity<?> validateProgramSelection(@RequestBody Map<String, String> requestBody) {
        String wiNum = requestBody.get("wiNum");
        String applicantIdStr = requestBody.get("applicantId");
        String incomeConsidered = requestBody.get("incomeConsidered");
        String programCode = requestBody.get("programCode");

        log.info("Validating program selection constraints - wiNum: {}, applicantId: {}, incomeConsidered: {}, programCode: {}",
                wiNum, applicantIdStr, incomeConsidered, programCode);

        // Validate required parameters
        if (wiNum == null || applicantIdStr == null || incomeConsidered == null || programCode == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "E");
            response.put("message", "All parameters (wiNum, applicantId, incomeConsidered, programCode) are required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Long applicantId = Long.parseLong(applicantIdStr);

            // Call service to validate program selection
            ProgramConstraintService.ValidationResult validationResult = programConstraintService.validateProgramSelection(
                    wiNum, applicantId, incomeConsidered, programCode);
            Map<String, Object> response = new HashMap<>();

            if (validationResult.isValid()) {
                response.put("status", "S");
                response.put("message", "Program selection is valid");
                log.info("Program selection validation successful for applicantId: {}", applicantId);
            } else {
                response.put("status", "E");
                response.put("message", validationResult.getMessage());

                if (validationResult.getRequiredProgram() != null) {
                    response.put("requiredProgram", validationResult.getRequiredProgram());
                }

                log.info("Program selection validation failed for applicantId: {} - {}",
                        applicantId, validationResult.getMessage());
            }

            return ResponseEntity.ok(response);

        } catch (NumberFormatException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "E");
            response.put("message", "Invalid applicant ID format");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error validating program selection", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "E");
            response.put("message", "Error validating program selection: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
