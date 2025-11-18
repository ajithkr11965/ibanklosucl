package com.sib.ibanklosucl.model.metrics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "resource_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private double duration;
    private long resource_size;
    private double startTime;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }

    public long getResource_size() { return resource_size; }
    public void setResource_size(long resource_size) { this.resource_size = resource_size; }

    public double getStartTime() { return startTime; }
    public void setStartTime(double startTime) { this.startTime = startTime; }

    @Override
    public String toString() {
        return "ResourceMetrics{" +
               "name='" + name + '\'' +
               ", type='" + type + '\'' +
               ", duration=" + duration +
               ", size=" + resource_size +
               ", startTime=" + startTime +
               '}';
    }
}

