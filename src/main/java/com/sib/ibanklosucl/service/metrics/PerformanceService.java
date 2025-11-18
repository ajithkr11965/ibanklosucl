package com.sib.ibanklosucl.service.metrics;

import com.sib.ibanklosucl.model.metrics.PerformanceMetrics;
import com.sib.ibanklosucl.repository.metrics.PerformanceMetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PerformanceService {

    @Autowired
    private PerformanceMetricsRepository metricsRepository;

    public void saveMetrics(PerformanceMetrics metrics) {
        metricsRepository.save(metrics);
    }
}

