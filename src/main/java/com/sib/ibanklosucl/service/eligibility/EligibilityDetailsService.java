package com.sib.ibanklosucl.service.eligibility;

import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.*;
import com.sib.ibanklosucl.service.*;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EligibilityDetailsService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private EligibilityDetailsRepository eligibilityDetailsRepository;
    @Autowired
    private VehicleLoanDetailsRepositoryImpl loanDetailsImpl;
    @Autowired
    private VehicleLoanDetailsService loanDetailsService;

    @Autowired
    private VehicleDetailsService vehicleDetailsService;

    @Autowired
    private iBankService iBankService;

    @Autowired
    private VehicleLoanProgramService vehicleLoanProgramService;

    @Autowired
    private VLEmploymentService vLEmploymentService;
    @Autowired
    private VLCreditService vlCreditService;

    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private ValidationRepository validationRepository;

    @Autowired
    private ProgramBasedEligibilityService programBasedEligibilityService;

    @Autowired
    private VehicleLoanVehicleRepository vehicleLoanVehicleRepository;
    @Autowired
    private VehicleEligiblityRepository eligiblityRepository;


//    public LoanEligibilityDetailsDTO getLoanEligibilityDetails(String wiNum, Long slNo) {
//        List<VehicleLoanProgram> vps = vehicleLoanProgramService.getVehicleLoanProgram(wiNum, slNo);
//        vps = vps.stream().filter(program -> !"NONE".equals(program.getLoanProgram())).collect(Collectors.toList());
//        String loanProgram = vps.isEmpty() ? null : vps.get(0).getLoanProgram();
//
//        List<VLEmployment> vlempdetails = vLEmploymentService.findBySlno(slNo);
//        List<String> employmentTypes = vlempdetails.stream().map(VLEmployment::getEmployment_type).collect(Collectors.toList());
//
//        boolean isCreditComplete = !isCreditComplete(wiNum, String.valueOf(slNo));
//
//        AnnualIncomeAndBankBalance result = vehicleLoanProgramService.getAnnualIncomeAndBankBalance( slNo);
//        Long creditScore = vlCreditService.getVLCreditDetails(wiNum, slNo).getBureauScore();
//
//        VehicleLoanVehicle vehicle = vehicleDetailsService.fetchExisting(wiNum, slNo, vlempdetails.get(0).getApplicantId());
//        VehicleLoanDetails loanDetails = loanDetailsService.findByWiNumAndSlno(wiNum, slNo).get(0);
//
//        BigDecimal amount = "INCOME".equals(loanProgram) ? result.getAnnualIncome() : result.getAnnualBankBalance();
//        String ltvDetails = iBankService.getLTVDetails(vehicle.getMakeName(), "DSA", loanProgram, String.valueOf(loanDetails.getTenor()), vehicle.getModelName(), "All", amount.toString());
//
//        return new LoanEligibilityDetailsDTO(loanProgram, employmentTypes, isCreditComplete, amount.toString(), creditScore, ltvDetails,loanDetails.getTenor());
//    }

    public ResponseEntity<?> getLoanEligibilityDetails(Long slNo) {
        try {
            List<VehicleEmpProgram> empPrograms = fetchRepository.getEmpProgram(slNo);
            VehicleLoanVehicle vehicle = vehicleLoanVehicleRepository.findExistingBySlno(slNo);
            LoanVehicleDto loanVehicle = fetchRepository.getLoanAndVehicle(slNo);
            boolean isNRIExistAndProgramIncome = empPrograms.stream()
                    .anyMatch(applicant -> "N".equals(applicant.getResidentFlg()) && "INCOME".equals(applicant.getLoanProgram()));
            //Validating program
            String program = validationRepository.validateLoanPrograms(empPrograms);
            //check whether all tabs completed
            validationRepository.isAllTabCompleted(slNo);
            BigDecimal vehicleLoanAmount = new BigDecimal(vehicle.getTotalInvoicePrice());
            BigDecimal requestLoanAmount = new BigDecimal(loanVehicle.getLoanAmt());
            BigDecimal tenor = new BigDecimal(loanVehicle.getTenor());
            BigDecimal addltvAmount=BigDecimal.ZERO;

            if("Y".equalsIgnoreCase(loanVehicle.getInsVal()) && "BAJ".equalsIgnoreCase(loanVehicle.getInsType())){
                addltvAmount=new BigDecimal(loanVehicle.getInsAmt());
            }

            //For any applicant/co-applicant whose income considered is “yes” and is having employment type as agriculturist/pensioner, then system will not auto-populate LTV percentage
            if (empPrograms.stream().anyMatch(t -> "INCOME".equals(t.getLoanProgram()) && ("PENSIONER".equals(t.getEmploymentType()) || "AGRICULTURIST".equals(t.getEmploymentType())))) {
                Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findBySlnoAndDelFlg(slNo, "N");
                BigDecimal ltvPer = null, ltvAmount = null;
                if (eligibilityDetailsOpt.isPresent()) {
                    ltvPer = eligibilityDetailsOpt.get().getLtvPer();
                    ltvAmount = eligibilityDetailsOpt.get().getLtvAmt();
                }
                return new ResponseEntity<>(new LoanEligibilityDetailsDTO(true, vehicleLoanAmount, ltvPer, ltvAmount, requestLoanAmount, tenor,addltvAmount), HttpStatus.OK);
            }

            String channel = "DSA";
            String yearsalAmount = null, ltvDetails = null;
            //LTV
            //FOR INCOME
            if ("INCOME".equals(program)) {
                BigDecimal nriLtvPer = null, resLtvPer = null;
                if (isNRIExistAndProgramIncome) {
                    yearsalAmount = empPrograms.stream().filter(a -> "N".equals(a.getResidentFlg()) && "INCOME".equals(a.getLoanProgram())).map(a -> new BigDecimal(a.getAvgSal())).reduce(BigDecimal.ZERO, BigDecimal::add).multiply(new BigDecimal(12)).toString();
                    ltvDetails = iBankService.getLTVDetail(loanVehicle.getMakeName(), channel, "NRI", loanVehicle.getTenor(), loanVehicle.getModelName(), "All", yearsalAmount);
                    nriLtvPer = new BigDecimal(ltvDetails);
                }
                //Resident  with program income
                if (empPrograms.stream().anyMatch(a -> "R".equals(a.getResidentFlg()) && "INCOME".equals(a.getLoanProgram()))) {
                    //ami*12 for yearly
                    yearsalAmount = empPrograms.stream().filter(a -> "R".equals(a.getResidentFlg()) && "INCOME".equals(a.getLoanProgram())).map(a -> new BigDecimal(a.getAvgSal())).reduce(BigDecimal.ZERO, BigDecimal::add).multiply(new BigDecimal(12)).toString();
                    ltvDetails = iBankService.getLTVDetail(loanVehicle.getMakeName(), channel, program, loanVehicle.getTenor(), loanVehicle.getModelName(), "All", yearsalAmount);
                    resLtvPer = new BigDecimal(ltvDetails);
                }
                ltvDetails = CommonUtils.min(nriLtvPer, resLtvPer).toString();
            } else {
                //ami*12 for yearly
                yearsalAmount = "SURROGATE".equals(program) ? empPrograms.stream().filter(a -> "SURROGATE".equals(a.getLoanProgram())).map(a -> new BigDecimal(a.getAbb())).reduce(BigDecimal.ZERO, BigDecimal::add).multiply(new BigDecimal(12)).toString() : ("LOANFD".equals(program) ? empPrograms.stream().filter(a -> "LOANFD".equals(a.getLoanProgram())).map(a -> new BigDecimal(a.getDepAmt())).reduce(BigDecimal.ZERO, BigDecimal::add).toString() : "0");
                ltvDetails = iBankService.getLTVDetail(loanVehicle.getMakeName(), channel, program, loanVehicle.getTenor(), loanVehicle.getModelName(), "All", yearsalAmount);
            }

            BigDecimal ltvpercentage = new BigDecimal(ltvDetails);
            BigDecimal ltvAmount = vehicleLoanAmount.multiply(ltvpercentage).divide(new BigDecimal(100), 0, RoundingMode.FLOOR);
            return new ResponseEntity<>(new LoanEligibilityDetailsDTO(false, vehicleLoanAmount, ltvpercentage, ltvAmount, requestLoanAmount, tenor,addltvAmount), HttpStatus.OK);
        } catch (ValidationException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getError().name(), e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(null, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    public Map<String, Object> getEligibilityDetails(String wiNum, Long slNo) {
        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(wiNum, slNo);

        if (eligibilityDetailsOpt.isPresent()) {
            EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("vehicleAmount", eligibilityDetails.getVehicleAmt());
            response.put("ltvPercent", eligibilityDetails.getLtvPer());
            response.put("ltvAmount", eligibilityDetails.getLtvAmt());
            response.put("requestedLoanAmount", eligibilityDetails.getLoanAmt());
            response.put("tenor", eligibilityDetails.getTenor());
            response.put("ino", eligibilityDetails.getIno());
            response.put("ltvType", eligibilityDetails.getLtvType());
            return response;
        } else
            return null;
    }

    public EligibilityDetails  checkProgramEligibilityWithoutInsurance(String wiNum,Long slno,boolean saveData) throws Exception {

        EligibilityDetails response = new EligibilityDetails();


        if (programBasedEligibilityService.checkEligibilityValidations(wiNum, slno)) {

            VehicleLoanDetails loanDetails = loanDetailsService.findByWiNumAndSlno(wiNum, slno).get(0);
            if(!"Y".equalsIgnoreCase(loanDetails.getInsVal())){
                throw new ValidationException(ValidationError.COM001,"Cannot Modify the WI Since Documentation has been Initiated");
            }
            Optional<EligibilityDetails> eligibilityDetails_ = eligibilityDetailsRepository.findBySlnoAndDelFlg(slno, "N");
            EligibilityDetails eligibilityDetails = eligibilityDetails_.get();
            //for Bajaj Insurance
            BigDecimal TotalLtv=eligibilityDetails.getLtvAmt();
            BigDecimal additionalLtv=BigDecimal.ZERO;
            BigDecimal dealerAmt=BigDecimal.ZERO;
            BigDecimal insAmt=BigDecimal.ZERO;

            BigDecimal  CardRate = eligibilityDetails.getSancCardRate();
            BigDecimal loanAmount = eligibilityDetails.getSancAmountRecommended();

            CheckEligibilityRequest.LoanEligibleDto eligibility = programBasedEligibilityService.getProgramBasedEligibility(wiNum, slno, loanDetails.getTenor(), CardRate, TotalLtv);
            BigDecimal eligibleAmt = eligibility.getEligibleAmt();


            if (BigDecimal.ZERO.compareTo(eligibleAmt) == 0) {
                throw new ValidationException(ValidationError.COM001, "Applicant is not eligible EMI is <=0!!");
            }
            //BigDecimal principal = (loanAmount.min(TotalLtv).min(eligibleAmt)).round(new MathContext(0, RoundingMode.FLOOR));
           // BigDecimal finalLTV = principal.multiply(new BigDecimal("100")).divide(loanDetails.getVehicleAmt(),2,RoundingMode.HALF_EVEN);
            BigDecimal principal = eligibilityDetails.getDealerAmt();// taking dealer amount as principal
            BigDecimal finalLTV = TotalLtv.multiply(new BigDecimal("100")).divide(loanDetails.getVehicleAmt(),2,RoundingMode.HALF_EVEN);// chnaged



            List<VehicleEmpProgram> empPrograms=fetchRepository.getEmpProgram(loanDetails.getSlno());
            String program= validationRepository.validateLoanPrograms(empPrograms);
            validationRepository.checkLoanAmount(empPrograms.stream().filter(e-> ("A".equals(e.getApplicantType()) || "C".equals(e.getApplicantType()) )).toList(),principal,program,"ELIG");


            BigDecimal emi = programBasedEligibilityService.getEligibleEmi(CardRate, principal, loanDetails.getTenor());
            response.setVehicleAmt(eligibilityDetails.getVehicleAmt());
            response.setLtvAmt(TotalLtv);
            response.setAddLtvAmt(additionalLtv);
            response.setProgramEligibleAmt(eligibleAmt);
            response.setEmi(emi);
            response.setLtvPer(finalLTV);
            dealerAmt=principal;
            response.setDealerAmt(dealerAmt);
            response.setInsAmt(insAmt);
            response.setVehicleAmt(loanDetails.getVehicleAmt());
            response.setEligibleLoanAmt(principal);

            if(saveData){
                eligibilityDetails.setLtvAmt(TotalLtv);
                eligibilityDetails.setEligibilityUser(usd.getPPCNo());
                eligibilityDetails.setEligibilityDate(new Date());
                eligibilityDetails.setLtvPer(finalLTV);
                eligibilityDetails.setSancEmi(emi);
                eligibilityDetails.setEmi(emi);
                eligibilityDetails.setFinalLTV(finalLTV);
                eligibilityDetails.setProgramEligibleAmt(eligibleAmt);
                eligibilityDetails.setEligibleLoanAmt(principal);
                eligibilityDetails.setEmi(emi);
                eligibilityDetails.setEmiMax(eligibility.getEmi());
                eligibilityDetails.setFinalLTV(finalLTV);
                eligibilityDetails.setDealerAmt(dealerAmt);
                eligibilityDetails.setInsAmt(insAmt);
                eligibilityDetails.setAddLtvAmt(additionalLtv);
                eligibilityDetails.setSancAmountRecommended(principal);
                eligibilityDetailsRepository.save(eligibilityDetails);
                loanDetails.setInsVal("N");
                loanDetails.setInsAmt(BigDecimal.ZERO);
                loanDetails.setInsType("");
                loanDetailsService.save(loanDetails);
            }

        }
        return response;
    }

    public ProgramEligibilityResponse checkProgramEligibility(CheckEligibilityRequest request, String currentQueue) throws Exception {

        ProgramEligibilityResponse response = new ProgramEligibilityResponse();
        if (request.getLtvAmt() == null) {
            throw new ValidationException(ValidationError.COM001, "Kindly Enter LTV Amount !!");
        } else if (request.getLtvPer() != null && (request.getLtvPer().compareTo(BigDecimal.ZERO) < 0 || Objects.requireNonNull(request.getLtvPer()).compareTo(new BigDecimal(100)) > 0)) {
            throw new ValidationException(ValidationError.COM001, "LTV Percantage Should be in range 0-100");
        }

        if (programBasedEligibilityService.checkEligibilityValidations(request.getWiNum(), request.getSlno())) {
            Map data = new HashMap<>();
            VehicleLoanDetails loanDetails = loanDetailsService.findByWiNumAndSlno(request.getWiNum(), request.getSlno()).get(0);
            Long creditScore = vlCreditService.getVLCreditDetails(request.getWiNum(), request.getSlno()).getBureauScore();
            if (request.getLtvPer() != null) {
                BigDecimal ltvAmount = loanDetails.getVehicleAmt().multiply(request.getLtvPer()).divide(new BigDecimal(100), 0, RoundingMode.FLOOR);
                request.setLtvAmt(ltvAmount);
            }

            //for Bajaj Insurance
            BigDecimal TotalLtv=request.getLtvAmt();
            BigDecimal additionalLtv=BigDecimal.ZERO;
            BigDecimal dealerAmt=BigDecimal.ZERO;
            BigDecimal insAmt=BigDecimal.ZERO;
            if(loanDetails.getInsVal().equalsIgnoreCase("Y") ){
                insAmt=loanDetails.getInsAmt();
                BigDecimal existingLtv=request.getLtvAmt();
                if("BAJ".equalsIgnoreCase(loanDetails.getInsType()))
                {
                    additionalLtv=insAmt;
                    TotalLtv=existingLtv.add(additionalLtv);
                    log.info("Total LTV =: {} + {} = {}",existingLtv,additionalLtv,TotalLtv);
                }
            }
            List<VehicleLoanProgram> vps = vehicleLoanProgramService.getVehicleLoanProgram(request.getWiNum(), request.getSlno());
            vps = vps.stream().filter(program -> !"NONE".equals(program.getLoanProgram())).collect(Collectors.toList());
            String loanProgram = vps.isEmpty() ? "" : vps.get(0).getLoanProgram();
            log.info("loanProgram : {}   Wi_num : {} ",loanProgram,request.getWiNum());

            SpreadDTO SpreadAPIData = iBankService.getSpread(
                    loanDetails.getRoiType(),
                    "DSA",
                    String.valueOf(creditScore),
                    String.valueOf(loanDetails.getTenor())
                    ,loanProgram
            );
            if (!"SUCCESS".equalsIgnoreCase(SpreadAPIData.getStatus())) {
                throw new ValidationException(ValidationError.COM001, SpreadAPIData.getReason());
            }
            BigDecimal CardRate;
            BigDecimal loanAmount;
            ///BigDecimal CardRate = SpreadAPIData.getCardrate();
            if ("RM".equals(currentQueue)) {
                if (request.getCardRate().isEmpty()) {
                    CardRate = SpreadAPIData.getCardrate();
                } else {
                    CardRate = new BigDecimal(request.getCardRate());
                }
                if (request.getLoanAmt().isEmpty()) {
                    loanAmount = loanDetails.getLoanAmt();
                } else {
                    loanAmount = new BigDecimal(request.getLoanAmt());
                }
                // For RM queue, use values from the request if available
            } else {
                // For other queues, use values from spreadAPIData and loanDetails
                CardRate = SpreadAPIData.getCardrate();
                loanAmount = loanDetails.getLoanAmt();
            }
            log.info("CardRate : {}   Wi_num : {} ",CardRate,request.getWiNum());
            validateMclrCompliance(CardRate, loanDetails.getTenor());
            CheckEligibilityRequest.LoanEligibleDto eligibility = programBasedEligibilityService.getProgramBasedEligibility(request.getWiNum(), request.getSlno(), loanDetails.getTenor(), CardRate, TotalLtv);
            BigDecimal eligibleAmt = eligibility.getEligibleAmt();


            if (BigDecimal.ZERO.compareTo(eligibleAmt) == 0) {
                throw new ValidationException(ValidationError.COM001, "Applicant is not eligible EMI is <=0!!");
            }
            BigDecimal principal = (loanAmount.min(TotalLtv).min(eligibleAmt)).round(new MathContext(0, RoundingMode.FLOOR));
            BigDecimal finalLTV = principal.multiply(new BigDecimal("100")).divide(loanDetails.getVehicleAmt(),2,RoundingMode.HALF_EVEN);

            dealerAmt=principal.subtract(insAmt);
            if(dealerAmt.compareTo(BigDecimal.ZERO)<=0){
                throw new ValidationException(ValidationError.COM001, "The Amount payed to Dealer <=0!!");
            }

            List<VehicleEmpProgram> empPrograms=fetchRepository.getEmpProgram(loanDetails.getSlno());
            String program= validationRepository.validateLoanPrograms(empPrograms);
            validationRepository.checkLoanAmount(empPrograms.stream().filter(e-> ("A".equals(e.getApplicantType()) || "C".equals(e.getApplicantType()) )).toList(),principal,program,"ELIG");


            BigDecimal emi = programBasedEligibilityService.getEligibleEmi(CardRate, principal, loanDetails.getTenor());

            // Save eligibility details
            Optional<EligibilityDetails> eligibilityDetails_ = eligibilityDetailsRepository.findBySlnoAndDelFlg(request.getSlno(), "N");
            EligibilityDetails eligibilityDetails = null;
            if (eligibilityDetails_.isEmpty()) {
                eligibilityDetails = new EligibilityDetails();
            } else {
                eligibilityDetails = eligibilityDetails_.get();
                data.put("eligibilityIno", eligibilityDetails.getIno().toString());
                data.put("recCPCROI", eligibilityDetails.getRoiRecommendedCPC() != null ? eligibilityDetails.getRoiRecommendedCPC().toPlainString() : "");
                data.put("recCPCAmt", eligibilityDetails.getLoanAmountRecommendedCPC() != null ? eligibilityDetails.getLoanAmountRecommendedCPC().toPlainString() : "");
            }
                eligibilityDetails.setWiNum(request.getWiNum());
                eligibilityDetails.setSlno(request.getSlno());
                eligibilityDetails.setApplicantId(request.getApplicantId());
                eligibilityDetails.setVehicleAmt(loanDetails.getVehicleAmt());
                eligibilityDetails.setLtvAmt(request.getLtvAmt());
                eligibilityDetails.setLoanAmt(loanDetails.getLoanAmt());
                eligibilityDetails.setEligibilityUser(usd.getPPCNo());
                eligibilityDetails.setEligibilityDate(new Date());
                eligibilityDetails.setCmuser(usd.getPPCNo());
                eligibilityDetails.setCmdate(new Date());
                eligibilityDetails.setDelFlg("N");
                eligibilityDetails.setProceedFlag("N");
                eligibilityDetails.setHomeSol(usd.getSolid());
                eligibilityDetails.setProgramEligibleAmt(eligibleAmt);
                eligibilityDetails.setEligibleLoanAmt(principal);
                eligibilityDetails.setCardRate(CardRate);
                eligibilityDetails.setLtvPer(request.getLtvPer());
                eligibilityDetails.setLtvType(request.getLtvType());
                eligibilityDetails.setAbb(eligibility.getAbb());
                eligibilityDetails.setAmi(eligibility.getAmi());
                eligibilityDetails.setEligibilityFlg("Y");
                eligibilityDetails.setFoirBalancePer(eligibility.getFoirBalancePer());
                eligibilityDetails.setObligation(eligibility.getObligation());
                eligibilityDetails.setFdAvailableAmt(eligibility.getFdAvailableAmt());
                eligibilityDetails.setTenor(loanDetails.getTenor());
                eligibilityDetails.setEbr(SpreadAPIData.getEbr());
                eligibilityDetails.setOpCost(SpreadAPIData.getOpcost());
                eligibilityDetails.setSpread(SpreadAPIData.getSpread());
                eligibilityDetails.setCrp(SpreadAPIData.getCpr());
                eligibilityDetails.setIntTblCode("");
                eligibilityDetails.setDelFlg("N");
                eligibilityDetails.setEmi(emi);
                eligibilityDetails.setEmiMax(eligibility.getEmi());
                eligibilityDetails.setFinalLTV(finalLTV);

                        //Dealer

                eligibilityDetails.setDealerAmt(dealerAmt);
                eligibilityDetails.setInsAmt(insAmt);
                eligibilityDetails.setAddLtvAmt(additionalLtv);

                eligibilityDetails = eligibilityDetailsRepository.save(eligibilityDetails);


            data.put("cardRate", CardRate.toString());
            data.put("finalLTV", finalLTV.toString());
            data.put("eligibility", eligibleAmt.toString());

            data.put("principal", principal.toString());
            data.put("emi", emi.toString());
            data.put("loanProgram", eligibility.getLoanProgram());
            data.put("dealerSum",dealerAmt.toString());
            data.put("insSum",insAmt.toString());
            response.setResponse(data);

        }
        return response;
    }

    @Transactional(rollbackOn = Exception.class)
    public void saveEligibilityRecommendation(EligibilityRecommendationRequest request) throws Exception {
        CardRateChangeResult rateChangeResult = checkCardRateChanges(request.getWiNum(), request.getSlno());
       // if (rateChangeResult.isHasChanged()) {
           // throw new RuntimeException("Card rate has changed. Current rate: " + rateChangeResult.getCurrentCardRate() +
                 //   ", Saved rate: " + rateChangeResult.getSavedCardRate() +
                //    ". Please send back the workitem.");
        //} else {
            Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(request.getWiNum(), request.getSlno());
            VehicleLoanDetails loanDetails = loanDetailsService.findByWiNumAndSlno(request.getWiNum(), request.getSlno()).get(0);
            if (eligibilityDetailsOpt.isPresent()) {
                EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
                Long creditScore = vlCreditService.getVLCreditDetails(request.getWiNum(), request.getSlno()).getBureauScore();
                BigDecimal latestCardRate = rateChangeResult.getCurrentCardRate();
                BigDecimal waiver = latestCardRate.subtract(request.getRoiRecommendedCPC());
                BigDecimal changedSpread = eligibilityDetails.getSpread().subtract(waiver);
                BigDecimal newCardRate;

                if ("fixed".equalsIgnoreCase(loanDetails.getRoiType())) {
                    newCardRate = eligibilityDetails.getEbr().add(changedSpread);
                } else { // floating
                    newCardRate = eligibilityDetails.getEbr()
                            .add(eligibilityDetails.getOpCost())
                            .add(eligibilityDetails.getCrp())
                            .add(changedSpread);
                }
                eligibilityDetails.setCardRate(newCardRate);
                eligibilityDetails.setSpread(changedSpread);
                validateMclrCompliance(newCardRate, loanDetails.getTenor());
                BigDecimal finalLTV = request.getLoanAmountRecommendedCPC().multiply(new BigDecimal("100")).divide(loanDetails.getVehicleAmt(),2,RoundingMode.HALF_EVEN);
                // Update all other fields
                eligibilityDetails.setVehicleAmt(request.getVehicleAmt());
                eligibilityDetails.setLtvAmt(request.getLtvAmt());
                eligibilityDetails.setLoanAmt(request.getLoanAmt());
                eligibilityDetails.setEligibilityUser(usd.getPPCNo());
                eligibilityDetails.setEligibilityDate(new Date());
                eligibilityDetails.setCmuser(usd.getPPCNo());
                eligibilityDetails.setCmdate(new Date());
                eligibilityDetails.setProceedFlag("Y");
                eligibilityDetails.setHomeSol(usd.getSolid());
                eligibilityDetails.setLtvPer(request.getLtvPer());
                eligibilityDetails.setLtvType(request.getLtvType());
                eligibilityDetails.setTenor(loanDetails.getTenor());
                eligibilityDetails.setSancTenor(loanDetails.getTenor());
                eligibilityDetails.setLoanAmountRecommendedCPC(request.getLoanAmountRecommendedCPC());
                eligibilityDetails.setSancAmountRecommended(request.getLoanAmountRecommendedCPC().setScale(0,RoundingMode.FLOOR));
                eligibilityDetails.setSancEmi(request.getEmi());
                eligibilityDetails.setEmi(request.getEmi());
                eligibilityDetails.setFinalLTV(finalLTV);
                if (request.getRoiRecommendedCPC().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Recommended ROI must be greater than 0.");
                }
                eligibilityDetails.setRoiRecommendedCPC(request.getRoiRecommendedCPC());
                eligibilityDetails.setCardRate(newCardRate);
                eligibilityDetails.setSancCardRate(newCardRate);
                eligibilityDetailsRepository.save(eligibilityDetails);
            } else {
                throw new RuntimeException("Eligibility details or Loan details not found for the given wiNum and slno");
            }
        //}
    }

    @Transactional
    public void saveEligibilityRecommendationOld(EligibilityRecommendationRequest request) {
        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(request.getWiNum(), request.getSlno());

        if (eligibilityDetailsOpt.isPresent()) {
            EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();

            BigDecimal roiRecommendedCPC = request.getRoiRecommendedCPC();
            if (roiRecommendedCPC.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Recommended ROI must be greater than 0.");
            }

            eligibilityDetails.setLoanAmountRecommendedCPC(request.getLoanAmountRecommendedCPC());
            eligibilityDetails.setRoiRecommendedCPC(roiRecommendedCPC);
            eligibilityDetails.setSancAmountRecommended(request.getLoanAmountRecommendedCPC().setScale(0,RoundingMode.FLOOR));
            eligibilityDetails.setSancEmi(request.getEmi());
            eligibilityDetails.setEmi(request.getEmi());
            eligibilityDetails.setCmuser(usd.getPPCNo());
            eligibilityDetails.setCmdate(new Date());
            eligibilityDetails.setProceedFlag("Y");
            eligibilityDetailsRepository.save(eligibilityDetails);
        } else {
            throw new RuntimeException("Eligibility details not found for the given wiNum and slno");
        }
    }

    public EligibilityDetails save(EligibilityDetails eligibilityDetails) {
        eligibilityDetails.setCmdate(new Date());
        eligibilityDetails.setCmuser(usd.getPPCNo());
        return eligibilityDetailsRepository.save(eligibilityDetails);
    }

    public EligibilityDetails DsaSave(EligibilityDetails eligibilityDetails) {
        return eligibilityDetailsRepository.save(eligibilityDetails);
    }

    public Optional<EligibilityDetails> findById(Long ino) {
        return eligibilityDetailsRepository.findById(ino);
    }

    public Optional<EligibilityDetails> findBySlno(Long slno) {
        return eligibilityDetailsRepository.findBySlnoAndDelFlg(slno, "N");
    }


    public boolean isCreditComplete(String wiNum, String slno) {
        String sql = "SELECT count(*) FROM VEHICLE_LOAN_APPLICANTS vla WHERE nvl(CREDIT_COMPLETE,'N') = 'N' AND DEL_FLG ='N' AND WI_NUM = ? AND SLNO = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{wiNum, slno}, Integer.class) > 0;
    }

    @Transactional
    public ResponseEntity<?> updateRecommendedLoanAmount(String wiNum, Long slno, BigDecimal recommendedAmount) {
        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(wiNum, slno);
        VehicleLoanVehicle vehicleLoanVehicle = vehicleLoanVehicleRepository.findExistingByWiNumAndSlno(wiNum, slno);
        if (eligibilityDetailsOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (vehicleLoanVehicle == null) {
            return ResponseEntity.badRequest().body("Vehicle loan details not found.");
        }

        EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
        BigDecimal programEligibleLoanAmount = eligibilityDetails.getProgramEligibleAmt();
        BigDecimal totalInvoicePrice = new BigDecimal(vehicleLoanVehicle.getTotalInvoicePrice());
        log.info("Validating the loan amounts programEligibleLoanAmount {} totalInvoicePrice {} recommendedAmount {}", programEligibleLoanAmount, totalInvoicePrice, recommendedAmount);

        if (recommendedAmount.compareTo(programEligibleLoanAmount) <= 0 &&
                recommendedAmount.compareTo(totalInvoicePrice) <= 0) {
            eligibilityDetails.setLoanAmountRecommended(recommendedAmount);
            eligibilityDetails.setSancAmountRecommended(eligibilityDetails.getLoanAmountRecommended().setScale(0,RoundingMode.FLOOR));
            eligibilityDetails.setSancEmi(eligibilityDetails.getEmi());
            eligibilityDetails.setSancCardRate(eligibilityDetails.getCardRate());
            eligibilityDetails.setSancTenor(eligibilityDetails.getTenor());
            eligibilityDetailsRepository.save(eligibilityDetails);
            return ResponseEntity.ok("Recommended loan amount updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Recommended amount exceeds program based eligible loan amount or total invoice price.");
        }
    }

    public EligibilityDetails getEligibilityDetailsCPC(String wiNum, Long slNo) {
        return eligibilityDetailsRepository.findByWiNumAndSlno(wiNum, slNo)
                .orElse(null);
    }

    private BigDecimal getCRP() {
        String sql = "select PVALUE as CRP from misprm@MYBANK where PCODE='VLCRP'";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

   private Map<String, Object> getEBRAndOperatingCost() {
    String sql = "select getlaarate@finacle10('L') INTRATE from dual";
    String result = jdbcTemplate.queryForObject(sql, String.class);
    String[] parts = result.split("\\|");

    Map<String, Object> rateMap = new HashMap<>();
    rateMap.put("EBR", parts.length > 0 ? new BigDecimal(parts[0]) : BigDecimal.ZERO);
    rateMap.put("OPERATING_COST", parts.length > 1 ? new BigDecimal(parts[1]) : BigDecimal.ZERO);

    // Add any additional values with meaningful keys
    if (parts.length > 2) {
        for (int i = 2; i < parts.length; i++) {
            rateMap.put("ADDITIONAL_VALUE_" + i, parts[i]);
        }
    }

    return rateMap;
}

    public CardRateChangeResult checkCardRateChanges(String wiNum, Long slno) {
        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(wiNum, slno);
        VehicleLoanDetails loanDetails = loanDetailsService.findByWiNumAndSlno(wiNum, slno).get(0);

        if (eligibilityDetailsOpt.isPresent()) {
            EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
            BigDecimal crp = getCRP();
            Map<String, Object> rateMap = getEBRAndOperatingCost();
            BigDecimal ebr = (BigDecimal) rateMap.getOrDefault("EBR", BigDecimal.ZERO);
            BigDecimal operatingCost = (BigDecimal) rateMap.getOrDefault("OPERATING_COST", BigDecimal.ZERO);
            if("FIXED".equals(loanDetails.getRoiType())) {
                crp=BigDecimal.ZERO;
                operatingCost=BigDecimal.ZERO;
            }
            BigDecimal spread = eligibilityDetails.getSpread();
            BigDecimal currentCardRate = ebr.add(operatingCost).add(crp).add(spread);
            BigDecimal savedCardRate = eligibilityDetails.getCardRate();
            boolean hasChanged = savedCardRate.compareTo(currentCardRate) != 0;
            return new CardRateChangeResult(hasChanged, savedCardRate, currentCardRate);
        } else {
            throw new RuntimeException("Eligibility details or Loan details not found for the given wiNum and slno");
        }
    }
    @Transactional
    public void updateRoiWaveredSpread(Long slno,String wiNum, BigDecimal newCardRate, BigDecimal newEmi, BigDecimal newSpread) {
        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findBySlnoAndDelFlg(slno, "N");
        if (eligibilityDetailsOpt.isPresent()) {
            EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
            VehicleLoanDetails loanDetails = loanDetailsService.findByWiNumAndSlno(wiNum, slno).get(0);
            eligibilityDetails.setSpread(newSpread);
            eligibilityDetails.setCardRate(newCardRate);
            eligibilityDetails.setSancCardRate(newCardRate);
            eligibilityDetails.setSancEmi(newEmi);
            eligibilityDetails.setEmi(newEmi);
            eligibilityDetails.setCmuser(usd.getPPCNo());
            eligibilityDetails.setCmdate(new Date());
            eligibilityDetailsRepository.save(eligibilityDetails);
            log.info("Updated eligibility details after ROI waiver for slno: {}", slno);
        } else {
            throw new RuntimeException("Eligibility details not found for slno: " + slno);
        }
    }
    public String getEligibilityFlag(Long slno) {
    return eligibilityDetailsRepository.findBySlnoAndDelFlg(slno, "N")
        .map(EligibilityDetails::getEligibilityFlg)
        .orElse("N");
}
private void validateMclrCompliance(BigDecimal cardRate, Integer tenor) {
    // Check if tenure is between 12 to 36 months (both inclusive)
    if (tenor != null && tenor >= 12 && tenor <= 36) {
        BigDecimal mclrRate = fetchRepository.getMclrRate();

        if (cardRate.compareTo(mclrRate) < 0) {
            throw new ValidationException(ValidationError.COM001,
                String.format("As per RBI Master Direction, the rate of interest on fixed rate loans " +
                "of tenor below 3 years shall not be less than 12 months MCLR (%.2f%%). " +
                "Current proposed rate: %.2f%%", mclrRate, cardRate));
        }
    }
}



}
