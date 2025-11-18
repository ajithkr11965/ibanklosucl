package com.sib.ibanklosucl.service.eligibility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.dto.CheckEligibilityRequest;
import com.sib.ibanklosucl.dto.LoanVehicleDto;
import com.sib.ibanklosucl.dto.VehicleEmpProgram;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.VehicleLoanDetails;
import com.sib.ibanklosucl.model.VehicleLoanProgram;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.ValidationRepository;
import com.sib.ibanklosucl.service.VehicleLoanDetailsService;
import com.sib.ibanklosucl.service.VehicleLoanProgramService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class EligibilityHelperService {

    @Autowired
    private com.sib.ibanklosucl.service.iBankService iBankService;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private VehicleLoanProgramService vehicleLoanProgramService;
    @Autowired
    private SanctionAmtCal sanctionAmtCal;
    @Autowired
    private VehicleLoanApplicantService vehicleLoanApplicantService;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private ValidationRepository vr;
    @Autowired
    private VehicleLoanDetailsService loanDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CheckEligibilityRequest.LoanEligibleDto getProgramBasedEligibility(String winum, Long slno, int tenor, BigDecimal cardRate, BigDecimal TotalLtv) {
        CheckEligibilityRequest.LoanEligibleDto response = new CheckEligibilityRequest.LoanEligibleDto();
        BigDecimal abb = null, ami = null, foirBalancePer = null, obligation = null, fdAvailableAmt = null, eligibleAmt = null, emi = null;
        List<VehicleLoanProgram> vps = vehicleLoanProgramService.getVehicleLoanProgram(winum, slno);
        vps = vps.stream().filter(program -> !"NONE".equals(program.getLoanProgram())).collect(Collectors.toList());
        if (vps.isEmpty()) {
            throw new ValidationException(ValidationError.ERRO02);
        }
        String loanProgram = vps.get(0).getLoanProgram();
        if ("60/40".equals(loanProgram)) {
            eligibleAmt = TotalLtv;
            //eligibleAmt = vehicleAmt.multiply(new BigDecimal("0.70"));
        } else if ("SURROGATE".equals(loanProgram)) {
            abb = vehicleLoanProgramService.getSumOfAbbWhereDelFlgIsN(winum, slno);
            emi = abb.divide(new BigDecimal(2), 10, RoundingMode.HALF_UP); //EMI = 0.5 * ABB
            eligibleAmt = sanctionAmtCal.calculateSanctionAmount(emi, cardRate, tenor);
        } else if ("INCOME".equals(loanProgram)) {
            //     ami=
            eligibleAmt = sanctionAmtCal.calculateSanctionAmountIncome(winum, slno, tenor, cardRate);
            emi=sanctionAmtCal.calculateEMI(cardRate,eligibleAmt,tenor);
        } else if ("LOANFD".equals(loanProgram)) {
            fdAvailableAmt = vehicleLoanProgramService.getSumOfDepAmtWhereDelFlgIsN(winum, slno);
            eligibleAmt = fdAvailableAmt;
        } else {
            throw new ValidationException(ValidationError.ERRO02);
        }

        response.setAbb(abb);
        response.setAmi(ami);
        response.setEmi(emi);
        response.setObligation(obligation);
        response.setEligibleAmt(eligibleAmt);
        response.setFoirBalancePer(foirBalancePer);
        response.setFdAvailableAmt(fdAvailableAmt);
        response.setLoanProgram(loanProgram);
        return response;
    }

    public BigDecimal calculateEmi(BigDecimal roi, BigDecimal amount, int tenor) {
        return sanctionAmtCal.calculateEMI(roi, amount, tenor);
    }

    public String checkEligibilityValidations(String winum, Long slno) {

        List<String> validationMessages = new ArrayList<>();
        List<VehicleEmpProgram> empPrograms = fetchRepository.getEmpProgram(slno);
        LoanVehicleDto loanVehicle = fetchRepository.getLoanAndVehicle(slno);
        Map<String, List<VehicleEmpProgram>> applicantsByProgram = empPrograms.stream()
                .collect(Collectors.groupingBy(VehicleEmpProgram::getLoanProgram));
        long nonNoneProgramsCount = applicantsByProgram.keySet().stream()
                .filter(program -> !"NONE".equals(program))
                .count();
        long surrogateCount = applicantsByProgram.keySet().stream()
                .filter("SURROGATE"::equals)
                .count();
        //4.Checking Program
        if (nonNoneProgramsCount == 0) {
            validationMessages.add(String.valueOf(ValidationError.ERRO02));
        } else if (nonNoneProgramsCount > 1) {
            validationMessages.add(String.valueOf(ValidationError.ERRO01));
        } else if (surrogateCount > 1) {
            validationMessages.add(String.valueOf(ValidationError.ERRO09));
        } else if (empPrograms.stream().anyMatch(t -> !"NONE".equals(t.getLoanProgram()) && "G".equals(t.getApplicantType()))) {
            validationMessages.add(String.valueOf(ValidationError.ERRO13));
        } else {
            String loanProgram = applicantsByProgram.keySet().stream()
                    .filter(program -> !"NONE".equals(program))
                    .findFirst()
                    .orElseThrow(() -> new ValidationException(ValidationError.ERRO02));


            List<String> LoopingValidations = empPrograms.stream()
                    .flatMap(program -> {
                        List<String> messages = new ArrayList<>();
                        String programType = program.getLoanProgram();
                        String empType = program.getEmploymentType();
                        String monthlyIncome = switch (program.getLoanProgram()) {
                            case "INCOME" -> program.getAvgSal();
                            case "SURROGATE" -> program.getAbb();
                            case "LOANFD" -> program.getDepAmt();
                            default -> "0";
                        };
//                        //1.All applicant & co applicant, whose income considered min income check
//                        if (!"NONE".equals(programType) && !vr.validateBorrowerMinIncome(programType,empType,monthlyIncome)) {
//                            messages.add("Minimum Income Criteria not met for : " + program.getApplName());
//                        }
//                        //5.Age Check applicant , co applicant & gurantor
//                        int age= CommonUtils.calculateAge(program.getApplDob());
//                        if ( !vr.checkAge(programType,empType,age)) {
//                            messages.add("Age Criteria not met for : " + program.getApplName() +" Age");
//                        }

                        //9.PROGRAM 60/40 Employment should not  be NONE
                        if ("60/40".equals(programType) && "NONE".equals(program.getEmploymentType())) {
                            messages.add("Employment Criteria not met for (PROGRAM 60/40) : " + program.getApplName() + " Age");
                        }

                        return messages.stream();

                    })
                    .toList();


            if (!validateMinAge(winum, slno, loanProgram)) {
                validationMessages.add("At least one applicant or co-applicant does not meet the minimum age criteria");
            }

            if (!validateResidenceEligibility(winum, slno, loanProgram)) {
                validationMessages.add("Applicants do not meet residence eligibility criteria");
            }


            if (validateLoanAmount(winum, slno, loanProgram)) {
                validationMessages.add("Loan amount does not meet eligibility criteria");
            }

            if (!validateTenure(winum, slno, loanProgram)) {
                validationMessages.add("Loan tenure does not meet eligibility criteria");
            }

            if (!validateProgram(winum, slno)) {
                validationMessages.add("Program criteria does not meet");
            }
        }

        if (validationMessages.isEmpty()) {
            return "OK";
        } else {
            return "<ul><li>" + String.join("</li><li>", validationMessages) + "</li></ul>";
        }
    }

    public boolean validateMinAge(String winum, Long slno, String loanProgram) {
        String sql = "SELECT COUNT(*) FROM VEHICLE_LOAN_APPLICANTS vla " +
                "JOIN VEHICLE_LOAN_PROGRAM vlp ON vla.APPLICANT_ID = vlp.APPLICANT_ID " +
                "JOIN VLMINAGEELIGIBLEMAS@mybank vlmin ON vlp.LOAN_PROGRAM = vlmin.PROGRAM_NAME " +
                "WHERE vla.APPLICANT_TYPE IN ('A', 'C')  AND vla.WI_NUM = ? AND vla.SLNO = ? AND vla.DEL_FLG = 'N' " +
                "AND vlmin.PROGRAM_NAME = ? AND FLOOR(vlmin.MIN_AGE) <=  months_between(TRUNC(sysdate),vla.APPL_DOB)/12";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{winum, slno, loanProgram}, Integer.class);
        return count != null && count > 0;
    }

    public boolean validateEmploymentEligibility(String winum, Long slno, String loanProgram) {
        String sql = "SELECT COUNT(*) FROM VLEMPLOYMENT vle " +
                "JOIN VLEMPLOPYMENTELIGIBLEMAS@mybank vlemp ON vle.EMPLOYMENT_TYPE = vlemp.EMPLOYMENT_NAME " +
                "WHERE vle.WI_NUM = ? AND vle.SLNO = ? AND vle.DEL_FLG = 'N' " +
                "AND vlemp.PROGRAM_NAME = ? AND vlemp.MIN_TOTAL_EMPLOYMENT <= vle.TOTAL_EXPERIENCE " +
                "AND vlemp.MIN_CURRENT_EMPLOYMENT <= vle.CURRENT_EXPERIENCE";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{winum, slno, loanProgram}, Integer.class);
        return count != null && count > 0;
    }

    public boolean validateResidenceEligibility(String winum, Long slno, String loanProgram) {
        String sql = "SELECT COUNT(*) FROM VEHICLE_LOAN_APPLICANTS vla " +
                "JOIN VEHICLE_LOAN_BASIC vlb ON vla.APPLICANT_ID = vlb.APPLICANT_ID " +
                "JOIN VEHICLE_LOAN_PROGRAM vlp ON vla.APPLICANT_ID = vlp.APPLICANT_ID " +
                "JOIN VLRESIDENCEELIGIBLEMAS@mybank vlres ON vlp.LOAN_PROGRAM = vlres.PROGRAM_NAME " +
                "WHERE vla.APPLICANT_TYPE = 'A' AND vla.WI_NUM = ? AND vla.SLNO = ? AND vla.DEL_FLG = 'N' " +
                "AND vlres.PROGRAM_NAME = ? AND vlres.MIN_MONTH <= vlb.DURATION_STAY";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{winum, slno, loanProgram}, Integer.class);
        return count != null && count > 0;
    }


    public boolean validateLoanAmount(String winum, Long slno, String loanProgram) {
        String sql = "SELECT COUNT(*) FROM vlloanamounteligiblemas@mybank WHERE " +
                "PROGRAM_NAME = ? AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{loanProgram, "All"}, Integer.class);
        if (count != null && count > 0) {
            sql = "SELECT COUNT(*) FROM vlloanamounteligiblemas@mybank WHERE " +
                    "PROGRAM_NAME = ? AND ? BETWEEN MIN_AMT AND MAX_AMT " +
                    "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
            count = jdbcTemplate.queryForObject(sql, new Object[]{loanProgram, 25000.0, "Salaried"}, Integer.class);
            return count != null && count > 0;
        } else {
            return true;
        }
    }


    public boolean validateLoanAmount2(String winum, Long slno, String loanProgram, String amount, String employmentType) {
        String sql = "SELECT COUNT(*) FROM vlloanamounteligiblemas@mybank WHERE " +
                "PROGRAM_NAME = ? AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{loanProgram, "All"}, Integer.class);
        if (count != null && count > 0) {
            sql = "SELECT COUNT(*) FROM vlloanamounteligiblemas@mybank WHERE " +
                    "PROGRAM_NAME = ? AND ? BETWEEN MIN_AMT AND MAX_AMT " +
                    "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
            count = jdbcTemplate.queryForObject(sql, new Object[]{loanProgram, amount, employmentType}, Integer.class);
            return count != null && count > 0;
        } else {
            return true;
        }
    }

    public boolean validateTenure(String winum, Long slno, String loanProgram) {
        VehicleLoanDetails vld = loanDetailsService.findByWiNumAndSlno(winum, slno).get(0);
        String sql = "SELECT COUNT(*) FROM vltenureeligiblemas@mybank WHERE " +
                "PROGRAM_NAME = ? AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{loanProgram, "Salaried"}, Integer.class);
        if (count != null && count > 0) {
            sql = "SELECT COUNT(*) FROM vltenureeligiblemas@mybank WHERE " +
                    "PROGRAM_NAME = ? AND ? BETWEEN MIN_TENURE AND MAX_TENURE " +
                    "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
            count = jdbcTemplate.queryForObject(sql, new Object[]{loanProgram, vld.getTenor(), "Salaried"}, Integer.class);
            return count != null && count > 0;
        } else {
            return true;
        }


    }

    public boolean validateProgram(String winum, Long slno) {
        return vehicleLoanProgramService.validateProgram(winum, slno);
    }

    public boolean validateDPDDays(String winum, Long slno, String loanProgram) {
        String sql = "SELECT COUNT(*) FROM VLDPDDAYSELIGIBLEMAS@mybank WHERE " +
                "PROGRAM_NAME = ? AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{loanProgram, "Salaried"}, Integer.class);
        if (count != null && count > 0) {
            sql = "SELECT COUNT(*) FROM vltenureeligiblemas@mybank WHERE " +
                    "PROGRAM_NAME = ? AND ? BETWEEN MIN_TENURE AND MAX_TENURE " +
                    "AND (EMPLOYMENT_NAME = ? OR EMPLOYMENT_NAME = 'All') AND DEL_FLAG = 'N'";
            count = jdbcTemplate.queryForObject(sql, new Object[]{loanProgram, 60.0, "All"}, Integer.class);
            return count != null && count > 0;
        } else {
            return true;
        }

    }

    public boolean parseEligibilityData(String jsonData) throws ValidationException {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);

            // Check overall status
            String status = rootNode.path("status").asText();
            if (!"SUCCESS".equals(status)) {
                throw new ValidationException(ValidationError.COM001, "The status is not SUCCESS");
            }


            String eligibilityFlag = rootNode.path("eligibilityFlag").asText();
            //  String eligibilityStatus = eligibilityData.path("status").asText();
            if ("green".equals(eligibilityFlag)) {
                return true;
            } else if ("red".equals(eligibilityFlag)) {
                JsonNode eligibilityData = rootNode.path("eligibilityData");
                String htmlContent = generateHtmlContent(eligibilityData);
                throw new ValidationException(ValidationError.COM001, htmlContent);
            } else {
                throw new ValidationException(ValidationError.COM001, "Unknown eligibility status");
            }

        } catch (ValidationException e) {
            throw new ValidationException(ValidationError.COM001, e.getMessage());
        } catch (Exception e) {
            throw new ValidationException(ValidationError.COM001, "Invalid JSON format :" + e);
        }
    }

    private String generateHtmlContent(JsonNode eligibilityData) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<div class='container'>")
                .append("<div class='alert alert-danger' role='alert'>")
                .append("<h4>Eligibility Criteria Not Met</h4>")
                .append("</div>")
                .append("<table class='table table-sm table-bordered table-striped'>")
                .append("<thead class='thead-light'>")
                .append("<tr>")
                .append("<th>Eligibility Description</th>")
                .append("<th>Applicant Name</th>")
                .append("<th>Entered Value</th>")
                .append("<th>Eligible Value</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");

        Iterator<Map.Entry<String, JsonNode>> fields = eligibilityData.fields();
        StreamSupport.stream(((Iterable<Map.Entry<String, JsonNode>>) () -> fields).spliterator(), false)
                .filter(field -> !field.getKey().equals("status"))
                .forEach(field -> {
                    JsonNode value = field.getValue();
                    String eliDesc = value.path("eliDesc").asText();
                    String eliCode = value.path("eliCode").asText();
                    JsonNode eliSubArray = value.path("eliSub");
                    int rowSpan = eliSubArray.size();

                    for (int i = 0; i < rowSpan; i++) {
                        JsonNode sub = eliSubArray.get(i);
                        String applicantName = sub.path("applicantName").asText();
                        String currentValue = sub.path("currentValue").asText();
                        String masterValue = sub.path("masterValue").asText();
                        String Desc = sub.path("Desc").asText();

                        htmlBuilder.append("<tr>");
                        if (i == 0) {
                            htmlBuilder.append("<td rowspan='").append(rowSpan).append("'>").append(eliDesc).append("</td>");
                        }
                        if (!"ELI000".equalsIgnoreCase(eliCode)) {
                            htmlBuilder.append("<td>").append(applicantName).append("</td>")
                                    .append("<td>").append(currentValue).append("</td>")
                                    .append("<td>").append(masterValue).append("</td>")
                                    .append("</tr>");
                        } else {
                            htmlBuilder.append("<td>").append(applicantName).append("</td>")
                                    .append("<td colspan='2'>").append(Desc).append("</td>")
                                    .append("</tr>");
                        }
                    }
                });

        htmlBuilder.append("</tbody>")
                .append("</table>")
                .append("</div>");
        return htmlBuilder.toString();
    }


    public String checkLoanTenor(int tenor, String slno) {
        String result="F|Please check the loan tenor";
        try{
            List<VehicleEmpProgram> empPrograms=fetchRepository.getEmpProgram(Long.parseLong(slno));
            vr.checkLoanTenor(empPrograms,tenor);
            Long minFixedTenor = Long.valueOf(iBankService.getMisPRM("MINFIXEDTENOR").getPVALUE());
            VehicleLoanDetails loanDetails= loanDetailsService.findBySlnoAndDelFlg(Long.parseLong(slno));
            log.info("minFixedTenor:"+minFixedTenor+"loanDetails.getRoiType():"+loanDetails.getRoiType()+",tenorL:"+tenor);
            if(tenor<minFixedTenor && "FIXED".equalsIgnoreCase(loanDetails.getRoiType())){
                result="F|For fixed ROI type, the minimum loan tenor should be "+minFixedTenor+" months";
            }else{
                result="S";
            }

        }catch(Exception e){
            log.error("some exception in checkLoanTenor",e);
        }
        log.info("checkLoanTenor result:"+result);
        return result;
    }
}
