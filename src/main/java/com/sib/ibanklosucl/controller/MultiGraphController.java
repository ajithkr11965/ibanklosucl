package com.sib.ibanklosucl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.service.reports.MultiGraphDataService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Import necessary Spring Web MVC classes
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// Import necessary Spring annotations
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// Import necessary Java utility classes
import java.util.*;

@Controller
public class MultiGraphController {

    private static final Logger logger = LoggerFactory.getLogger(MultiGraphController.class);

    private final UserSessionData usd;
    private final MultiGraphDataService multiGraphDataService;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public MultiGraphController(UserSessionData usd, MultiGraphDataService multiGraphDataService) {
        this.usd = usd;
        this.multiGraphDataService = multiGraphDataService;
    }

    @GetMapping("/multi-graph-view")
    public String showMultiGraphView(Model model) {
        try {
            List<String> graphTypes = Arrays.asList("monthlyStats", "queueProcessing", "loanStatus", "loanPerformance");
            model.addAttribute("employee", usd.getEmployee());
            model.addAttribute("graphData", multiGraphDataService.getMultiGraphData(graphTypes));
            Map<String, Object> initialData = multiGraphDataService.getInitialDashboardData();
            logger.info("Initial data: {}", objectMapper.writeValueAsString(initialData));
            model.addAttribute("initialData", objectMapper.writeValueAsString(initialData));
            model.addAttribute("employee", usd.getEmployee());
            return "rpt/multiGraphView";
        } catch (Exception e) {
            logger.error("Error preparing multi-graph view", e);
            model.addAttribute("error", "An unexpected error occurred while preparing the dashboard.");
            return "error";
        }
    }

    @GetMapping("/multi-graph-data")
    @ResponseBody
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getMultiGraphData(@RequestParam List<String> graphTypes) {
        try {
            Map<String, List<Map<String, Object>>> data = multiGraphDataService.getMultiGraphData(graphTypes);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid graph type requested", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error fetching multi-graph data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/api/filtered-data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFilteredData(@RequestBody Map<String, Object> filters) {
        try {
            Map<String, Object> dashboardData = multiGraphDataService.getFilteredDashboardData(filters);
            return ResponseEntity.ok(dashboardData);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid filter parameters", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid filter parameters: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching filtered data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred while fetching filtered data."));
        }
    }

    @PostMapping("/api/export-data")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> exportData() {
        try {
            List<Map<String, Object>> exportData = multiGraphDataService.getExportData();
            if (exportData.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(exportData);
        } catch (Exception e) {
            logger.error("Error exporting data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/detailed-data")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getDetailedData(@RequestParam("queue") String queue) {
        try {
            List<Map<String, Object>> detailedData = multiGraphDataService.getDetailedData(queue);
            if (detailedData.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(detailedData);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid queue parameter", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error fetching detailed data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Revised getQueueOptions endpoint
    @GetMapping("/api/queue-options")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getQueueOptions() {
        try {
            List<Map<String, Object>> queues = multiGraphDataService.getQueueOptions();
            return ResponseEntity.ok(queues);
        } catch (Exception e) {
            logger.error("Error fetching queue options", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // New endpoint for drill-down data
    @PostMapping("/api/drill-down-data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDrillDownData(@RequestBody Map<String, Object> params) {
        try {
            Map<String, Object> drillDownData = multiGraphDataService.getDrillDownData(params);
            return ResponseEntity.ok(drillDownData);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid drill-down parameters", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid parameters: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching drill-down data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred while fetching drill-down data."));
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        logger.error("Unhandled exception in MultiGraphController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred. Please try again later."));
    }
}