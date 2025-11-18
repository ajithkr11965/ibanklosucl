package com.sib.ibanklosucl.controller;

import com.sib.ibanklosucl.config.menuaccess.RequiresMenuAccess;
import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.doc.RepaymentRequestWrapper;
import com.sib.ibanklosucl.model.VehicleLoanLock;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.model.VehicleLoanSubqueueTask;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.service.HelpdeskAuditService;
import com.sib.ibanklosucl.service.VehicleLoanHelpdeskService;
import com.sib.ibanklosucl.service.doc.RepaymentService;
import com.sib.ibanklosucl.service.impl.RepaymentSaveImpl;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/helpdesk")
@Slf4j
public class HelpdeskOperationsController {

    @Autowired
    private VehicleLoanHelpdeskService helpdeskService;

    @Autowired
    private HelpdeskAuditService auditService;

    @Autowired
    private UserSessionData usd;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private RepaymentService repaymentService;
    @Autowired
    private RepaymentSaveImpl repaymentSave;




    private int maxDaysAudit = 30; // Default 30 days for audit trail

    @RequiresMenuAccess(menuIds = {"HDA"})
    @GetMapping
    public String showHelpdeskPage(Model model, HttpSession session) {
        try {
            // Add user role check
            String userRole = (String) session.getAttribute("userRole");
//            if (!"HELPDESK".equals(userRole) && !"ADMIN".equals(userRole)) {
//                model.addAttribute("errorMessage", "Unauthorized access. Helpdesk role required.");
//                return "error/403";
//            }
            model.addAttribute("usd",usd);

            return "helpdesk/operations";
        } catch (Exception e) {
            log.error("Error in showHelpdeskPage: ", e);
            model.addAttribute("errorMessage", "System error occurred. Please try again later.");
            return "error/500";
        }
    }


