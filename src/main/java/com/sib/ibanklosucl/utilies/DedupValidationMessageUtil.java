package com.sib.ibanklosucl.utilies;

import com.sib.ibanklosucl.dto.DedupValidationResultDTO;

public class DedupValidationMessageUtil {
//    public static String generateErrorMessage(DedupValidationResultDTO validationResult) {
//        StringBuilder message = new StringBuilder("Validation failed. The following checks are pending:\n");
//
//        for (DedupValidationResultDTO.ApplicantDedupStatus status : validationResult.getApplicantStatuses()) {
//            if (!status.isFinacleDedupDone() || !status.isLosDedupDone()) {
//                message.append("<li>Applicant: ").append(status.getApplicantName()).append(" (ID: ").append(status.getApplicantId()).append(")</li>");
//                if (!status.isFinacleDedupDone()) {
//                    message.append(" <ol><li> - Finacle Dedup check is pending</li>");
//                }
//                if (!status.isLosDedupDone()) {
//                    message.append("  <li>- LOS Dedup check is pending</li>");
//                }
//                message.append("</ol>");
//            }
//        }
//
//        message.append("Please complete the pending checks before processing the loan.");
//        return message.toString();
//    }
   public static String generateErrorMessage(DedupValidationResultDTO validationResult) {
        StringBuilder message = new StringBuilder("<div class='dedup-error-message'>");
        message.append("<h6>Validation failed. The following checks are pending:</h6>");
        message.append("<ul class='applicant-list'>");

        for (DedupValidationResultDTO.ApplicantDedupStatus status : validationResult.getApplicantStatuses()) {
            if (!status.isFinacleDedupDone() || !status.isLosDedupDone()) {
                message.append("<li class='applicant-item'>");
                message.append("<span class='applicant-name'>").append(status.getApplicantName())
                       .append("</span>");
                message.append("<ul class='pending-checks'>");
                if (!status.isFinacleDedupDone()) {
                    message.append("<li>Finacle Dedup check is pending in Basic Details</li>");
                }
                if (!status.isLosDedupDone()) {
                    message.append("<li>LOS Dedup check is pending in Basic Details</li>");
                }
                message.append("</ul>");
                message.append("</li>");
            }
        }

        message.append("</ul>");
        message.append("<p class='error-footer'>Please complete the pending checks before processing the loan.</p>");
        message.append("</div>");

        return message.toString();
    }
}
