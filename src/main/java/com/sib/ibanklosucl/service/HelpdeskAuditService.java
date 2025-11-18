package com.sib.ibanklosucl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class HelpdeskAuditService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Logs a helpdesk action with detailed information
     */
    public void logAction(
            String actionType,
            String wiNum,
            String userId,
            String remarks,
            String ipAddress,
            String additionalInfo) {

        try {
            String sql = """
                INSERT INTO HELPDESK_AUDIT_LOG (
                    ACTION_TYPE,
                    WI_NUM,
                    ACTED_BY,
                    ACTION_DATE,
                    REMARKS,
                    IP_ADDRESS,
                    BROWSER_INFO,
                    ADDITIONAL_INFO
                ) VALUES (?, ?, ?, SYSTIMESTAMP, ?, ?, ?, ?)
            """;

            jdbcTemplate.update(
                sql,
                actionType,
                wiNum,
                userId,
                remarks,
                ipAddress,
                getBrowserInfo(),
                additionalInfo
            );

            log.info("Audit log created - Action: {}, WI: {}, User: {}",
                actionType, wiNum, userId);

        } catch (Exception e) {
            log.error("Error creating audit log - Action: {}, WI: {}, User: {}",
                actionType, wiNum, userId, e);
        }
    }

    /**
     * Retrieves audit trail for a specific work item
     */
    public List<Map<String, Object>> getAuditTrail(String wiNum) {
        String sql = """
            SELECT 
                ACTION_TYPE,
                ACTED_BY,
                ACTION_DATE,
                REMARKS,
                IP_ADDRESS,
                BROWSER_INFO,
                ADDITIONAL_INFO
            FROM HELPDESK_AUDIT_LOG
            WHERE WI_NUM = ?
            ORDER BY ACTION_DATE DESC
        """;

        try {
            return jdbcTemplate.queryForList(sql, wiNum);
        } catch (Exception e) {
            log.error("Error fetching audit trail for WI: {}", wiNum, e);
            return Collections.emptyList();
        }
    }
    public Page<Map<String, Object>> apiLogs(String wiNum, int page, int size) {
        String countSql = "SELECT COUNT(*) FROM APILOG WHERE WI_NUM = ?";

        String dataSql = """
        SELECT * FROM (
            SELECT a.*, ROWNUM rnum FROM (
                SELECT ID, API_NAME, REQUEST, RESPONSE 
                FROM APILOG 
                WHERE WI_NUM = ? 
                ORDER BY ID DESC
            ) a WHERE ROWNUM <= ?
        ) WHERE rnum > ?
        """;

        int start = page * size;
        int end = start + size;

        // Get total count
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, wiNum);

        // Handle null total
        if (total == null) {
            total = 0L;
        }

        // Get paginated data
        List<Map<String, Object>> content = jdbcTemplate.queryForList(dataSql, wiNum, end, start);

        // Create Page object
        Page<Map<String, Object>> pageResult = new PageImpl<>(content, PageRequest.of(page, size), total);

        return pageResult;
    }




    /**
     * Get audit summary between dates
     */
    public Map<String, Object> getAuditSummary(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = """
            SELECT 
                ACTION_TYPE,
                COUNT(*) as ACTION_COUNT,
                COUNT(DISTINCT ACTED_BY) as UNIQUE_USERS,
                COUNT(DISTINCT WI_NUM) as UNIQUE_WORKITEMS
            FROM HELPDESK_AUDIT_LOG
            WHERE ACTION_DATE BETWEEN ? AND ?
            GROUP BY ACTION_TYPE
        """;

        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(
                sql,
                Timestamp.valueOf(startDate),
                Timestamp.valueOf(endDate)
            );

            Map<String, Object> summary = new HashMap<>();
            summary.put("periodStart", startDate);
            summary.put("periodEnd", endDate);
            summary.put("actionSummary", results);

            return summary;
        } catch (Exception e) {
            log.error("Error generating audit summary", e);
            return Collections.emptyMap();
        }
    }

    /**
     * Export audit logs to CSV
     */


    /**
     * Get user action history
     */
    public List<Map<String, Object>> getUserActionHistory(String userId) {
        String sql = """
            SELECT 
                ACTION_TYPE,
                WI_NUM,
                ACTION_DATE,
                REMARKS,
                IP_ADDRESS
            FROM HELPDESK_AUDIT_LOG
            WHERE ACTED_BY = ?
            ORDER BY ACTION_DATE DESC
        """;

        try {
            return jdbcTemplate.queryForList(sql, userId);
        } catch (Exception e) {
            log.error("Error fetching user action history for user: {}", userId, e);
            return Collections.emptyList();
        }
    }

    private String getBrowserInfo() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                HttpServletRequest request =
                    ((ServletRequestAttributes) requestAttributes).getRequest();
                return request.getHeader("User-Agent");
            }
        } catch (Exception e) {
            log.debug("Could not get browser info", e);
        }
        return "Unknown";
    }

    public List<Map<String, Object>> getApiEndpoints() {
        String sql = """
            SELECT API, API_NAME 
            FROM VLOS_API_MASTER 
            WHERE DEL_FLG = 'N' 
            ORDER BY API_NAME
        """;

        try {
            return jdbcTemplate.queryForList(sql);
        } catch (DataAccessException e) {
            log.error("Error fetching API endpoints", e);
            throw new RuntimeException("Failed to fetch API endpoints", e);
        }
    }

    public Map<String, String> getApiLogData(String workItem, String apiEndpoint) {
        String sql = """
            SELECT REQUEST as request, RESPONSE as response 
            FROM APILOG 
            WHERE WI_NUM = ? 
            AND API_NAME = ? 
            ORDER BY REQUEST_TIMESTAMP DESC 
            FETCH FIRST 1 ROW ONLY
        """;

        try {
            return jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> {
                        Map<String, String> result = new HashMap<>();
                        // Handle null values from database
                        String request = rs.getString("request");
                        String response = rs.getString("response");
                        result.put("request", request != null ? request : "{}");
                        result.put("response", response != null ? response : "{}");
                        return result;
                    },
                    workItem, apiEndpoint
            );
        } catch (EmptyResultDataAccessException e) {
            log.info("No data found for workItem: {} and apiEndpoint: {}", workItem, apiEndpoint);
            Map<String, String> emptyResult = new HashMap<>();
            emptyResult.put("request", "{}");
            emptyResult.put("response", "{}");
            return emptyResult;
        } catch (DataAccessException e) {
            log.error("Error fetching API log data", e);
            throw new RuntimeException("Failed to fetch API log data", e);
        }
    }


}