    @GetMapping("/search")
    public String searchWorkItem(
            @RequestParam String wiNum,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        log.info("Searching for work item: {}, page: {}, size: {}", wiNum, page, size);
        model.addAttribute("usd",usd);
        try {
            // Input validation
            if (wiNum == null || wiNum.trim().isEmpty()) {
                model.addAttribute("errorMessage", "Work item number is required");
                return "helpdesk/operations";
            }

            // Basic information retrieval
            VehicleLoanMaster vehicleLoanMaster = helpdeskService.getWorkItemDetails(wiNum);
            if (vehicleLoanMaster == null) {
                model.addAttribute("errorMessage", "Work item not found: " + wiNum);
                return "helpdesk/operations";
            }

           // ExperianData experianData = helpdeskService.getExperianWorkItemDetails(wiNum);
          boolean exists  = helpdeskService.workItemExixts(wiNum);

            // Additional information retrieval
            try {
                VehicleLoanLock vehicleLoanLock = helpdeskService.getWorkItemLock(wiNum);
                List<VehicleLoanSubqueueTask> subqueueTasks = helpdeskService.getSubqueueTasks(wiNum);
                List<Map<String, Object>> auditTrail = auditService.getAuditTrail(wiNum);
                model.addAttribute("bankName", fetchRepository.getBankCode());
                model.addAttribute("vehicleLoanRepayment", repaymentService.getRepaymentDetails(wiNum));


//                // Paginated API logs
//                int validPage = Math.max(1, page);
//                int validSize = Math.max(1, Math.min(50, size)); // Limit maximum page size
//
//                // Adjust page indexing (Spring Data JPA pages are zero-based)
//                int adjustedPage = validPage - 1;
//
//                Page<Map<String, Object>> apiLogsPage = auditService.apiLogs(wiNum, adjustedPage, validSize);
//
//                // Add attributes to model
//                model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
//                model.addAttribute("vehicleLoanLock", vehicleLoanLock);
//                model.addAttribute("subqueueTasks", subqueueTasks);
//                model.addAttribute("auditTrail", auditTrail);
//                model.addAttribute("apiLogs", apiLogsPage.getContent());
//
//                // Pagination attributes
//                model.addAttribute("currentPage", validPage);
//                model.addAttribute("pageSize", validSize);
//                model.addAttribute("totalPages", apiLogsPage.getTotalPages());
//                System.out.println("totalElements"+apiLogsPage.getTotalElements());
//                model.addAttribute("totalElements", apiLogsPage.getTotalElements());



                                // Add attributes to model
                model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
               // model.addAttribute("experianData", experianData);
                model.addAttribute("workItemExists", exists);

                model.addAttribute("wiNum", wiNum);
                model.addAttribute("vehicleLoanLock", vehicleLoanLock);
                model.addAttribute("subqueueTasks", subqueueTasks);
                model.addAttribute("auditTrail", auditTrail);
                model.addAttribute("apiLogs", null);

                // Pagination attributes
                model.addAttribute("currentPage", null);
                model.addAttribute("pageSize", null);
                model.addAttribute("totalPages", null);
                model.addAttribute("totalElements", null);


                return "helpdesk/workItemDetails";

            } catch (Exception e) {
                log.error("Error fetching additional details for work item: {}", wiNum, e);
                model.addAttribute("errorMessage", "Error fetching work item details. Please try again.");
                model.addAttribute("vehicleLoanMaster", vehicleLoanMaster); // Still show basic information
                return "helpdesk/workItemDetails";
            }

        } catch (Exception e) {
            log.error("Failed to process work item search: {}", wiNum, e);
            model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            return "helpdesk/operations";
        }
    }
    @PostMapping("/updateRepaymentDetails")
    @RequiresMenuAccess(menuIds = {"BD"})
    public ResponseEntity<ResponseDTO> updateRepayment(@RequestBody RepaymentRequestWrapper wrapper,
                                                      HttpServletRequest request
                                                       ) {

        try {
            String userId = usd.getPPCNo();
            String ipAddress = request.getRemoteAddr();
            ResponseDTO dto=repaymentSave.saveRepaymentForBD(wrapper.getRepaymentDTO());
            auditService.logAction(
                    "REPAYMENT",
                    wrapper.getRepaymentDTO().getWiNum(),
                    userId,
                    wrapper.getRemarks(),
                    ipAddress,
                    "Additional context if needed"
            );
            return new  ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Some exception",e);
            return new ResponseEntity<>(new ResponseDTO("F", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/submit-api")
    public String submitApi(@RequestParam String workItem,
                            @RequestParam String apiEndpoint,
                            Model model) {
        // Validate input
        if (!workItem.matches("VLR_\\d{9}")) {
            throw new IllegalArgumentException("Invalid work item format");
        }

        Map<String, String> apiLogData = auditService.getApiLogData(workItem, apiEndpoint);

        // Add the JSON data as strings that will be parsed by JavaScript
        model.addAttribute("requestJson", apiLogData.get("request"));
        model.addAttribute("responseJson", apiLogData.get("response"));

        // Add metadata for the view
        model.addAttribute("workItem", workItem);
        model.addAttribute("apiEndpoint", apiEndpoint);
        return "helpdesk/json-viewer";
    }



    @PostMapping("/manage-acopn")
    @ResponseBody
    public ResponseEntity<?> manageAcopn(
            @RequestParam String wiNum,
            @RequestParam String remarks,
            @RequestParam String queueAction,
            @RequestParam(required = false) String userId, // or retrieve from session
            HttpServletRequest request) {
        try {
            // Validate remarks
            if (remarks == null || remarks.trim().length() < 10) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("error", "Remarks must be at least 10 characters long"));
            }

            // Fallback if userId is not provided
            if (userId == null || userId.isEmpty()) {
                userId = (String) request.getSession().getAttribute("userId");
            }

            String ipAddress = request.getRemoteAddr();

            // Call the service layer
            helpdeskService.manageAcopn(wiNum, remarks, queueAction, userId);

            // Log into your existing audit mechanism
            auditService.logAction(
                    "MANAGE_ACOPN",
                    wiNum,
                    userId,
                    remarks + " (Queue Action: " + queueAction + ")",
                    ipAddress,
                    "Additional context if needed"
            );

            return ResponseEntity.ok(new ApiResponse("success",
                    String.format("ACOPN management completed with action: %s", queueAction)));

        } catch (Exception e) {
            log.error("Error in manage-acopn for {}: ", wiNum, e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", e.getMessage()));
        }
    }


    @PostMapping("/reset-documentation")
    @ResponseBody
    public ResponseEntity<?> resetDocumentation(
            @RequestParam String wiNum,
            @RequestParam String remarks,
            HttpServletRequest request) {
        try {
            // Input validation


            String userId = usd.getPPCNo();
            String ipAddress = request.getRemoteAddr();

            // Get document status before reset
            boolean isDocSigned = helpdeskService.isDocumentSigned(wiNum);

            helpdeskService.resetDocumentation(wiNum, remarks, userId);

            // Log the action with additional context
            auditService.logAction(
                "RESET_DOCUMENTATION",
                wiNum,
                userId,
                remarks,
                ipAddress,
                String.format("Document was %s before reset", isDocSigned ? "signed" : "not signed")
            );

            String message = "Documentation reset successfully";
            if (isDocSigned) {
                message += ". Note: Document was previously signed and may incur new stamp charges.";
            }

            return ResponseEntity.ok(new ApiResponse("success", message));

        } catch (Exception e) {
            log.error("Error resetting documentation for {}: ", wiNum, e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse("error", e.getMessage()));
        }
    }

   @PostMapping("/reset-experian")
   @ResponseBody
   public ResponseEntity<?>  resetExperian(
           @RequestParam String workItem,
           @RequestParam String panNumber,
           @RequestParam String remarks,
           HttpServletRequest request) {
       try {
            // Input validation
//            if (remarks == null || remarks.trim().length() < 10) {
//                return ResponseEntity.badRequest()
//                        .body(new ResponseDTO("error", "Remarks must be at least 10 characters long"));
//
//            }
            String userId = usd.getPPCNo();
           String ipAddress = request.getRemoteAddr();
           helpdeskService.processCreditComplete(panNumber,workItem);
            helpdeskService.resetExperian(workItem,panNumber ,remarks,userId);
           auditService.logAction(
                   "RESET_EXPERIAN",
                   workItem,
                   userId,
                   remarks,
                   ipAddress,
                   String.format("Additional context if needed")
           );
           String message = "Experian reset successfully";
          // model.addAttribute("successMessage","Experian reset successfully");
           return ResponseEntity.ok(new ApiResponse("Success", message));
          // return "helpdesk/workItemDetails";

       }
       catch (Exception e) {
           log.error("Error resetting Experian for {}: ", workItem, e);
           return ResponseEntity.badRequest()
                   .body(new ApiResponse("error", e.getMessage()));

       }
    }


    @PostMapping("/release-lock")
    @ResponseBody
    public ResponseEntity<?> releaseLock(
            @RequestParam String wiNum,
            @RequestParam String remarks,
            HttpServletRequest request) {
        try {
            // Input validation
            if (remarks == null || remarks.trim().length() < 10) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Remarks must be at least 10 characters long"));
            }

            String userId = (String) request.getSession().getAttribute("userId");
            String ipAddress = request.getRemoteAddr();

            helpdeskService.releaseLock(wiNum, remarks, userId);

            auditService.logAction(
                "RELEASE_LOCK",
                wiNum,
                userId,
                remarks,
                ipAddress,
                null
            );

            return ResponseEntity.ok(new ApiResponse("success", "Lock released successfully"));

        } catch (Exception e) {
            log.error("Error releasing lock for {}: ", wiNum, e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PostMapping("/release-child-locks")
    @ResponseBody
    public ResponseEntity<?> releaseChildLocks(
            @RequestParam String wiNum,
            @RequestParam String remarks,
            HttpServletRequest request) {
        try {
            // Input validation
            if (remarks == null || remarks.trim().length() < 10) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Remarks must be at least 10 characters long"));
            }

            String userId = (String) request.getSession().getAttribute("userId");
            String ipAddress = request.getRemoteAddr();

            // Get number of locked tasks before release
            int lockedTasksCount = helpdeskService.getLockedTasksCount(wiNum);

            helpdeskService.releaseChildLocks(wiNum, remarks, userId);

            auditService.logAction(
                "RELEASE_CHILD_LOCKS",
                wiNum,
                userId,
                remarks,
                ipAddress,
                String.format("Released %d locked tasks", lockedTasksCount)
            );

            return ResponseEntity.ok(new ApiResponse("success",
                String.format("Successfully released %d child locks", lockedTasksCount)));

        } catch (Exception e) {
            log.error("Error releasing child locks for {}: ", wiNum, e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse("error", e.getMessage()));
        }
    }




}