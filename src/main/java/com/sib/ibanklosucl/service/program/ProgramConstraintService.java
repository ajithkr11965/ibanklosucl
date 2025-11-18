package com.sib.ibanklosucl.service.program;

import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanProgram;
import com.sib.ibanklosucl.repository.VehicleLoanApplicantRepository;
import com.sib.ibanklosucl.repository.VehicleLoanProgramRepository;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProgramConstraintService {
    @Autowired
    private VehicleLoanProgramRepository programRepository;

    @Autowired
    private VehicleLoanApplicantRepository applicantRepository;

    @Autowired
    private UserSessionData usd;

    /**
     * Get program constraints for the given work item and applicant
     *
     * @param wiNum              Work item number
     * @param currentApplicantId Current applicant ID
     * @return ProgramConstraint object with constraint details
     */
    public ProgramConstraint getProgramConstraint(String wiNum, Long currentApplicantId) {
        ProgramConstraint constraint = new ProgramConstraint();
        constraint.setHasConstraint(false);

        // Find existing programs with income considered for this work item
        List<VehicleLoanProgram> existingPrograms = programRepository.findByWiNumAndIncomeConsideredAndDelFlg(wiNum, "Y", "N");

        // Filter out the current applicant
        existingPrograms = existingPrograms.stream()
                .filter(p -> !p.getApplicantId().equals(currentApplicantId))
                .collect(Collectors.toList());

        if (!existingPrograms.isEmpty()) {
            boolean hasRtrApplicant = existingPrograms.stream()
                    .anyMatch(p -> "RTR".equals(p.getLoanProgram()));

            if (hasRtrApplicant) {
                // Get the RTR applicant details
                Optional<VehicleLoanProgram> rtrProgram = existingPrograms.stream()
                        .filter(p -> "RTR".equals(p.getLoanProgram()))
                        .findFirst();

                if (rtrProgram.isPresent()) {
                    constraint.setHasConstraint(true);
                    constraint.setRequiredProgram("NONE"); // Force NONE program
                    constraint.setRtrApplicantId(rtrProgram.get().getApplicantId());

                    // Get applicant name if available
                    String applicantName = getApplicantName(rtrProgram.get().getApplicantId());
                    String applicantInfo = applicantName != null ?
                            applicantName + " (ID: " + rtrProgram.get().getApplicantId() + ")" :
                            "ID: " + rtrProgram.get().getApplicantId();

                    constraint.setMessage("Another applicant (" + applicantInfo +
                            ") has selected RTR program. You must select Income considered = No.");
                }
            } else {
                // For non-RTR programs, all applicants with income considered must have the same program
                String existingProgram = existingPrograms.get(0).getLoanProgram();

                constraint.setHasConstraint(true);
                constraint.setRequiredProgram(existingProgram);

                // Get first applicant name if available
                String applicantName = getApplicantName(existingPrograms.get(0).getApplicantId());
                String applicantInfo = applicantName != null ?
                        applicantName + " (ID: " + existingPrograms.get(0).getApplicantId() + ")" :
                        "ID: " + existingPrograms.get(0).getApplicantId();

                constraint.setMessage("Applicant " + applicantInfo +
                        " has selected " + getProgramDisplayName(existingProgram) +
                        ". All applicants with income considered must use the same program.");
            }
        }

        return constraint;
    }

    /**
     * Validate program selection against constraints
     *
     * @param wiNum            Work item number
     * @param applicantId      Applicant ID
     * @param incomeConsidered Whether income is considered (Y/N)
     * @param programCode      Selected program code
     * @return ValidationResult with validation status and message
     */
    public ValidationResult validateProgramSelection(String wiNum, Long applicantId,
                                                     String incomeConsidered, String programCode) {
        ValidationResult result = new ValidationResult();
        result.setValid(true);

        // If income is not considered, no validation needed
        if (!"Y".equals(incomeConsidered)) {
            return result;
        }

        ProgramConstraint constraint = getProgramConstraint(wiNum, applicantId);

        if (constraint.isHasConstraint()) {
            if ("RTR".equals(constraint.getRequiredProgram()) && !"RTR".equals(programCode)) {
                result.setValid(false);
                result.setMessage("All applicants with income considered must select RTR program.");
                result.setRequiredProgram("RTR");
            } else if ("NONE".equals(constraint.getRequiredProgram()) && !"NONE".equals(programCode)) {
                result.setValid(false);
                result.setMessage(constraint.getMessage());
                result.setRequiredProgram("NONE");
            } else if (!constraint.getRequiredProgram().equals(programCode)) {
                result.setValid(false);
                result.setMessage("All applicants with income considered must select the same program: " +
                        getProgramDisplayName(constraint.getRequiredProgram()));
                result.setRequiredProgram(constraint.getRequiredProgram());
            }
        }

        // Check if current applicant is selecting RTR - enforce constraints on other applicants
        if ("RTR".equals(programCode)) {
            // Find other applicants with income considered Y and program not RTR
            List<VehicleLoanProgram> otherPrograms = programRepository.findByWiNumAndIncomeConsideredAndDelFlg(wiNum, "Y", "N");
            otherPrograms = otherPrograms.stream()
                    .filter(p -> !p.getApplicantId().equals(applicantId) && !"RTR".equals(p.getLoanProgram()))
                    .collect(Collectors.toList());

            if (!otherPrograms.isEmpty()) {
                result.setValid(false);

                // Build message about conflicting applicants
                StringBuilder message = new StringBuilder("Cannot select RTR program because the following applicants " +
                        "already have different programs selected with income considered:");

                message.append("<ul>");
                for (VehicleLoanProgram program : otherPrograms) {
                    String applicantName = getApplicantName(program.getApplicantId());
                    String applicantInfo = applicantName != null ?
                            applicantName + " (ID: " + program.getApplicantId() + ")" :
                            "ID: " + program.getApplicantId();

                    message.append("<li>").append(applicantInfo)
                            .append(" - ").append(getProgramDisplayName(program.getLoanProgram()))
                            .append("</li>");
                }
                message.append("</ul>");

                message.append("These applicants must change their selection to 'Income considered = No' before you can select RTR.");

                result.setMessage(message.toString());
            }
        }

        return result;
    }

    /**
     * Get friendly display name for program code
     */
    private String getProgramDisplayName(String programCode) {
        switch (programCode) {
            case "INCOME":
                return "Income Program";
            case "SURROGATE":
                return "Surrogate Program";
            case "RTR":
                return "RTR Program";
            case "IMPUTEDINCOME":
                return "Imputed Income Program";
            case "LOANFD":
                return "Loan FD Program";
            case "60/40":
                return "60/40 Program";
            case "NONFOIR":
                return "NON-FOIR Program";
            case "NONE":
                return "No Program";
            default:
                return programCode;
        }
    }

    /**
     * Get applicant name by ID
     */
    private String getApplicantName(Long applicantId) {
        try {
            Optional<VehicleLoanApplicant> applicant = applicantRepository.findById(applicantId);
            return applicant.map(VehicleLoanApplicant::getApplName).orElse(null);
        } catch (Exception e) {
            log.warn("Error fetching applicant name for ID {}: {}", applicantId, e.getMessage());
            return null;
        }
    }

    @Getter
    @Setter
    public static class ValidationResult {
        private boolean valid;
        private String message;
        private String requiredProgram;
    }

    @Getter
    @Setter
    public static class ProgramConstraint {
        private boolean hasConstraint;
        private String requiredProgram;
        private Long rtrApplicantId;
        private String message;
    }
}
