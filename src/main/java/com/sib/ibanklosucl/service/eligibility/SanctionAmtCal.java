package com.sib.ibanklosucl.service.eligibility;
import com.sib.ibanklosucl.dto.FoirDTO;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanProgram;
import com.sib.ibanklosucl.repository.VLCreditrepository;
import com.sib.ibanklosucl.repository.VehicleLoanApplicantRepository;
import com.sib.ibanklosucl.repository.VehicleLoanProgramRepository;
import com.sib.ibanklosucl.service.VehicleLoanDetailsService;
import com.sib.ibanklosucl.service.iBankService;
import com.sib.ibanklosucl.service.VehicleLoanProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SanctionAmtCal {

    private static final String RESIDENT_FLAG = "R";
    private static final String ABB_DOC_TYPE = "OVERSEASABB";

    private static final String MONTHLY_DOC_TYPE = "MONTHLY";
    private static final String INCOME_CONSIDERED_FLAG = "Y";
    private static final String NON_FOIR_FLAG = "N";
    private static final String ACTIVE_FLAG = "N";

    private final VehicleLoanApplicantRepository applicantRepository;
    private final VLCreditrepository financialRepository;
    private final VehicleLoanProgramRepository programRepository;
    private final VehicleLoanDetailsService loanDetailsService;
    private final iBankService iBankService;
    private final VehicleLoanProgramService vehicleLoanProgramService;

    //	EMI formula = Sanction amount * [
    //(ROI/12)[(1+ROI/12)^tenor/{(1+ROI/12)^tenor}-1]
    //]
    //E = (P.r.(1+r)^n) / ((1+r)^n – 1) --> r=ROI/12,n in months
    //P = E.((1+r)^n – 1) / r.(1+r)^n)
    public BigDecimal calculateSanctionAmount(BigDecimal emi, BigDecimal roi, int tenor) {
        try {
            log.debug("Calculating sanction amount with EMI: {}, ROI: {}, Tenor: {}", emi, roi, tenor);
            BigDecimal monthlyRate = roi.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
            BigDecimal onePlusRatePowTenor = BigDecimal.ONE.add(monthlyRate).pow(tenor);
            BigDecimal numerator = emi.multiply(onePlusRatePowTenor.subtract(BigDecimal.ONE));
            BigDecimal denominator =onePlusRatePowTenor.multiply(monthlyRate);
            if (denominator.compareTo(BigDecimal.ZERO) == 0) {
                log.error("Attempted division by zero in sanction amount calculation with ROI: {} and Tenor: {}", roi, tenor);
                throw new ArithmeticException("Division by zero in calculation.");
            }
            BigDecimal sanctionAmount = numerator.divide(denominator, 2, RoundingMode.FLOOR);
            log.info("Sanction amount calculated successfully: {}", sanctionAmount);
            return sanctionAmount;
        } catch (Exception ex) {
            log.error("Error calculating sanction amount", ex);
            throw ex;
        }
    }

    public BigDecimal calculateEMI(BigDecimal roi,BigDecimal amount, int tenor) {
        try {
            log.debug("Calculating EMI with Amount: {}, ROI: {}, Tenor: {}", amount, roi, tenor);
            BigDecimal monthlyRate = roi.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
            BigDecimal onePlusRatePowTenor = BigDecimal.ONE.add(monthlyRate).pow(tenor);
            BigDecimal numerator = amount.multiply(monthlyRate).multiply(onePlusRatePowTenor);
            BigDecimal denominator = onePlusRatePowTenor.subtract(BigDecimal.ONE);

            if (denominator.compareTo(BigDecimal.ZERO) == 0) {
                log.error("Division by zero when calculating EMI for Amount: {}, ROI: {}, Tenor: {}", amount, roi, tenor);
                throw new ArithmeticException("Division by zero in calculation.");
            }

            BigDecimal emi = numerator.divide(denominator, 2, RoundingMode.HALF_UP);
            log.info("EMI calculated successfully: {}", emi);
            return emi;
        } catch (Exception ex) {
            log.error("Error calculating EMI", ex);
            throw ex;
        }
    }
@Transactional
    public BigDecimal calculateEMIForApplicant(Long applicantId) {
        try {
            log.debug("Calculating EMI for Applicant ID: {}", applicantId);
            Optional<VehicleLoanProgram> optionalProgram = programRepository.findByApplicantIdAndDelFlg(applicantId,"N");
            if (optionalProgram.isEmpty()) {
                log.debug("No program found for Applicant ID: {}", applicantId);
                return BigDecimal.ZERO;
            }

            VehicleLoanProgram program = optionalProgram.get();
            VehicleLoanApplicant applicant = applicantRepository.findByApplicantIdAndDelFlg(applicantId, ACTIVE_FLAG);
            String residentFlag = applicant.getResidentFlg();

            BigDecimal ami = calculateAmi(program);
            FoirDTO foirBalance = calculateFoirBalance(program, residentFlag);
            BigDecimal obligations = new BigDecimal(financialRepository.findByApplicantIdAndDelFlg(applicantId, ACTIVE_FLAG).getTotObligations());
            BigDecimal calculatedEMI = (ami.multiply(foirBalance.getFoirBalRate()).divide(new BigDecimal(100),10,RoundingMode.HALF_EVEN)).subtract(obligations);
            program.setFoirRate(foirBalance.getFoirRate());
            programRepository.save(program);
            log.info("Calculated EMI for Applicant ID: {}, EMI: {}", applicantId, calculatedEMI);
            return calculatedEMI;
        } catch (Exception ex) {
            log.error("Error calculating EMI for applicant", ex);
            throw ex;
        }
    }

    public BigDecimal calculateTotalEMI(String winum, Long slno) {
        try {
            log.debug("Calculating total EMI for WiNum: {}, SlNo: {}", winum, slno);
            List<VehicleLoanApplicant> applicants = applicantRepository.findByWiNumAndSlno(winum, slno);
            List<VehicleLoanProgram> programs = vehicleLoanProgramService.getVehicleLoanProgram(winum, slno);
            Set<Long> applicantIds = programs.stream()
                    .filter(program -> INCOME_CONSIDERED_FLAG.equals(program.getIncomeConsidered()))
                    .map(VehicleLoanProgram::getApplicantId)
                    .collect(Collectors.toSet());

            BigDecimal totalEmi = applicants.stream()
                    .filter(applicant -> applicantIds.contains(applicant.getApplicantId()))
                    .map(applicant -> calculateEMIForApplicant(applicant.getApplicantId()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            log.info("Total EMI calculated successfully: {}", totalEmi);
            return totalEmi;
        } catch (Exception ex) {
            log.error("Error calculating total EMI", ex);
            throw ex;
        }
    }

    public BigDecimal calculateAmi(VehicleLoanProgram program) {
        log.debug("Calculating AMI for Program : {}", program.getLoanProgram());
        String docType = program.getDoctype();
       // BigDecimal ami = ABB_DOC_TYPE.equals(docType) ? program.getAbb() : program.getAvgSal();
        BigDecimal ami = program.getAvgSal();
        log.info("Calculated AMI: {}", ami);
        return ami;
    }

    public BigDecimal calculateSanctionAmountIncome(String winum, Long slno, int tenor, BigDecimal roi) {
        try {
            log.debug("Calculating Sanction Amount Income for WiNum: {}, SlNo: {}", winum, slno);
            BigDecimal totalEmi = calculateTotalEMI(winum, slno);
            BigDecimal sanctionAmount = totalEmi.compareTo(BigDecimal.ZERO) > 0 ? calculateSanctionAmount(totalEmi, roi, tenor) : BigDecimal.ZERO;
            log.info("Sanction Amount Income calculated successfully: {}", sanctionAmount);
            return sanctionAmount;
        } catch (Exception ex) {
            log.error("Error calculating Sanction Amount Income", ex);
            throw ex;
        }
    }

    private FoirDTO calculateFoirBalance(VehicleLoanProgram program, String residentFlag) {
        try {
            FoirDTO foirDTO=new FoirDTO();
            log.debug("Calculating FOIR Balance for Program : {}, Resident Flag: {}", program.getLoanProgram(), residentFlag);
            BigDecimal foirBalance,foirRate;
            BigDecimal Hundred=new BigDecimal(100);
            if (RESIDENT_FLAG.equals(residentFlag)) {
                boolean nonFoir = loanDetailsService.findByWiNumAndSlno(program.getWiNum(), program.getSlNo())
                        .stream()
                        .anyMatch(loanDetail -> NON_FOIR_FLAG.equals(loanDetail.getFoirType()));
                String residentFoir = nonFoir ? "100" : getResidentFoirValue();
                foirRate=new BigDecimal(residentFoir);
                foirBalance =foirRate;// Hundred.subtract(foirRate);
            } else {
                String docType = program.getDoctype();
                if (ABB_DOC_TYPE.equals(docType)) {
                    foirRate=getNriAbbFoirValue();
                    foirBalance =foirRate;// Hundred.subtract(foirRate);
                } else if (MONTHLY_DOC_TYPE.equals(docType)) {
                    foirRate=getNriGrossIncomeFoirValue();
                    foirBalance = foirRate;//Hundred.subtract(foirRate);
                } else {
                    throw new IllegalArgumentException("Unknown document type: " + docType);
                }
            }
            log.info("FOIR Balance calculated: {}", foirBalance);
            foirDTO.setFoirRate(foirRate);
            foirDTO.setFoirBalRate(foirBalance);
            return foirDTO;
        } catch (Exception ex) {
            log.error("Error calculating FOIR Balance", ex);
            throw ex;
        }
    }

    private String getResidentFoirValue() {
        try {
            log.debug("Getting Resident FOIR Value");
            String value = iBankService.getMisPRM("RESIDENTFOIR").getPVALUE();
            log.info("Resident FOIR Value: {}", value);
            return value;
        } catch (Exception ex) {
            log.error("Error retrieving Resident FOIR Value", ex);
            throw ex;
        }
    }

    private BigDecimal getNriAbbFoirValue() {
        try {
            log.debug("Getting NRI ABB FOIR Value");
            BigDecimal value =new BigDecimal(iBankService.getMisPRM("NRIABBFOIR").getPVALUE());
            log.info("NRI ABB FOIR Value: {}", value);
            return value;
        } catch (Exception ex) {
            log.error("Error retrieving NRI ABB FOIR Value", ex);
            throw ex;
        }
    }

    private BigDecimal getNriGrossIncomeFoirValue() {
        try {
            log.debug("Getting NRI Gross Income FOIR Value");
            BigDecimal value = new BigDecimal(iBankService.getMisPRM("NRIGROSSINCOMEFOIR").getPVALUE());
            log.info("NRI Gross Income FOIR Value: {}", value);
            return value;
        } catch (Exception ex) {
            log.error("Error retrieving NRI Gross Income FOIR Value", ex);
            throw ex;
        }
    }
}
