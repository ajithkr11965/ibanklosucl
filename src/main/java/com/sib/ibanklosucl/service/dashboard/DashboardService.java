package com.sib.ibanklosucl.service.dashboard;

import com.sib.ibanklosucl.dto.MenuList;
import com.sib.ibanklosucl.dto.dashboard.QueueCountDTO;
import com.sib.ibanklosucl.model.menuaccess.DashboardData;
import com.sib.ibanklosucl.model.menuaccess.DashboardItem;
import com.sib.ibanklosucl.repository.menuaccess.QueueVariationRepository;
import com.sib.ibanklosucl.service.vlsr.MenuAccessService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class DashboardService {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private QueueVariationRepository queueVariationRepository;

    @Autowired
    private VehicleLoanMasterService vlservice;

    @Autowired
    private UserSessionData usd;

    @Autowired
    private MenuAccessService menuAccessService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public DashboardData prepareDashboardData() {
        String homeSol = usd.getSolid();
        List<MenuList> accessibleMenus = menuAccessService.getAccessibleMenus();

        // Calculate the task counts for each accessible menu
        Map<String, Integer> menuItemCounts = getMenuCounts(homeSol,accessibleMenus);


        QueueCountDTO queueCountDTO = vlservice.getStatusCountsBySolId(usd.getSolid());
        // Calculate the total task count

        //System.out.println("queueCount64"+queueCountDTO.getCountForMenu("WAIVE"));
        int totalTasks = 0;
        List<DashboardItem> dashboardItems = namedParameterJdbcTemplate.query(
                "SELECT * FROM DASHBOARD_ITEM ORDER BY ID",
                new MapSqlParameterSource(),
                new BeanPropertyRowMapper<>(DashboardItem.class)
        );
        if(!usd.getEmployee().getOffType().equalsIgnoreCase("BRANCH"))
        {
//            totalTasks = accessibleMenus.stream()
//                    .mapToInt(menu -> menuItemCounts.getOrDefault(menu.getMenuID(), queueCountDTO.getCountForMenu(menu.getMenuID())==null?0:queueCountDTO.getCountForMenu(menu.getMenuID()))).sum();

            List<DashboardItem> eligibleItems = dashboardItems.stream().filter(dashboardItem -> accessibleMenus.stream().anyMatch(accessibleMenu ->accessibleMenu.getMenuID().equals(dashboardItem.getMenuId()))).collect(Collectors.toList());
            for (DashboardItem item : eligibleItems) {
                int currentCount = queueCountDTO.getCountForMenu(item.getMenuId())==null||queueCountDTO.getCountForMenu(item.getMenuId())==0? menuItemCounts.get(item.getMenuId()):queueCountDTO.getCountForMenu(item.getMenuId());
                //  int currentCount = menuItemCounts.getOrDefault(item.getMenuId(), queueCountDTO.getCountForMenu(item.getMenuId()));
                item.setCount(currentCount);
                totalTasks+=currentCount;

                // Calculate the variation by comparing with the previous day's count

                int previousCount = queueVariationRepository.findPreviousCountBySolIdAndMenuIdAndTimestampBetween(homeSol, item.getMenuId())
                        .orElse(currentCount);
                int variation = currentCount - previousCount;
                item.setVariation(variation);

                // Set the direction based on the variation
                if (variation > 0) {
                    item.setCountDirection("up");
                } else if (variation < 0) {
                    item.setCountDirection("down");
                } else {
                    item.setCountDirection(""); // No arrow
                }
            }


        }
        else
        {
            totalTasks = menuItemCounts.values().stream().mapToInt(Integer::intValue).sum();
            // Set counts and variations for each dashboard item
            for (DashboardItem item : dashboardItems) {
                 int currentCount = menuItemCounts.getOrDefault(item.getMenuId(), queueCountDTO.getCountForMenu(item.getMenuId())==null?0:queueCountDTO.getCountForMenu(item.getMenuId()));
                //  int currentCount = menuItemCounts.getOrDefault(item.getMenuId(), queueCountDTO.getCountForMenu(item.getMenuId()));
                item.setCount(currentCount);

                // Calculate the variation by comparing with the previous day's count

                int previousCount = queueVariationRepository.findPreviousCountBySolIdAndMenuIdAndTimestampBetween(homeSol, item.getMenuId())
                        .orElse(currentCount);
                int variation = currentCount - previousCount;
                item.setVariation(variation);

                // Set the direction based on the variation
                if (variation > 0) {
                    item.setCountDirection("up");
                } else if (variation < 0) {
                    item.setCountDirection("down");
                } else {
                    item.setCountDirection(""); // No arrow
                }
            }
        }



        // Retrieve all dashboard items





        // Return the data encapsulated in DashboardData
        DashboardData dashboardData = new DashboardData();
        dashboardData.setMenuItemCounts(menuItemCounts);

        dashboardData.setTotalTasks(totalTasks);
        dashboardData.setDashboardItems(dashboardItems);

        return dashboardData;
    }

    private Map<String, Integer> getMenuCounts(String homeSol,List<MenuList> accessibleMenus)
    {
        if(!usd.getEmployee().getOffType().equalsIgnoreCase("BRANCH"))
        {
            Map<String, Integer> menuItemCounts = accessibleMenus.stream()
                    .collect(Collectors.toMap(
                            MenuList::getMenuID,
                            menu -> jdbcTemplate.queryForObject(
                                    "SELECT COALESCE(SUM(COUNT), 0) FROM QUEUE_STAT WHERE QUEUE = ?",
                                    new Object[]{menu.getMenuID()},
                                    Integer.class)
                    ));
            return menuItemCounts;
        }
        else {
            Map<String, Integer> menuItemCounts = accessibleMenus.stream()
                    .collect(Collectors.toMap(
                            MenuList::getMenuID,
                            menu -> jdbcTemplate.queryForObject(
                                    "SELECT COALESCE(SUM(COUNT), 0) FROM QUEUE_STAT WHERE SOL_ID = ? AND QUEUE = ?",
                                    new Object[]{homeSol, menu.getMenuID()},
                                    Integer.class)
                    ));

            return menuItemCounts;
        }


    }

}

