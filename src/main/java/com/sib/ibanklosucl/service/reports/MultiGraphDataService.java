package com.sib.ibanklosucl.service.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// Import necessary Spring JDBC classes
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
// Import necessary annotations
import org.springframework.stereotype.Service;

// Import necessary Java utility classes
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MultiGraphDataService {

    private static final Logger logger = LoggerFactory.getLogger(MultiGraphDataService.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public MultiGraphDataService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public Map<String, List<Map<String, Object>>> getMultiGraphData(List<String> graphTypes) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        for (String graphType : graphTypes) {
            try {
                String sql = getQueryForGraphType(graphType);
                List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);
                result.put(graphType, data);
            } catch (Exception e) {
                logger.error("Error fetching data for graph type: " + graphType, e);
                result.put(graphType, new ArrayList<>());
            }
        }
        return result;
    }

    private String getQueryForGraphType(String graphType) {
        return switch (graphType) {
            case "monthlyStats" -> """
                SELECT TO_CHAR(vloan.CMDATE, 'YYYY-MM') AS month,
                COUNT(*) AS total_applications,
                SUM(CASE WHEN vloan.SAN_FLG = 'Y' THEN 1 ELSE 0 END) AS sanctioned_count,
                SUM(CASE WHEN vloan.DISB_FLG = 'SUCCESS' THEN 1 ELSE 0 END) AS disbursed_count,
                SUM(NVL(vdetails.SANC_AMOUNT_RECOMMENDED, 0)) AS sum_loan_sanc_amount,
                SUM(nvl(vdisb.DISBURSEDAMOUNT,0)) AS disbursed_amount
                FROM VEHICLE_LOAN_MASTER vloan
                JOIN VEHICLE_LOAN_ELIGIBILITY vdetails ON vloan.wi_num = vdetails.wi_num
                LEFT JOIN VEHICLE_LOAN_ACCOUNT vdisb ON vloan.wi_num = vdisb.wi_num
                WHERE vloan.CMDATE >= ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -12)
                GROUP BY TO_CHAR(vloan.CMDATE, 'YYYY-MM')
                ORDER BY month
                """;
            case "queueProcessing" -> """
                SELECT misrct.CODEDESC AS queue_name,
                ROUND(AVG((COALESCE(tat.QUEUE_EXIT_DATE, SYSDATE) - tat.QUEUE_ENTRY_DATE) * 24), 2) AS avg_processing_time_hours
                FROM VEHICLE_LOAN_TAT tat
                JOIN misrct ON tat.QUEUE = misrct.CODEVALUE AND misrct.CODETYPE = 'QT'
                WHERE tat.DEL_FLG = 'N'
                GROUP BY misrct.CODEDESC
                ORDER BY avg_processing_time_hours DESC
                """;
            case "loanStatus" -> """
                SELECT status, COUNT(*) AS count
                FROM VEHICLE_LOAN_MASTER vloan
                WHERE vloan.CUST_NAME IS NOT NULL AND vloan.ACTIVE_FLG = 'Y'
                GROUP BY status
                """;
            case "loanPerformance" -> """
                SELECT TO_CHAR(vloan.CMDATE, 'YYYY-MM') AS month,
                ROUND(SUM(NVL(vdetails.SANC_AMOUNT_RECOMMENDED, 0)) / 1000000, 2) AS sanctioned_amount,
                ROUND(SUM(nvl(vdisb.DISBURSEDAMOUNT,0)) / 1000000, 2) AS disbursed_amount
                FROM VEHICLE_LOAN_MASTER vloan
                JOIN VEHICLE_LOAN_ELIGIBILITY vdetails ON vloan.wi_num = vdetails.wi_num
                LEFT JOIN VEHICLE_LOAN_ACCOUNT vdisb ON vloan.wi_num = vdisb.wi_num
                GROUP BY TO_CHAR(vloan.CMDATE, 'YYYY-MM')
                ORDER BY month
                """;
            default -> throw new IllegalArgumentException("Unsupported graph type: " + graphType);
        };
    }

    public Map<String, Object> getFilteredDashboardData(Map<String, Object> filters) {
        String aggregation = (String) filters.getOrDefault("aggregation", "monthly");
        String dateRange = (String) filters.getOrDefault("dateRange", "");
        List<String> queues = (List<String>) filters.getOrDefault("queues", new ArrayList<>()); // Changed to "queues"

        // Handle loanAmountRange properly
        List<BigDecimal> loanAmountRange;
        Object loanAmountRangeObj = filters.get("loanAmountRange");
        if (loanAmountRangeObj instanceof List<?>) {
            loanAmountRange = ((List<?>) loanAmountRangeObj).stream()
                    .map(obj -> obj instanceof Number ? new BigDecimal(((Number) obj).toString()) : BigDecimal.ZERO)
                    .collect(Collectors.toList());
        } else {
            loanAmountRange = Arrays.asList(BigDecimal.ZERO, new BigDecimal("999999999999.99"));
        }

        String solId = (String) filters.getOrDefault("solId", "");
        String filterOption = (String) filters.getOrDefault("filterOption", "");

        logger.info("Processed loanAmountRange: " + loanAmountRange);

        Map<String, Object> data = new HashMap<>();
        data.put("kpis", getKPIs(queues, dateRange, loanAmountRange, aggregation));
        data.put("chartData", getChartData(aggregation, dateRange, queues, loanAmountRange, solId, filterOption));
        return data;
    }

    private Map<String, Object> getKPIs(List<String> queues, String dateRange, List<BigDecimal> loanAmountRange, String aggregation) {
        String dateFormat;
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("YYYY-MM-dd");

        // Set date formatting and range based on the aggregation type
        switch (aggregation) {
            case "daily" -> {
                dateFormat = "YYYY-MM-DD";
                startDate = LocalDate.now();  // Today's date
            }
            case "monthly" -> {
                dateFormat = "YYYY-MM";
                startDate = endDate.minusDays(30);  // Last 30 days from today
            }
            case "yearly" -> {
                dateFormat = "YYYY";
                if (endDate.getMonthValue() < 4) {
                    startDate = LocalDate.of(endDate.getYear() - 1, 4, 1);  // April 1 of the previous year
                } else {
                    startDate = LocalDate.of(endDate.getYear(), 4, 1);  // April 1 of the current year
                }
            }
            case "fully" -> {
                dateFormat = "YYYY";
                startDate = LocalDate.of(endDate.getYear()-4, 4, 1);  // April 1 of the current year

            }
            default -> dateFormat = "YYYY-MM";  // Default to monthly if no valid aggregation is specified
        }

        String groupBy = "TO_CHAR(vloan.CMDATE, '" + dateFormat + "')";
        StringBuilder sql = new StringBuilder("""
                    SELECT
                                    COUNT(*) as total_loans,
                                    SUM(CASE WHEN vloan.QUEUE = 'PD' AND vloan.ACTIVE_FLG = 'Y' THEN NVL(vdetails.SANC_AMOUNT_RECOMMENDED, 0) ELSE 0 END) as total_loan_amount,
                                    AVG(CASE WHEN vloan.QUEUE = 'PD' AND vloan.ACTIVE_FLG = 'Y' THEN NVL(vdetails.SANC_AMOUNT_RECOMMENDED, 0) ELSE 0 END) as avg_loan_amount,
                                    AVG(TAT.avg_tat) as avg_tat,
                                    SUM(CASE WHEN vloan.SAN_FLG = 'Y' AND vloan.ACTIVE_FLG = 'Y' THEN 1 ELSE 0 END) as sanctioned_loans,
                                    SUM(CASE WHEN vloan.QUEUE = 'PD' AND vloan.ACTIVE_FLG = 'Y' THEN 1 ELSE 0 END) as disbursed_loans
                                FROM VEHICLE_LOAN_MASTER vloan
                                JOIN VEHICLE_LOAN_ELIGIBILITY vdetails ON vloan.wi_num = vdetails.wi_num
                                JOIN (
                                    SELECT
                                        wi_num,
                                        AVG((COALESCE(tat.QUEUE_EXIT_DATE, SYSDATE) - tat.QUEUE_ENTRY_DATE) * 24) as avg_tat
                                    FROM VEHICLE_LOAN_TAT tat
                                    WHERE tat.DEL_FLG != 'Y'
                                    GROUP BY wi_num
                                ) tat ON vloan.wi_num = tat.wi_num
                                WHERE vloan.ACTIVE_FLG = 'Y'
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        // Apply filters
        if (!queues.isEmpty()) {
            sql.append(" AND vloan.QUEUE IN (:queues)");
            params.addValue("queues", queues);
            logger.info("QUEUES : " + queues);
        }

        // Use aggregation-based date range if no custom dateRange is specified
        if (dateRange.isEmpty() && startDate != null) {
            sql.append(" AND vloan.CMDATE BETWEEN to_date(:startDate,'YYYY-MM-DD') AND to_date(:endDate,'YYYY-MM-DD')");
            logger.info("if no custom dateRange is specified : " + startDate.format(df) + " >>>> " + endDate.format(df));
            params.addValue("startDate", startDate.format(df));
            params.addValue("endDate", endDate.format(df));
            logger.info("Aggregation Date Range : " + startDate + " to " + endDate);
        } else if (!dateRange.isEmpty()) {
            String[] dates = dateRange.split(" to ");
            if (dates.length == 2) {
                sql.append(" AND vloan.CMDATE BETWEEN to_date(:startDate,'YYYY-MM-DD') AND to_date(:endDate,'YYYY-MM-DD')");
                logger.info("Custom Date Range : " + dates[0].trim() + " >>>> " + dates[1].trim());
                params.addValue("startDate", dates[0].trim());
                params.addValue("endDate", dates[1].trim());
            }
        }

        if (loanAmountRange != null && loanAmountRange.size() == 2 && (!loanAmountRange.get(0).equals(loanAmountRange.get(1)))) {
            sql.append(" AND vdetails.SANC_AMOUNT_RECOMMENDED BETWEEN :minAmount AND :maxAmount");
            BigDecimal minAmount = loanAmountRange.get(0);
            BigDecimal maxAmount = loanAmountRange.get(1).min(new BigDecimal("999999999999.99")); // Set a reasonable upper limit
            logger.info("KPI Loan amount range: {} >>> {}", minAmount, maxAmount);
            params.addValue("minAmount", minAmount);
            params.addValue("maxAmount", maxAmount);
        }
        // Grouping based on aggregation
        if(!aggregation.equals("fully"))
            sql.append(" GROUP BY ").append(groupBy);

        try {
            List<Map<String, Object>> results = namedParameterJdbcTemplate.queryForList(sql.toString(), params);

            int totalLoans = 0;
            double totalLoanAmount = 0;
            double avgLoanAmount = 0;
            double avgTAT = 0;
            int sanctionedLoans = 0;
            int disbursedLoans = 0;

            int count = results.size();

            for (Map<String, Object> row : results) {
                totalLoans += ((Number) row.getOrDefault("total_loans", 0)).intValue();
                totalLoanAmount += ((Number) row.getOrDefault("total_loan_amount", 0)).doubleValue();
                avgLoanAmount += ((Number) row.getOrDefault("avg_loan_amount", 0)).doubleValue();
                avgTAT += ((Number) row.getOrDefault("avg_tat", 0)).doubleValue();
                sanctionedLoans += ((Number) row.getOrDefault("sanctioned_loans", 0)).intValue();
                disbursedLoans += ((Number) row.getOrDefault("disbursed_loans", 0)).intValue();
            }

            if (count > 0) {
                avgLoanAmount /= count;
                avgTAT /= count;
            }

            return Map.of(
                    "totalLoans", totalLoans,
                    "totalLoanAmount", totalLoanAmount,
                    "avgLoanAmount", avgLoanAmount,
                    "avgTAT", avgTAT,
                    "sanctionedLoans", sanctionedLoans,
                    "disbursedLoans", disbursedLoans
            );
        } catch (Exception e) {
            logger.error("Error fetching KPIs", e);
            return Map.of(
                    "totalLoans", 0,
                    "totalLoanAmount", 0.0,
                    "avgLoanAmount", 0.0,
                    "avgTAT", 0.0,
                    "sanctionedLoans", 0,
                    "disbursedLoans", 0
            );
        }
    }


    private Map<String, Object> getChartData(String aggregation, String dateRange, List<String> queues,
                                             List<BigDecimal> loanAmountRange, String solId, String filterOption) {
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("monthlyStats", getMonthlyStats(aggregation, dateRange, loanAmountRange));
        chartData.put("queueProcessing", getQueueProcessing(queues));
        chartData.put("loanStatus", getLoanStatus(filterOption));
        chartData.put("loanPerformance", getLoanPerformance(solId, dateRange));
        return chartData;
    }

    private Map<String, Object> getMonthlyStats(String aggregation, String dateRange, List<BigDecimal> loanAmountRange) {
        String groupBy;
        switch (aggregation) {
            case "daily" -> groupBy = "TO_CHAR(vloan.CMDATE, 'YYYY-MM-DD')";
            case "yearly" -> groupBy = "TO_CHAR(vloan.CMDATE, 'YYYY')";
            default -> groupBy = "TO_CHAR(vloan.CMDATE, 'YYYY-MM')";
        }

        StringBuilder sql = new StringBuilder("""
            SELECT %s AS period,
            COUNT(*) AS total_applications,
            SUM(CASE WHEN vloan.SAN_FLG = 'Y' THEN 1 ELSE 0 END) AS sanctioned_count,
            SUM(CASE WHEN vloan.DISB_FLG = 'SUCCESS' THEN 1 ELSE 0 END) AS disbursed_count,
            ROUND(SUM(NVL(vdetails.SANC_AMOUNT_RECOMMENDED, 0)) / 1000000, 2) AS sum_loan_sanc_amount,
            ROUND(SUM(CASE WHEN vloan.DISB_FLG = 'SUCCESS' THEN NVL(vdetails.SANC_AMOUNT_RECOMMENDED, 0) ELSE 0 END) / 1000000, 2) AS disbursed_amount
            FROM VEHICLE_LOAN_MASTER vloan
            JOIN VEHICLE_LOAN_ELIGIBILITY vdetails ON vloan.wi_num = vdetails.wi_num
            JOIN MIS_REPORT mis ON vloan.wi_num = mis.wi_num
        """.formatted(groupBy));

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (!dateRange.isEmpty()) {
            String[] dates = dateRange.split(" to ");
            if (dates.length == 2) {
                sql.append(" AND vloan.CMDATE BETWEEN :startDate AND :endDate");
                params.addValue("startDate", LocalDate.parse(dates[0].trim(), DateTimeFormatter.ISO_DATE));
                params.addValue("endDate", LocalDate.parse(dates[1].trim(), DateTimeFormatter.ISO_DATE));
            }
        }

        if (loanAmountRange != null && loanAmountRange.size() == 2) {
            sql.append(" AND vdetails.SANC_AMOUNT_RECOMMENDED BETWEEN :minAmount AND :maxAmount");
            BigDecimal minAmount = loanAmountRange.get(0);
            BigDecimal maxAmount = loanAmountRange.get(1).min(new BigDecimal("999999999999.99")); // Set a reasonable upper limit
            logger.info("Loan amount range: {} >>> {}", minAmount, maxAmount);
            params.addValue("minAmount", minAmount);
            params.addValue("maxAmount", maxAmount);
        }

        sql.append(" GROUP BY ").append(groupBy).append(" ORDER BY period");

        List<Map<String, Object>> results = namedParameterJdbcTemplate.queryForList(sql.toString(), params);

        List<Integer> totalApplications = new ArrayList<>();
        List<Integer> sanctionedCount = new ArrayList<>();
        List<Integer> disbursedCount = new ArrayList<>();
        List<Double> sanctionedAmount = new ArrayList<>();
        List<Double> disbursedAmount = new ArrayList<>();
        List<String> periods = new ArrayList<>();

        for (Map<String, Object> result : results) {
            periods.add((String) result.get("period"));
            totalApplications.add(((Number) result.getOrDefault("total_applications", 0)).intValue());
            sanctionedCount.add(((Number) result.getOrDefault("sanctioned_count", 0)).intValue());
            disbursedCount.add(((Number) result.getOrDefault("disbursed_count", 0)).intValue());
            sanctionedAmount.add(((Number) result.getOrDefault("sum_loan_sanc_amount", 0)).doubleValue());
            disbursedAmount.add(((Number) result.getOrDefault("disbursed_amount", 0)).doubleValue());
        }

        Map<String, Object> monthlyStats = new HashMap<>();
        monthlyStats.put("series", List.of(
                Map.of("name", "Total Applications", "data", totalApplications),
                Map.of("name", "Sanctioned Count", "data", sanctionedCount),
                Map.of("name", "Disbursed Count", "data", disbursedCount),
                Map.of("name", "Sanctioned Amount", "data", sanctionedAmount),
                Map.of("name", "Disbursed Amount", "data", disbursedAmount)
        ));
        monthlyStats.put("categories", periods);

        return monthlyStats;
    }

    private Map<String, Object> getQueueProcessing(List<String> queues) {
        StringBuilder sql = new StringBuilder("""
            SELECT misrct.CODEDESC AS queue_name,
            ROUND(AVG((COALESCE(tat.QUEUE_EXIT_DATE, SYSDATE) - tat.QUEUE_ENTRY_DATE) * 24), 2) AS avg_processing_time_hours
            FROM VEHICLE_LOAN_TAT tat
            JOIN misrct ON tat.QUEUE = misrct.CODEVALUE AND misrct.CODETYPE = 'QT'
            WHERE tat.DEL_FLG = 'N'
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (!queues.isEmpty()) {
            sql.append(" AND tat.QUEUE IN (:queues)");
            params.addValue("queues", queues);
        }

        sql.append(" GROUP BY misrct.CODEDESC ORDER BY avg_processing_time_hours DESC");

        List<Map<String, Object>> results = namedParameterJdbcTemplate.queryForList(sql.toString(), params);

        List<Double> avgProcessingTime = new ArrayList<>();
        List<String> queueNames = new ArrayList<>();

        for (Map<String, Object> result : results) {
            queueNames.add((String) result.get("queue_name"));
            avgProcessingTime.add(((Number) result.get("avg_processing_time_hours")).doubleValue());
        }

        Map<String, Object> queueProcessing = new HashMap<>();
        queueProcessing.put("series", List.of(Map.of("name", "Processing Time", "data", avgProcessingTime)));
        queueProcessing.put("categories", queueNames);

        return queueProcessing;
    }

    private Map<String, Object> getLoanStatus(String filterOption) {
        String sql = """
            SELECT status, COUNT(*) AS count
            FROM VEHICLE_LOAN_MASTER vloan
            WHERE vloan.CUST_NAME IS NOT NULL AND vloan.ACTIVE_FLG = 'Y'
            GROUP BY status
        """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        List<Integer> statusCounts = new ArrayList<>();
        List<String> statusLabels = new ArrayList<>();

        for (Map<String, Object> result : results) {
            statusLabels.add((String) result.get("status"));
            statusCounts.add(((Number) result.get("count")).intValue());
        }

        Map<String, Object> loanStatus = new HashMap<>();
        loanStatus.put("series", List.of(Map.of("name", "Loan Status", "data", statusCounts)));
        loanStatus.put("labels", statusLabels);

        return loanStatus;
    }

    private Map<String, Object> getLoanPerformance(String solId, String dateRange) {
        StringBuilder sql = new StringBuilder("""
            SELECT TO_CHAR(vloan.CMDATE, 'YYYY-MM') AS month,
            ROUND(SUM(NVL(vdetails.SANC_AMOUNT_RECOMMENDED, 0)) / 1000000, 2) AS sanctioned_amount,
            ROUND(SUM(CASE WHEN vloan.DISB_FLG = 'SUCCESS' THEN NVL(vdetails.SANC_AMOUNT_RECOMMENDED, 0) ELSE 0 END) / 1000000, 2) AS disbursed_amount
            FROM VEHICLE_LOAN_MASTER vloan
            JOIN VEHICLE_LOAN_ELIGIBILITY vdetails ON vloan.wi_num = vdetails.wi_num
            JOIN MIS_REPORT mis ON vloan.wi_num = mis.wi_num
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (solId != null && !solId.isEmpty()) {
            sql.append(" AND vloan.HOME_SOL = :solId");
            params.addValue("solId", solId);
        }

        if (!dateRange.isEmpty()) {
            String[] dates = dateRange.split(" to ");
            if (dates.length == 2) {
                sql.append(" AND vloan.CMDATE BETWEEN :startDate AND :endDate");
                params.addValue("startDate", LocalDate.parse(dates[0].trim(), DateTimeFormatter.ISO_DATE));
                params.addValue("endDate", LocalDate.parse(dates[1].trim(), DateTimeFormatter.ISO_DATE));
            }
        }

        sql.append(" GROUP BY TO_CHAR(vloan.CMDATE, 'YYYY-MM') ORDER BY month");

        List<Map<String, Object>> results = namedParameterJdbcTemplate.queryForList(sql.toString(), params);

        List<Double> sanctionedAmounts = new ArrayList<>();
        List<Double> disbursedAmounts = new ArrayList<>();
        List<String> months = new ArrayList<>();

        for (Map<String, Object> result : results) {
            months.add((String) result.get("month"));
            sanctionedAmounts.add(((Number) result.getOrDefault("sanctioned_amount", 0)).doubleValue());
            disbursedAmounts.add(((Number) result.getOrDefault("disbursed_amount", 0)).doubleValue());
        }

        Map<String, Object> loanPerformance = new HashMap<>();
        loanPerformance.put("series", List.of(
                Map.of("name", "Sanctioned Amount", "data", sanctionedAmounts),
                Map.of("name", "Disbursed Amount", "data", disbursedAmounts)
        ));
        loanPerformance.put("categories", months);

        return loanPerformance;
    }

    public Map<String, Object> getInitialDashboardData() {
        Map<String, Object> data = new HashMap<>();
        try {
            data.put("kpis", getKPIs(new ArrayList<>(), "", Arrays.asList(new BigDecimal(0.0), new BigDecimal(99999999999999.99)), "fully"));
            data.put("chartData", getChartData("monthly", "", new ArrayList<>(), Arrays.asList(new BigDecimal(0.0), new BigDecimal(99999999999999.99)), "", ""));
        } catch (Exception e) {
            logger.error("Error fetching initial dashboard data", e);
            data.put("kpis", new HashMap<>());
            data.put("chartData", new HashMap<>());
        }
        return data;
    }

    public List<Map<String, Object>> getExportData() {
        String sql = """
            SELECT vloan.wi_num, 
            NVL(vdetails.SANC_AMOUNT_RECOMMENDED, 0) AS SANC_AMOUNT_RECOMMENDED, 
            vloan.CMDATE, 
            home_sol.br_name as branch_name, 
            tat.QUEUE_ENTRY_DATE, 
            tat.QUEUE_EXIT_DATE
            FROM VEHICLE_LOAN_MASTER vloan
            JOIN VEHICLE_LOAN_ELIGIBILITY vdetails ON vloan.wi_num = vdetails.wi_num
            JOIN misbmt@mybank home_sol ON vloan.home_sol = home_sol.sol_id
            LEFT JOIN VEHICLE_LOAN_TAT tat ON vloan.wi_num = tat.wi_num
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> row = new HashMap<>();
            row.put("wi_num", rs.getString("wi_num"));
            row.put("SANC_AMOUNT_RECOMMENDED", rs.getDouble("SANC_AMOUNT_RECOMMENDED"));
            row.put("CMDATE", rs.getDate("CMDATE"));
            row.put("branch_name", rs.getString("branch_name"));
            row.put("QUEUE_ENTRY_DATE", rs.getDate("QUEUE_ENTRY_DATE"));
            row.put("QUEUE_EXIT_DATE", rs.getDate("QUEUE_EXIT_DATE"));
            return row;
        });
    }

    public List<Map<String, Object>> getDetailedData(String queue) {
        String sql = "SELECT * FROM VEHICLE_LOAN_MASTER WHERE QUEUE = ?";
        return jdbcTemplate.queryForList(sql, queue);
    }

    // Revised getQueueOptions method
    public List<Map<String, Object>> getQueueOptions() {
        String sql = """
            SELECT misrct.CODEVALUE AS codeValue, misrct.CODEDESC AS codeDesc
            FROM misrct 
            WHERE misrct.CODETYPE = 'QT'
            ORDER BY misrct.CODEDESC
        """;
        return jdbcTemplate.queryForList(sql);
    }

    // New methods for drill-down functionality
    public Map<String, Object> getDrillDownData(Map<String, Object> params) {
        String chartType = (String) params.get("chartType");
        if (chartType == null) {
            throw new IllegalArgumentException("chartType is required");
        }

        switch (chartType) {
            case "monthlyStats":
                return getMonthlyStatsDrillDownData(params);
            // Add cases for other chart types if needed
            default:
                throw new IllegalArgumentException("Unsupported chart type for drill-down: " + chartType);
        }
    }

    private Map<String, Object> getMonthlyStatsDrillDownData(Map<String, Object> params) {
        String period = (String) params.get("category");
        String seriesName = (String) params.get("seriesName");

        if (period == null || seriesName == null) {
            throw new IllegalArgumentException("Both 'category' and 'seriesName' are required for drill-down");
        }


        LocalDate startDate;
        LocalDate endDate;

        LocalDate currentDate = LocalDate.now();

        if (period.length() == 4) {
            // Yearly format
            int year = Integer.parseInt(period);

            // Financial year starts on 1st April and ends on 31st March
            if (currentDate.isBefore(LocalDate.of(year, 4, 1))) {
                // Current date is before 1st April of the given year
                // So the financial year is from 1st April of the previous year to 31st March of the given year
                startDate = LocalDate.of(year - 1, 4, 1);
                endDate = currentDate.isBefore(LocalDate.of(year, 3, 31)) ? currentDate : LocalDate.of(year, 3, 31);
            } else {
                // Current date is on or after 1st April of the given year
                // So the financial year is from 1st April of the given year to 31st March of the next year
                startDate = LocalDate.of(year, 4, 1);
                endDate = currentDate.isBefore(LocalDate.of(year + 1, 3, 31)) ? currentDate : LocalDate.of(year + 1, 3, 31);
            }
        } else if (period.length() == 7) {
            // Monthly format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            startDate = LocalDate.parse(period+"-01", formatter).withDayOfMonth(1);
            endDate = startDate.plusMonths(1).minusDays(1);

            // Ensure endDate does not exceed current date
            if (endDate.isAfter(currentDate)) {
                endDate = currentDate;
            }
        } else {
            // Daily format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            startDate = LocalDate.parse(period, formatter);
            endDate = startDate; // For daily format, start and end date are the same
        }


        String sql = ""; // SQL query to fetch the detailed data
        MapSqlParameterSource sqlParams = new MapSqlParameterSource();
        sqlParams.addValue("startDate", startDate);
        sqlParams.addValue("endDate", endDate);
        logger.info(startDate+" >>>> Dates >>>>"+endDate);
        switch (seriesName) {
            case "Total Applications":
                sql = """
                    SELECT vloan.wi_num, vloan.CMDATE, vloan.CUST_NAME, vdetails.SANC_AMOUNT_RECOMMENDED, home_sol.BR_NAME AS BRANCH_NAME
                    FROM VEHICLE_LOAN_MASTER vloan
                    JOIN VEHICLE_LOAN_ELIGIBILITY vdetails ON vloan.wi_num = vdetails.wi_num
                    JOIN misbmt@mybank home_sol ON vloan.HOME_SOL = home_sol.SOL_ID
                    JOIN MIS_REPORT mis ON vloan.wi_num = mis.wi_num
                    WHERE vloan.CMDATE BETWEEN :startDate AND :endDate
                """;
                break;
            case "Sanctioned Amount":
                sql = """
                    SELECT vloan.wi_num, vloan.CMDATE, vloan.CUST_NAME, sum(vdetails.SANC_AMOUNT_RECOMMENDED), home_sol.BR_NAME AS BRANCH_NAME
                    FROM VEHICLE_LOAN_MASTER vloan
                    JOIN VEHICLE_LOAN_ELIGIBILITY vdetails ON vloan.wi_num = vdetails.wi_num
                    JOIN MIS_REPORT mis ON vloan.wi_num = mis.wi_num
                    JOIN misbmt@mybank home_sol ON vloan.HOME_SOL = home_sol.SOL_ID
                    WHERE vloan.CMDATE BETWEEN :startDate AND :endDate
                    AND vloan.SAN_FLG = 'Y' GROUP BY vloan.wi_num, vloan.CMDATE, vloan.CUST_NAME, home_sol.BR_NAME
                """;
                break;
            case "Sanctioned Count":
                sql = """
                    SELECT vloan.wi_num, vloan.CMDATE, vloan.CUST_NAME, count(*), home_sol.BR_NAME AS BRANCH_NAME
                    FROM VEHICLE_LOAN_MASTER vloan
                    JOIN VEHICLE_LOAN_ELIGIBILITY vdetails ON vloan.wi_num = vdetails.wi_num
                    JOIN MIS_REPORT mis ON vloan.wi_num = mis.wi_num
                    JOIN misbmt@mybank home_sol ON vloan.HOME_SOL = home_sol.SOL_ID
                    WHERE vloan.CMDATE BETWEEN :startDate AND :endDate
                    AND vloan.SAN_FLG = 'Y' GROUP BY vloan.wi_num, vloan.CMDATE, vloan.CUST_NAME, home_sol.BR_NAME
                """;
                break;
            case "Disbursed Count":
                sql = """
                    SELECT vloan.wi_num, vloan.CMDATE, vloan.CUST_NAME, count(*), home_sol.BR_NAME AS BRANCH_NAME
                    FROM VEHICLE_LOAN_MASTER vloan
                    JOIN VEHICLE_LOAN_ELIGIBILITY vdetails ON vloan.wi_num = vdetails.wi_num
                    JOIN MIS_REPORT mis ON vloan.wi_num = mis.wi_num
                    JOIN misbmt@mybank home_sol ON vloan.HOME_SOL = home_sol.SOL_ID
                    WHERE vloan.CMDATE BETWEEN :startDate AND :endDate
                    AND vloan.DISB_FLG = 'SUCCESS' GROUP BY vloan.wi_num, vloan.CMDATE, vloan.CUST_NAME, home_sol.BR_NAME
                """;
                break;

            case "Disbursed Amount":
                sql = """
                    SELECT vloan.wi_num, vloan.CMDATE, vloan.CUST_NAME, sum(vdetails.DISBURSEDAMOUNT), home_sol.BR_NAME AS BRANCH_NAME
                    FROM VEHICLE_LOAN_MASTER vloan
                    JOIN VEHICLE_LOAN_ACCOUNT vdetails ON vloan.wi_num = vdetails.wi_num
                    JOIN MIS_REPORT mis ON vloan.wi_num = mis.wi_num
                    JOIN misbmt@mybank home_sol ON vloan.HOME_SOL = home_sol.SOL_ID
                    WHERE vloan.CMDATE BETWEEN :startDate AND :endDate
                    AND vloan.DISB_FLG = 'SUCCESS' GROUP BY vloan.wi_num, vloan.CMDATE, vloan.CUST_NAME, home_sol.BR_NAME
                """;
                break;
            default:
                throw new IllegalArgumentException("Unsupported seriesName for drill-down: " + seriesName);
        }

        List<Map<String, Object>> results = namedParameterJdbcTemplate.queryForList(sql, sqlParams);

        // Aggregate data by branch
        Map<String, Integer> branchCounts = new HashMap<>();

        for (Map<String, Object> row : results) {
            String branchName = (String) row.get("BRANCH_NAME");
            branchCounts.put(branchName, branchCounts.getOrDefault(branchName, 0) + 1);
        }

        // Prepare data for the chart
        List<String> categories = new ArrayList<>(branchCounts.keySet());
        List<Integer> dataValues = categories.stream().map(branchCounts::get).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("title", "Detailed Data for " + seriesName + " in " + period);
        response.put("yAxisTitle", "Number of Loans");
        response.put("series", List.of(Map.of("name", seriesName, "data", dataValues)));
        response.put("categories", categories);

        return response;
    }
}