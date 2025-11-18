package com.sib.ibanklosucl.model.metrics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "performance_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double pageLoadTime;
    private double domContentLoaded;
    private Double firstPaint;
    private Double firstContentfulPaint;
    private String url;
    private String timestamp;
    private String ipAddress;
    private String userAgent;
    @Column(name = "ppcno")
    private String ppcno;
    @Column(name = "SOL_ID")
    private String sol_id;
    @Column(name = "load_date")
    private LocalDateTime load_date;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_metrics_id")
    private List<ResourceMetrics> resources;

    // Getters and setters
    public double getPageLoadTime() { return pageLoadTime; }
    public void setPageLoadTime(double pageLoadTime) { this.pageLoadTime = pageLoadTime; }

    public double getDomContentLoaded() { return domContentLoaded; }
    public void setDomContentLoaded(double domContentLoaded) { this.domContentLoaded = domContentLoaded; }

    public Double getFirstPaint() { return firstPaint; }
    public void setFirstPaint(Double firstPaint) { this.firstPaint = firstPaint; }

    public Double getFirstContentfulPaint() { return firstContentfulPaint; }
    public void setFirstContentfulPaint(Double firstContentfulPaint) { this.firstContentfulPaint = firstContentfulPaint; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public List<ResourceMetrics> getResources() { return resources; }
    public void setResources(List<ResourceMetrics> resources) { this.resources = resources; }
}