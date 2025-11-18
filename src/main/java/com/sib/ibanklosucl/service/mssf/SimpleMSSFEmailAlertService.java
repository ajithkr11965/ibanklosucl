package com.sib.ibanklosucl.service.mssf;

import com.sib.ibanklosucl.dto.doc.SMSEmailDTO;
import com.sib.ibanklosucl.dto.mssf.MSSFCustomerSummaryDTO;
import com.sib.ibanklosucl.repository.mssf.MSSFCustomerRepository;
import com.sib.ibanklosucl.service.email.WarningMailService;
import com.sib.ibanklosucl.service.integration.SMSEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class SimpleMSSFEmailAlertService {

    @Autowired
    private MSSFCustomerRepository mssfCustomerRepository;

    @Autowired
    private SMSEmailService smsEmailService;
    @Autowired
    private WarningMailService warningMailService;


    @Value("${mssf.email.from:noreply@sib.bank.in}")
    private String fromEmail;

    @Value("${mssf.email.enabled:true}")
    private boolean emailEnabled;
    @Value("${app.dev-mode:true}")
    private boolean devMode;

    /**
     * Generate branch email from SOL ID: br + solId + @sib.bank.in
     * Example: SOL001 -> br001@sib.bank.in
     */
    private String getBranchEmail(String solId) {
        // Remove "SOL" prefix if present and use only the number part
        String solNumber = solId.replace("SOL", "");
        return "br" + solNumber + "@sib.bank.in";
    }

    /**
     * Generate manager CC email from SOL ID: manager + solId + @sib.bank.in
     * Example: SOL001 -> manager001@sib.bank.in
     */
    private String getManagerEmail(String solId) {
        String solNumber = solId.replace("SOL", "");
        return "manager" + solNumber + "@sib.bank.in";
    }

    /**
     * Get list of active SOL IDs from MSSFDealerSol table
     */
    private List<String> getActiveSolIds() {
        return mssfCustomerRepository.findActiveSolIds();
    }

    /**
     * Send alerts for new MSSF leads generated in the last 4 hours
     */
    public void sendNewLeadAlerts() {
        if (!emailEnabled) {
            log.info("MSSF email alerts are disabled");
            return;
        }

        log.debug("Starting MSSF new lead alerts");
        LocalDateTime fromTime = LocalDateTime.now().minusHours(4); // Last 4 hours

        List<String> activeSolIds = getActiveSolIds();
        log.debug("Found {} active SOL IDs for new lead alerts", activeSolIds.size());

        for (String solId : activeSolIds) {
            try {
                List<MSSFCustomerSummaryDTO> newLeads =
                        mssfCustomerRepository.findNewLeadsBySolAndDate(solId, fromTime);

                if (!newLeads.isEmpty()) {
                    String branchEmail = getBranchEmail(solId);
                    String ccEmail = "";
                    if (devMode) {
                        ccEmail = "lincevarghese@sib.bank.in;jayakrishnan.u@sib.bank.in";
                    }
                    sendNewLeadEmail(solId, branchEmail, ccEmail, newLeads);
                    log.debug("New lead alert sent for {} to {} with {} leads", solId, branchEmail, newLeads.size());
                }

            } catch (Exception e) {
                log.error("Error sending new lead alert for {}, Error: {}", solId, e.getMessage());
            }
        }
    }

    /**
     * Send reminders for applications pending more than 3 days
     */
    public void sendPendingReminders() {
        if (!emailEnabled) {
            return;
        }

        log.info("Starting MSSF pending reminders");
        LocalDateTime beforeDate = LocalDateTime.now().minusDays(3); // Pending more than 3 days

        List<String> activeSolIds = getActiveSolIds();
        log.info("Found {} active SOL IDs for pending reminders", activeSolIds.size());

        for (String solId : activeSolIds) {
            try {
                List<MSSFCustomerSummaryDTO> pendingApps =
                        mssfCustomerRepository.findPendingApplicationsBySolOlderThan(solId, beforeDate);

                if (pendingApps.size() >= 5) { // Only send if 5 or more pending
                    String branchEmail = getBranchEmail(solId);
                    String ccEmail = "albgcentralteam@sib.bank.in;rsmalgroup@sib.bank.in";
                    if (devMode) {
                        ccEmail = "lincevarghese@sib.bank.in;jayakrishnan.u@sib.bank.in";
                    }

                    sendPendingReminderEmail(solId, branchEmail, ccEmail, pendingApps);
                    log.info("Pending reminder sent for {} to {} with {} pending applications", solId, branchEmail, pendingApps.size());
                }

            } catch (Exception e) {
                log.error("Error sending pending reminder for {}, Error: {}", solId, e.getMessage());
            }
        }
    }

    /**
     * Send urgent alerts for applications pending more than 7 days
     */
    public void sendUrgentAlerts() {
        if (!emailEnabled) {
            return;
        }

        log.info("Starting MSSF urgent alerts");
        LocalDateTime urgentDate = LocalDateTime.now().minusDays(7); // Pending more than 7 days

        List<String> activeSolIds = getActiveSolIds();
        log.info("Found {} active SOL IDs for urgent alerts", activeSolIds.size());

        for (String solId : activeSolIds) {
            try {
                List<MSSFCustomerSummaryDTO> urgentApps =
                        mssfCustomerRepository.findPendingApplicationsBySolOlderThan(solId, urgentDate);

                if (!urgentApps.isEmpty()) { // Send for any urgent applications
                    String branchEmail = getBranchEmail(solId);
                    String managerEmail = getManagerEmail(solId);

                    sendUrgentAlertEmail(solId, branchEmail, managerEmail, urgentApps);
                    log.info("Urgent alert sent for {} to {} with {} urgent applications", solId, branchEmail, urgentApps.size());
                }

            } catch (Exception e) {
                log.error("Error sending urgent alert for {}, Error: {}", solId, e.getMessage());
            }
        }
    }

    private void sendNewLeadEmail(String solId, String toEmail, String ccEmail, List<MSSFCustomerSummaryDTO> newLeads) {
        String subject = String.format("🆕 New MSSF Leads - %s (%d Applications)", solId, newLeads.size());
        String body = generateNewLeadEmailBody(solId, newLeads);

        sendEmail("MSSF_NEW_LEAD", toEmail, ccEmail, subject, body);
    }

    private void sendPendingReminderEmail(String solId, String toEmail, String ccEmail, List<MSSFCustomerSummaryDTO> pendingApps) {
        String subject = String.format("⏰ MSSF Pending Applications - %s (%d Pending)", solId, pendingApps.size());
        String body = generatePendingReminderEmailBody(solId, pendingApps);

        sendEmail("MSSF_PENDING", toEmail, ccEmail, subject, body);
    }

    private void sendUrgentAlertEmail(String solId, String toEmail, String ccEmail, List<MSSFCustomerSummaryDTO> urgentApps) {
        String subject = String.format("🚨 URGENT: MSSF Applications >7 Days - %s (%d Applications)", solId, urgentApps.size());
        String body = generateUrgentAlertEmailBody(solId, urgentApps);

        sendEmail("MSSF_URGENT", toEmail, ccEmail, subject, body);
    }

    private void sendEmail(String alertId, String toEmail, String ccEmail, String subject, String body) {
        if (devMode) {
            String devOverrideEmails = "ajithkr@sib.bank.in";
            log.info("🔧 TEST MODE: Overriding email addresses");
            log.info("Original TO: {} -> Override TO: {}", toEmail, devOverrideEmails);
            toEmail = devOverrideEmails;
            ccEmail = ""; // Clear CC in test mode
        }

        SMSEmailDTO emailDTO = new SMSEmailDTO();
        emailDTO.setSlno(System.currentTimeMillis());
        emailDTO.setWiNum("MSSF");
        emailDTO.setSlno(Long.valueOf("10000"));
        emailDTO.setReqType("E");
        emailDTO.setAlertId(alertId);
        emailDTO.setEmailFrom(fromEmail);
        emailDTO.setEmailTo(toEmail);
        emailDTO.setEmailCc(ccEmail);
        emailDTO.setEmailSubject(subject);
        emailDTO.setEmailBody(body);
        emailDTO.setSentUser("BATCH_JOB");
        emailDTO.setAppid(100L);

        smsEmailService.insertSMSEmail(emailDTO);
    }

    private String generateNewLeadEmailBody(String solId, List<MSSFCustomerSummaryDTO> newLeads) {
        String title = String.format("🆕 New MSSF Leads Available - %s", solId);

        StringBuilder content = new StringBuilder();
        content.append("<p>We are pleased to inform you that <b>").append(newLeads.size())
                .append(" new MSSF loan applications</b> have been received for your branch.</p>");

        content.append("<p><b>Alert Details:</b></p>");
        content.append("<ul>");
        content.append("<li><b>SOL ID:</b> ").append(solId).append("</li>");
        content.append("<li><b>New Applications:</b> ").append(newLeads.size()).append("</li>");
        content.append("<li><b>Generated:</b> ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).append("</li>");
        content.append("</ul>");

        content.append("<table class='data-table'>");
        content.append("<tr><th>Ref No</th><th>Customer Name</th><th>Mobile</th><th>Loan Amount</th><th>Dealer Code</th><th>Dealer Name & Location</th><th>Applied Date</th></tr>");

        int maxRecords = Math.min(newLeads.size(), 15); // Show up to 15 records
        for (int i = 0; i < maxRecords; i++) {
            MSSFCustomerSummaryDTO lead = newLeads.get(i);
            content.append("<tr>");
            content.append("<td><b>").append(lead.getRefNo()).append("</b></td>");
            content.append("<td>").append(lead.getCustomerName()).append("</td>");
            content.append("<td>").append(lead.getMobile()).append("</td>");
            content.append("<td>₹ ").append(String.format("%,.2f", lead.getLoanAmount())).append("</td>");
            content.append("<td>").append(lead.getDealerCode()).append("</td>");
            content.append("<td>").append(lead.getDealerNameAndLocationSafe()).append("</td>");
            content.append("<td>").append(lead.getCreatedDate().format(DateTimeFormatter.ofPattern("dd-MM HH:mm"))).append("</td>");
            content.append("</tr>");
        }

        if (newLeads.size() > 15) {
            content.append("<tr><td colspan='7' style='text-align: center; font-style: italic;'>")
                    .append("... and ").append(newLeads.size() - 15).append(" more applications</td></tr>");
        }
        content.append("</table>");

        String actionItems = """
                <li>Login to the MSSF system to review these applications</li>
                <li>Contact customers within 24 hours for better conversion rates</li>
                <li>Update application status after initial assessment</li>
                <li>Ensure all required documents are collected promptly</li>
                <li>Coordinate with respective dealers for document verification</li>
                """;

        return warningMailService.generateMSSFBranchEmailBody("NEW_LEAD", title, content.toString(), actionItems);
    }

    private String generatePendingReminderEmailBody(String solId, List<MSSFCustomerSummaryDTO> pendingApps) {
        String title = String.format("⏰ MSSF Pending Applications Reminder - %s", solId);

        StringBuilder content = new StringBuilder();
        content.append("<p>This is a reminder that <b>").append(pendingApps.size())
                .append(" MSSF loan applications</b> are currently pending processing for more than 3 days.</p>");

        content.append("<p><b>Reminder Details:</b></p>");
        content.append("<ul>");
        content.append("<li><b>SOL ID:</b> ").append(solId).append("</li>");
        content.append("<li><b>Pending Applications:</b> ").append(pendingApps.size()).append("</li>");
        content.append("<li><b>Threshold:</b> Pending > 3 days</li>");
        content.append("<li><b>Generated:</b> ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).append("</li>");
        content.append("</ul>");

        content.append("<p style='color: #f57600;'><b>⚠️ These applications require immediate attention to maintain service standards.</b></p>");

        content.append("<table class='data-table'>");
        content.append("<tr><th>Ref No</th><th>Customer Name</th><th>Mobile</th><th>Loan Amount</th><th>Days Pending</th><th>Dealer Code</th><th>Dealer Name & Location</th><th>Status</th></tr>");

        for (MSSFCustomerSummaryDTO app : pendingApps) {
            String rowClass = app.getDaysSinceCreated() > 7 ? "urgent" : "highlight";
            content.append("<tr class='").append(rowClass).append("'>");
            content.append("<td><b>").append(app.getRefNo()).append("</b></td>");
            content.append("<td>").append(app.getCustomerName()).append("</td>");
            content.append("<td>").append(app.getMobile()).append("</td>");
            content.append("<td>₹ ").append(String.format("%,.2f", app.getLoanAmount())).append("</td>");
            content.append("<td><b>").append(app.getDaysSinceCreated()).append(" days</b></td>");
            content.append("<td>").append(app.getDealerCode()).append("</td>");
            content.append("<td>").append(app.getDealerNameAndLocationSafe()).append("</td>");
            content.append("<td>Pending Review</td>");
            content.append("</tr>");
        }
        content.append("</table>");

        String actionItems = """
                <li><b>Review and process these applications immediately</b></li>
                <li>Contact customers to provide status updates</li>
                <li>Contact respective dealers for any missing documents or clarifications</li>
                <li>Collect any missing documents or information</li>
                <li>Escalate to branch manager if additional approvals are needed</li>
                <li>Update application status in the system after processing</li>
                """;

        return warningMailService.generateMSSFBranchEmailBody("PENDING", title, content.toString(), actionItems);
    }

    private String generateUrgentAlertEmailBody(String solId, List<MSSFCustomerSummaryDTO> urgentApps) {
        String title = String.format("🚨 URGENT: MSSF Applications Pending >7 Days - %s", solId);

        StringBuilder content = new StringBuilder();
        content.append("<p style='color: #d32f2f;'><b>URGENT ATTENTION REQUIRED:</b> ")
                .append(urgentApps.size()).append(" MSSF loan applications have been pending for more than 7 days.</p>");

        content.append("<p><b>Critical Alert Details:</b></p>");
        content.append("<ul>");
        content.append("<li><b>SOL ID:</b> ").append(solId).append("</li>");
        content.append("<li><b>Urgent Applications:</b> ").append(urgentApps.size()).append("</li>");
        content.append("<li><b>Threshold:</b> <span style='color: #d32f2f;'>Pending > 7 days</span></li>");
        content.append("<li><b>Generated:</b> ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).append("</li>");
        content.append("</ul>");

        content.append("<div style='background-color: #ffcdd2; padding: 15px; border-left: 5px solid #d32f2f; margin: 15px 0;'>");
        content.append("<p style='margin: 0; color: #d32f2f;'><b>⚠️ IMMEDIATE ACTION REQUIRED</b></p>");
        content.append("<p style='margin: 5px 0 0 0;'>These applications have exceeded the acceptable processing time and require immediate attention to maintain customer satisfaction and regulatory compliance.</p>");
        content.append("</div>");

        content.append("<table class='data-table'>");
        content.append("<tr><th>Ref No</th><th>Customer Name</th><th>Mobile</th><th>Loan Amount</th><th>Days Pending</th><th>Priority</th></tr>");

        for (MSSFCustomerSummaryDTO app : urgentApps) {
            String priority = app.getDaysSinceCreated() > 14 ? "CRITICAL" : "HIGH";
            String priorityColor = app.getDaysSinceCreated() > 14 ? "#d32f2f" : "#f57600";

            content.append("<tr class='urgent'>");
            content.append("<td><b>").append(app.getRefNo()).append("</b></td>");
            content.append("<td>").append(app.getCustomerName()).append("</td>");
            content.append("<td>").append(app.getMobile()).append("</td>");
            content.append("<td>₹ ").append(String.format("%,.2f", app.getLoanAmount())).append("</td>");
            content.append("<td style='color: #d32f2f;'><b>").append(app.getDaysSinceCreated()).append(" days</b></td>");
            content.append("<td style='color: ").append(priorityColor).append(";'><b>").append(priority).append("</b></td>");
            content.append("</tr>");
        }
        content.append("</table>");

        String actionItems = """
                <li><b style='color: #d32f2f;'>IMMEDIATE REVIEW REQUIRED - Process these applications today</b></li>
                <li><b>Customer Communication:</b> Contact customers immediately with status updates</li>
                <li><b>Escalation:</b> If approvals are pending, escalate to branch manager immediately</li>
                <li><b>Documentation:</b> Update system with current status and next steps</li>
                <li><b>Follow-up:</b> Schedule immediate follow-up actions for tomorrow</li>
                <li><b>Reporting:</b> Inform regional office of any systemic delays</li>
                """;

        return warningMailService.generateMSSFBranchEmailBody("URGENT", title, content.toString(), actionItems);
    }


}

