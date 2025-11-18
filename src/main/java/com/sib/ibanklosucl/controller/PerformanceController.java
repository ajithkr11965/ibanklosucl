package com.sib.ibanklosucl.controller;

import com.sib.ibanklosucl.model.metrics.PerformanceMetrics;
import com.sib.ibanklosucl.service.metrics.PerformanceService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@org.springframework.web.bind.annotation.RestController
@RequestMapping("/metricsapi")
@Slf4j
public class PerformanceController {
    @Autowired
    private PerformanceService performanceService;
    @Autowired
    private UserSessionData usd;
    @PostMapping("/log-performance")
    public ResponseEntity<String> logPerformance(@RequestBody PerformanceMetrics metrics, HttpServletRequest request) {
        log.info("Performance metrics: {}", metrics);
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        metrics.setIpAddress(ipAddress);
        metrics.setUserAgent(userAgent);
        metrics.setSol_id(usd.getSolid());
        metrics.setPpcno(usd.getPPCNo());
        metrics.setLoad_date(LocalDateTime.now());
        performanceService.saveMetrics(metrics);
        log.info("Performance metrics received from IP: {}", ipAddress);
        return ResponseEntity.ok("Metrics received and saved");
    }
}
