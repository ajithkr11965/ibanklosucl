package com.sib.ibanklosucl.scheduler.mssf;

import com.sib.ibanklosucl.service.mssf.SimpleMSSFEmailAlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
@ConditionalOnProperty(name = "mssf.email.enabled", havingValue = "true", matchIfMissing = true)
public class SimpleMSSFEmailScheduler {

    @Autowired
    private SimpleMSSFEmailAlertService emailAlertService;

    /**
     * NEW LEAD ALERTS BATCH JOB
     * Runs every 4 hours during business hours (9 AM, 1 PM, 5 PM) on weekdays
     * Checks for new MSSF leads generated in the last 4 hours
     */
    @Scheduled(cron = "0 0 10,14,18 * * MON-SAT")
    public void newLeadAlertsBatchJob() {
        log.info("=== STARTING MSSF NEW LEAD ALERTS BATCH JOB ===");
        try {
            emailAlertService.sendNewLeadAlerts();
            log.info("=== MSSF NEW LEAD ALERTS BATCH JOB COMPLETED SUCCESSFULLY ===");
        } catch (Exception e) {
            log.error("=== MSSF NEW LEAD ALERTS BATCH JOB FAILED ===", e);
        }
    }

    /**
     * PENDING REMINDERS BATCH JOB
     * Runs daily at 10 AM on weekdays
     * Checks for applications pending more than 3 days with 5+ applications threshold
     */
    @Scheduled(cron = "0 0 10 * * MON-SAT")
    public void pendingRemindersBatchJob() {
        log.info("=== STARTING MSSF PENDING REMINDERS BATCH JOB ===");
        try {
            emailAlertService.sendPendingReminders();
            log.info("=== MSSF PENDING REMINDERS BATCH JOB COMPLETED SUCCESSFULLY ===");
        } catch (Exception e) {
            log.error("=== MSSF PENDING REMINDERS BATCH JOB FAILED ===", e);
        }
    }

    /**
     * URGENT ALERTS BATCH JOB
     * Runs twice daily at 10 AM and 3 PM on weekdays
     * Checks for applications pending more than 7 days (any count triggers alert)
     */
//    @Scheduled(cron = "0 0 10,15 * * MON-SAT")
//    public void urgentAlertsBatchJob() {
//        log.info("=== STARTING MSSF URGENT ALERTS BATCH JOB ===");
//        try {
//            emailAlertService.sendUrgentAlerts();
//            log.info("=== MSSF URGENT ALERTS BATCH JOB COMPLETED SUCCESSFULLY ===");
//        } catch (Exception e) {
//            log.error("=== MSSF URGENT ALERTS BATCH JOB FAILED ===", e);
//        }
//    }

    /**
     * MANUAL TEST BATCH JOB (FOR TESTING ONLY)
     * Runs every 5 minutes when test mode is enabled
     * Enable by setting: mssf.email.test.mode=true
     */
//    @Scheduled(cron = "0 */5 * * * *")
//    @ConditionalOnProperty(name = "mssf.email.test.mode", havingValue = "true")
//    public void testBatchJob() {
//        log.info("=== RUNNING MSSF EMAIL ALERTS IN TEST MODE ===");
//        try {
//            emailAlertService.sendNewLeadAlerts();
//            emailAlertService.sendPendingReminders();
//            emailAlertService.sendUrgentAlerts();
//            log.info("=== TEST MSSF EMAIL ALERTS COMPLETED ===");
//        } catch (Exception e) {
//            log.error("=== TEST MSSF EMAIL ALERTS FAILED ===", e);
//        }
//    }

    /**
     * WEEKLY SUMMARY BATCH JOB
     * Runs every Monday at 9 AM
     * Can be extended to send weekly summary reports
     */
//    @Scheduled(cron = "0 0 9 * * MON")
//    public void weeklySummaryBatchJob() {
//        log.info("=== STARTING MSSF WEEKLY SUMMARY BATCH JOB ===");
//        try {
//            // Future implementation: weekly summary
//            log.info("Weekly summary batch job placeholder - implement as needed");
//            log.info("=== MSSF WEEKLY SUMMARY BATCH JOB COMPLETED ===");
//        } catch (Exception e) {
//            log.error("=== MSSF WEEKLY SUMMARY BATCH JOB FAILED ===", e);
//        }
//    }

    /**
     * HEALTH CHECK BATCH JOB
     * Runs every hour to log system health
     * Helps monitor if the scheduler is working
     */
    @Scheduled(cron = "0 0 * * * *")
    @ConditionalOnProperty(name = "mssf.email.health.check", havingValue = "true", matchIfMissing = false)
    public void healthCheckBatchJob() {
        log.info("MSSF Email Alert Scheduler Health Check - System is running");
    }
        /**
     * DEVELOPMENT TEST BATCH JOB
     * Runs every 2 minutes when test mode is enabled
     * Enable by setting: mssf.email.test.mode=true
     */
//    @Scheduled(cron = "0 */2 * * * *")
//    @ConditionalOnProperty(name = "mssf.email.test.mode", havingValue = "true")
//    public void developmentTestBatchJob() {
//        log.info("========================================");
//        log.info("=== DEVELOPMENT TEST MODE - RUNNING MSSF EMAIL ALERTS ===");
//        log.info("========================================");
//
//        try {
//            log.info("Testing new lead alerts...");
//            emailAlertService.sendNewLeadAlerts();
//
//            Thread.sleep(2000); // 2 second delay between tests
//
//            log.info("Testing pending reminders...");
//            emailAlertService.sendPendingReminders();
//
//            Thread.sleep(2000);
//
//            log.info("=== DEVELOPMENT TEST COMPLETED SUCCESSFULLY ===");
//
//        } catch (Exception e) {
//            log.error("=== DEVELOPMENT TEST FAILED ===", e);
//        }
//    }

    /**
     * MANUAL TRIGGER TEST - Runs every 10 seconds for immediate testing
     * Enable by setting: mssf.email.manual.test=true
     */
//    @Scheduled(cron = "*/10 * * * * *")
//    @ConditionalOnProperty(name = "mssf.email.manual.test", havingValue = "true")
//    public void manualTestBatchJob() {
//        log.info("🔧 MANUAL TEST - Running MSSF email alerts every 10 seconds");
//        try {
//            //emailAlertService.sendNewLeadAlerts();
//            //emailAlertService.sendPendingReminders();
//            //emailAlertService.sendUrgentAlerts();
//        } catch (Exception e) {
//            log.error("Manual test failed", e);
//        }
//    }

}

