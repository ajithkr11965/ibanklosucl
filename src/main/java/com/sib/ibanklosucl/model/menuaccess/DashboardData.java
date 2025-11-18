package com.sib.ibanklosucl.model.menuaccess;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardData {
    private int totalTasks;
    private Map<String, Integer> menuItemCounts;
    private List<QueueStat> queueStats;
    private List<DashboardItem> dashboardItems;
}
