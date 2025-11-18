package com.sib.ibanklosucl.repository.metrics;

import com.sib.ibanklosucl.model.metrics.PerformanceMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceMetricsRepository extends JpaRepository<PerformanceMetrics, Long> {
}

