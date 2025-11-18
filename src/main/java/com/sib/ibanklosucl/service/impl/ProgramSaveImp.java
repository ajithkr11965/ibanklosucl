package com.sib.ibanklosucl.service.impl;

import com.sib.ibanklosucl.dto.DataItem;
import com.sib.ibanklosucl.dto.FormData;
import com.sib.ibanklosucl.dto.FormSave;
import com.sib.ibanklosucl.dto.TabResponse;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.*;
import com.sib.ibanklosucl.repository.program.*;
import com.sib.ibanklosucl.service.VehicleLoanProgramService;
import com.sib.ibanklosucl.service.VlSaveService;
import com.sib.ibanklosucl.service.iBankService;
import com.sib.ibanklosucl.service.vlsr.FDAccountService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanProgramIntegrationService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
public class ProgramSaveImp implements VlSaveService {

    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;
    @Autowired
    private VehicleLoanApplicantService vehicleLoanApplicantService;
    @Autowired
    private VehicleLoanProgramService vehicleLoanProgramService;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private VehicleLoanProgramRepository vehicleLoanProgramRepository;
    @Autowired
    private VehicleLoanApplicantRepository vehicleLoanApplicantRepository;
    @Autowired
    private iBankService bankService;
    @Autowired
    private FDAccountService fdAccountService;
    @Autowired
    private VehicleLoanProgramIntegrationService loanProgramIntegrationService;
    @Autowired
    private VehicleLoanProgramNRIRepository vehicleLoanProgramNriRepository;
    @Autowired
    private VehicleLoanProgramSalaryRepository vehicleLoanProgramSalaryRepository;
    @Autowired
    private ITRAlertRepository itrAlertRepository;
    @Autowired
    private BSADetailsRepository bsaDetailsRepository;
    @Autowired
    private VehicleLoanFDRepository vehicleLoanFDRepository;
    @Autowired
    private EligibilityDetailsRepository eligibilityDetailsRepository;

    @Override
    @Transactional
    public TabResponse executeSave(FormSave request) {
        try {
            String slno = request.getBody().getSlno();
            String applicantId = request.getBody().getAppid();
            String wiNum = request.getBody().getWinum();
            String incomeCheck = "";
            String monthlyorabb = "";

            // Null checks
            if (slno == null || applicantId == null || wiNum == null) {
                throw new IllegalArgumentException("Invalid input parameters");
            }

            VehicleLoanApplicant applicant = vehicleLoanApplicantService.getById(Long.valueOf(applicantId));
            VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterService.findById(Long.valueOf(slno));

            if (applicant == null || vehicleLoanMaster == null) {
                throw new IllegalArgumentException("Applicant or Vehicle Loan Master not found");
            }

            Optional<VehicleLoanProgram> optionalVehicleLoanProgram = vehicleLoanProgramService.findVehicleLoanProgrambyApplicantId(Long.valueOf(applicantId));
            VehicleLoanProgram vehicleLoanProgram;

            vehicleLoanProgram = optionalVehicleLoanProgram.orElseGet(VehicleLoanProgram::new);

            VehicleLoanProgram previousLoanProgram = new VehicleLoanProgram();
            BeanUtils.copyProperties(vehicleLoanProgram, previousLoanProgram);

            // Update VehicleLoanProgram entity based on the new loan program
            vehicleLoanProgram.setWiNum(wiNum);
            vehicleLoanProgram.setSlNo(Long.valueOf(slno));
            vehicleLoanProgram.setApplicantId(Long.valueOf(applicantId));
            vehicleLoanProgram.setReqIpAddr(CommonUtils.getIP(request.getReqip()));
            vehicleLoanProgram.setDelFlg("N");
            vehicleLoanProgram.setCmUser(usd.getPPCNo());
            vehicleLoanProgram.setCmDate(new Date());
            vehicleLoanProgram.setHomeSol(usd.getSolid());
            vehicleLoanProgram.setPan(applicant.getKycapplicants().getPanNo());
            vehicleLoanProgram.setDob(applicant.getKycapplicants().getPanDob());
            vehicleLoanProgram.setResidentType(applicant.getResidentFlg());
            applicant.setIncomeComplete("Y");
            vehicleLoanMaster.setCurrentTab(request.getBody().getCurrenttab());

            // Fetch data from the form

            for (DataItem data : request.getBody().getData()) {
                switch (data.getKey().trim()) {
                    case "incomeCheck":
                        incomeCheck = data.getValue();
                        vehicleLoanProgram.setIncomeConsidered(incomeCheck);
                        if ("N".equals(incomeCheck)) {
                            vehicleLoanProgram.setLoanProgram("NONE");
                        }
                        break;
                    case "programCode":
                        if ("N".equals(incomeCheck)) {
                            vehicleLoanProgram.setLoanProgram("NONE");
                        } else {
                            vehicleLoanProgram.setLoanProgram(data.getValue());
                            if (data.getValue().equals("SURROGATE")) {
                                vehicleLoanProgram.setAcctStmtMonths(bankService.getMisPRM("ACCT_STMT_MONTHS").getPVALUE());
                            } else if (data.getValue().equals("70/30")) {
                                vehicleLoanProgram.setDoctype("70/30");
                            }
                        }
                        break;
                    case "itravailable":
                        if (data.getValue().equals("Y")) {
                            vehicleLoanProgram.setDoctype("ITR");
                            vehicleLoanProgram.setItrFlg("Y");
                            vehicleLoanProgram.setItrMonths(bankService.getMisPRM("PRITRMONTHS").getPVALUE());
                        } else {
                            vehicleLoanProgram.setItrFlg("N");
                            vehicleLoanProgram.setItrMonths("");
                        }
                        break;
                    case "itrMonthlyGross":
                        if (vehicleLoanProgram.getLoanProgram().equals("INCOME") && ("Y").equals(vehicleLoanProgram.getItrFlg())) {
                            if (data.getValue() != null && !data.getValue().isEmpty()) {
                                vehicleLoanProgram.setAvgSal(new BigDecimal(data.getValue()));
                            }
                        }
                        break;
                    case "bsaABB":
                        if (vehicleLoanProgram.getLoanProgram().equals("SURROGATE")) {
                            vehicleLoanProgram.setAbb(new BigDecimal(data.getValue()));
                            vehicleLoanProgram.setDoctype("ABB");
                        }
                        break;
                    case "totalavailBalance":
                        if (vehicleLoanProgram.getLoanProgram().equals("LOANFD")) {
                            BigDecimal depAmount = new BigDecimal(data.getValue()).setScale(2, RoundingMode.HALF_UP);
                            vehicleLoanProgram.setDepAmt(depAmount);
                            vehicleLoanProgram.setDoctype("LOANFD");
                            boolean isCifValid = fdAccountService.validateCifId(Long.valueOf(applicantId), wiNum, applicant.getCifId());
                            if (!isCifValid) {
                                return new TabResponse("W", "Current FD details do not belong to the CIF. FD records have been marked for deletion.", applicant.getApplicantId().toString());
                            }
                        }
                        break;
                    case "form16available":
                        vehicleLoanProgram.setForm16Flg(data.getValue());
                        break;
                    case "AvgIncome":
                        if (vehicleLoanProgram.getLoanProgram().equals("INCOME") && (vehicleLoanProgram.getItrFlg() == null || "N".equals(vehicleLoanProgram.getItrFlg())) && ("R".equals(vehicleLoanProgram.getResidentType()))) {
                            if (data.getValue() != null && !data.getValue().isEmpty()) {
                                vehicleLoanProgram.setAvgSal(new BigDecimal(data.getValue()));
                            }
                            vehicleLoanProgram.setDoctype("PAYSLIP");
                            vehicleLoanProgram.setNumSalSlipFiles(bankService.getMisPRM("PRSALSLIPMON").getPVALUE());
                        }
                        break;
                    case "MonthSalary":
                        if (vehicleLoanProgram.getLoanProgram().equals("INCOME") && (vehicleLoanProgram.getItrFlg() == null || "N".equals(vehicleLoanProgram.getItrFlg())) && ("N".equals(vehicleLoanProgram.getResidentType()))) {
                            if (data.getValue() != null && !data.getValue().isEmpty()) {
                                vehicleLoanProgram.setNriNetSalary(new BigDecimal(data.getValue()));
                            }
                        }
                         break;
                     case "Avgtotal_remittance":
                        if (vehicleLoanProgram.getLoanProgram().equals("INCOME") && (vehicleLoanProgram.getItrFlg() == null || "N".equals(vehicleLoanProgram.getItrFlg())) && ("N".equals(vehicleLoanProgram.getResidentType()))) {
                            if (data.getValue() != null && !data.getValue().isEmpty()) {
                                vehicleLoanProgram.setAvgTotalRemittance(new BigDecimal(data.getValue()));
                            }
                        }
                         break;
                         case "Avgbulk_remittance":
                         if (vehicleLoanProgram.getLoanProgram().equals("INCOME") && (vehicleLoanProgram.getItrFlg() == null || "N".equals(vehicleLoanProgram.getItrFlg())) && ("N".equals(vehicleLoanProgram.getResidentType()))) {
                             if (data.getValue() != null && !data.getValue().isEmpty()) {
                                 vehicleLoanProgram.setAvgBulkRemittance(new BigDecimal(data.getValue()));
                             }
                         }
                          break;
                         case "Avgnet_remittance":
                        if (vehicleLoanProgram.getLoanProgram().equals("INCOME") && (vehicleLoanProgram.getItrFlg() == null || "N".equals(vehicleLoanProgram.getItrFlg())) && ("N".equals(vehicleLoanProgram.getResidentType()))) {
                            if (data.getValue() != null && !data.getValue().isEmpty()) {
                                vehicleLoanProgram.setAvgNetRemittance(new BigDecimal(data.getValue()));
                                vehicleLoanProgram.setAvgSal(new BigDecimal(data.getValue()));
                            }
                        }
                         break;
                    case "abb":
                        if (vehicleLoanProgram.getLoanProgram().equals("INCOME") && (vehicleLoanProgram.getItrFlg() == null || "N".equals(vehicleLoanProgram.getItrFlg())) && ("N".equals(vehicleLoanProgram.getResidentType()))) {
                            if (data.getValue() != null && !data.getValue().isEmpty()) {
                                vehicleLoanProgram.setAvgSal(new BigDecimal(data.getValue()));
                            }
                        }
                        break;
                    case "monthlyorabb":
                        if (vehicleLoanProgram.getLoanProgram().equals("INCOME") && (vehicleLoanProgram.getItrFlg() == null || "N".equals(vehicleLoanProgram.getItrFlg())) && ("N".equals(vehicleLoanProgram.getResidentType()))) {
                            if (data.getValue() != null && !data.getValue().isEmpty()) {
                                monthlyorabb = data.getValue();
                                log.info("NRI Income type selected {}", monthlyorabb);
                                if ("MonthSalary".equals(monthlyorabb)) {
                                    vehicleLoanProgram.setDoctype("MONTHLY");
                                } else if ("ABB".equals(monthlyorabb)) {
                                    vehicleLoanProgram.setDoctype("OVERSEASABB");
                                }
                            }
                        }
                         break;

                    default:
                        break;
                }
            }
            log.info("Program details program -{} doctype -{} nriflag -{} itrflag -{},nriincometype -{}", vehicleLoanProgram.getLoanProgram(), vehicleLoanProgram.getDoctype(), vehicleLoanProgram.getResidentType(), vehicleLoanProgram.getItrFlg(), vehicleLoanProgram.getDoctype());
            // Resetting the program and child tables if program is modified
            resetLoanSpecificFields(vehicleLoanProgram, previousLoanProgram);

            // Save the updated VehicleLoanProgram entity
            vehicleLoanProgramRepository.save(vehicleLoanProgram);

            // Insert new records in the child table based on the new loan program
            if (vehicleLoanProgram.getLoanProgram().equals("INCOME")) {
                if ((vehicleLoanProgram.getItrFlg() == null || "N".equals(vehicleLoanProgram.getItrFlg())) && "R".equals(vehicleLoanProgram.getResidentType())) {
                    List<VehicleLoanProgramSalary> existingEntries = vehicleLoanProgramSalaryRepository.findByApplicantIdAndWiNumAndDelFlg(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum(), "N");
                    for (VehicleLoanProgramSalary existingEntry : existingEntries) {
                        existingEntry.setDelFlg("Y");
                        existingEntry.setDelDate(new Date());
                        existingEntry.setDelUser(usd.getPPCNo());
                    }
                    vehicleLoanProgramSalaryRepository.saveAll(existingEntries);
                    List<VehicleLoanProgramSalary> vehicleLoanProgramSalaryList = new ArrayList<>();
                    for (DataItem data : request.getBody().getData()) {
                        if (data.getKey().startsWith("sal_month")) {
                            VehicleLoanProgramSalary vehicleLoanProgramSalary = new VehicleLoanProgramSalary();
                            vehicleLoanProgramSalary.setWiNum(wiNum);
                            vehicleLoanProgramSalary.setSlno(Long.valueOf(slno));
                            vehicleLoanProgramSalary.setApplicantId(Long.valueOf(applicantId));
                            vehicleLoanProgramSalary.setSalMonth(Integer.valueOf(data.getValue()));
                            vehicleLoanProgramSalary.setReqIpAddr(CommonUtils.getIP(request.getReqip()));
                            vehicleLoanProgramSalary.setDelFlg("N");
                            vehicleLoanProgramSalary.setSalaryDoc("Uploaded");
                            vehicleLoanProgramSalary.setLastModUser(usd.getPPCNo());
                            vehicleLoanProgramSalary.setLastModDate(new Date());
                            vehicleLoanProgramSalary.setHomeSol(usd.getSolid());
                            vehicleLoanProgramSalary.setVlprogramSal(vehicleLoanProgram);
                            vehicleLoanProgramSalaryList.add(vehicleLoanProgramSalary);
                        }
                    }
                    loanProgramIntegrationService.deleteAndSaveVehicleLoanProgramSalary(Long.valueOf(applicantId), wiNum, vehicleLoanProgramSalaryList);
                } else if ((vehicleLoanProgram.getItrFlg() == null || "N".equals(vehicleLoanProgram.getItrFlg())) && "N".equals(vehicleLoanProgram.getResidentType()) && "MONTHLY".equals(vehicleLoanProgram.getDoctype())) {
                    List<VehicleLoanProgramNRI> existingEntries = vehicleLoanProgramNriRepository.findByApplicantIdAndWiNumAndDelFlg(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum(), "N");
                    for (VehicleLoanProgramNRI existingNRI : existingEntries) {
                        existingNRI.setDelFlg("Y");
                        existingNRI.setDelDate(new Date());
                        existingNRI.setDelUser(usd.getPPCNo());
                    }
                    vehicleLoanProgramNriRepository.saveAll(existingEntries);
                    List<VehicleLoanProgramNRI> vehicleLoanProgramNriList = new ArrayList<>();
                    for (DataItem data : request.getBody().getData()) {
                        if (data.getKey().startsWith("MonthSalary_mon")) {
                            VehicleLoanProgramNRI vehicleLoanProgramNri = new VehicleLoanProgramNRI();
                            vehicleLoanProgramNri.setWiNum(wiNum);
                            vehicleLoanProgramNri.setSlNo(Long.valueOf(slno));
                            vehicleLoanProgramNri.setApplicantId(Long.valueOf(applicantId));
                            String[] parts = data.getValue().split("-");
                            int month = Arrays.asList(new String[]{
                                    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
                                    "Aug", "Sep", "Oct", "Nov", "Dec"
                            }).indexOf(parts[0]) + 1;
                            int year = Integer.parseInt(parts[1]);
                            vehicleLoanProgramNri.setRemitYear(year);
                            vehicleLoanProgramNri.setRemitMonth(month);
                            vehicleLoanProgramNri.setReqIpAddr(CommonUtils.getIP(request.getReqip()));
                            vehicleLoanProgramNri.setDelFlg("N");
                            vehicleLoanProgramNri.setLastModUser(usd.getPPCNo());
                            vehicleLoanProgramNri.setLastModDate(new Date());
                            vehicleLoanProgramNri.setHomeSol(usd.getSolid());
                            vehicleLoanProgramNri.setVlnri(vehicleLoanProgram);
                            vehicleLoanProgramNriList.add(vehicleLoanProgramNri);
                        } else if (data.getKey().startsWith("total_remittance")) {
                            int index = Integer.parseInt(data.getKey().replace("total_remittance", ""));
                            if (data.getValue() != null && !data.getValue().isEmpty()) {
                                vehicleLoanProgramNriList.get(index).setTotRemittance(Double.parseDouble(data.getValue()));
                            }
                        } else if (data.getKey().startsWith("bulk_remittance")) {
                            int index = Integer.parseInt(data.getKey().replace("bulk_remittance", ""));
                            if (data.getValue() != null && !data.getValue().isEmpty()) {
                                vehicleLoanProgramNriList.get(index).setBulkRemittance(Double.parseDouble(data.getValue()));
                            }
                        } else if (data.getKey().startsWith("net_remittance")) {
                            int index = Integer.parseInt(data.getKey().replace("net_remittance", ""));
                            if (data.getValue() != null && !data.getValue().isEmpty()) {
                                vehicleLoanProgramNriList.get(index).setNetRemittance(Double.parseDouble(data.getValue()));
                            }
                        }
                    }
                    vehicleLoanProgramNriRepository.saveAll(vehicleLoanProgramNriList);
                }
            } else if (vehicleLoanProgram.getLoanProgram().equals("LOANFD")) {
                List<VehicleLoanFD> vehicleLoanFDList = new ArrayList<>();
                for (DataItem data : request.getBody().getData()) {
                    if (data.getKey().startsWith("fdaccnum")) {
                        VehicleLoanFD vehicleLoanFD = new VehicleLoanFD();
                        vehicleLoanFD.setWiNum(wiNum);
                        vehicleLoanFD.setSlno(Long.valueOf(slno));
                        vehicleLoanFD.setApplicantId(Long.valueOf(applicantId));
                        vehicleLoanFD.setFdaccnum(data.getValue());
                        vehicleLoanFD.setReqIpAddr(CommonUtils.getIP(request.getReqip()));
                        vehicleLoanFD.setDelFlg("N");
                        vehicleLoanFD.setVlfd(vehicleLoanProgram);
                        vehicleLoanFD.setLastModUser(usd.getPPCNo());
                        vehicleLoanFD.setLastModDate(new Date());
                        vehicleLoanFD.setHomeSol(usd.getSolid());
                        vehicleLoanFDList.add(vehicleLoanFD);
                    }
                }
                fdAccountService.deleteAndSaveVehicleLoanFD(Long.valueOf(applicantId), wiNum, vehicleLoanFDList);
            }
            // Update related entities
            if (vehicleLoanProgram.getLoanProgram().equals("INCOME") && "Y".equals(vehicleLoanProgram.getItrFlg()) && "R".equals(vehicleLoanProgram.getResidentType())) {
                loanProgramIntegrationService.updateVehicleLoanITRWithProgram(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram);
//            } else if (vehicleLoanProgram.getLoanProgram().equals("INCOME") && vehicleLoanProgram.getItrFlg().equals("N") && vehicleLoanProgram.getResidentType().equals("R")) {
//                loanProgramIntegrationService.updateVehicleLoanSalaryWithProgram(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram);
            } else if (vehicleLoanProgram.getLoanProgram().equals("INCOME") && (vehicleLoanProgram.getItrFlg() == null || "N".equals(vehicleLoanProgram.getItrFlg())) && "N".equals(vehicleLoanProgram.getResidentType()) && "MONTHLY".equals(vehicleLoanProgram.getDoctype())) {
                loanProgramIntegrationService.updateVehicleLoanNRIWithProgram(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram);
            } else if (vehicleLoanProgram.getLoanProgram().equals("SURROGATE")) {
                loanProgramIntegrationService.updateVehicleLoanBSAWithProgram(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram);
            } else if (vehicleLoanProgram.getLoanProgram().equals("LOANFD")) {
                fdAccountService.updateVehicleLoanFDWithProgram(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram);
            }

            vehicleLoanApplicantService.resetLoanFlg(Long.valueOf(slno));
            Optional<EligibilityDetails> eligibilityDetails_ = eligibilityDetailsRepository.findBySlnoAndDelFlg(applicant.getSlno(), "N");
            EligibilityDetails eligibilityDetails = null;
            if (eligibilityDetails_.isPresent()) {
                eligibilityDetails = eligibilityDetails_.get();
                eligibilityDetails.setEligibilityFlg("N");
                eligibilityDetails.setProceedFlag("N");
                eligibilityDetailsRepository.save(eligibilityDetails);
            }
            vehicleLoanMasterService.saveLoan(vehicleLoanMaster);
            vehicleLoanApplicantService.saveApplicant(applicant);

            log.info("Vehicle Loan Program saved successfully for applicant ID: {}", applicantId);

            return new TabResponse("S", "", applicant.getApplicantId().toString());
        } catch (Exception e) {
            log.error("Error occurred while saving Vehicle Loan Program", e);
            return new TabResponse("E", "An error occurred while saving the data", null);
        }
    }

    private void resetLoanSpecificFields(VehicleLoanProgram vehicleLoanProgram, VehicleLoanProgram previousLoanProgram) {
        String previousLoanProgramCode = previousLoanProgram.getLoanProgram();
        if (!vehicleLoanProgram.getLoanProgram().equals(previousLoanProgramCode)) {
            if (vehicleLoanProgram.getLoanProgram().equals("INCOME")) {
                vehicleLoanProgram.setAbb(null);
                vehicleLoanProgram.setDepAmt(null);
                vehicleLoanProgram.setAcctStmtMonths(null);
                bsaDetailsRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                vehicleLoanProgramSalaryRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                vehicleLoanProgramNriRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                vehicleLoanFDRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
            } else if (vehicleLoanProgram.getLoanProgram().equals("NONE")) {
                vehicleLoanProgram.setAbb(null);
                vehicleLoanProgram.setDepAmt(null);
                vehicleLoanProgram.setAcctStmtMonths(null);
                vehicleLoanProgram.setAvgSal(null);
                vehicleLoanProgram.setItrFlg(null);
                vehicleLoanProgram.setItrMonths(null);
                vehicleLoanProgram.setForm16Flg(null);
                vehicleLoanProgram.setSalSlipMonths(null);
                vehicleLoanProgram.setNumSalSlipFiles(null);
                vehicleLoanProgram.setNumItrFiles(null);
                vehicleLoanProgram.setPan(null);
                vehicleLoanProgram.setDob(null);
                vehicleLoanProgram.setDoctype(null);
                bsaDetailsRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                vehicleLoanProgramSalaryRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                vehicleLoanProgramNriRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                vehicleLoanFDRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                itrAlertRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
            } else if (vehicleLoanProgram.getLoanProgram().equals("SURROGATE")) {
                vehicleLoanProgram.setAvgSal(null);
                vehicleLoanProgram.setItrFlg(null);
                vehicleLoanProgram.setItrMonths(null);
                vehicleLoanProgram.setForm16Flg(null);
                vehicleLoanProgram.setSalSlipMonths(null);
                vehicleLoanProgram.setNumSalSlipFiles(null);
                vehicleLoanProgram.setNumItrFiles(null);
                vehicleLoanProgram.setPan(null);
                vehicleLoanProgram.setDob(null);
                vehicleLoanProgram.setDepAmt(null);
                itrAlertRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                vehicleLoanProgramSalaryRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                vehicleLoanProgramNriRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                vehicleLoanFDRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
            } else if (vehicleLoanProgram.getLoanProgram().equals("70/30")) {
                vehicleLoanProgram.setAvgSal(null);
                vehicleLoanProgram.setAbb(null);
                vehicleLoanProgram.setDepAmt(null);
                vehicleLoanProgram.setAcctStmtMonths(null);
                vehicleLoanProgram.setItrFlg(null);
                vehicleLoanProgram.setItrMonths(null);
                vehicleLoanProgram.setForm16Flg(null);
                vehicleLoanProgram.setSalSlipMonths(null);
                vehicleLoanProgram.setNumSalSlipFiles(null);
                vehicleLoanProgram.setNumItrFiles(null);
                vehicleLoanProgram.setPan(null);
                vehicleLoanProgram.setDob(null);
                itrAlertRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                bsaDetailsRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                vehicleLoanProgramSalaryRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                vehicleLoanProgramNriRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                vehicleLoanFDRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
            } else if (vehicleLoanProgram.getLoanProgram().equals("LOANFD")) {
                vehicleLoanProgram.setAvgSal(null);
                vehicleLoanProgram.setAbb(null);
                vehicleLoanProgram.setAcctStmtMonths(null);
                vehicleLoanProgram.setItrFlg(null);
                vehicleLoanProgram.setItrMonths(null);
                vehicleLoanProgram.setForm16Flg(null);
                vehicleLoanProgram.setSalSlipMonths(null);
                vehicleLoanProgram.setNumSalSlipFiles(null);
                vehicleLoanProgram.setNumItrFiles(null);
                vehicleLoanProgram.setPan(null);
                vehicleLoanProgram.setDob(null);
                itrAlertRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                bsaDetailsRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                vehicleLoanProgramSalaryRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                vehicleLoanProgramNriRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
            }
        } else {
            log.info("old and new same program" + vehicleLoanProgram.getLoanProgram());
            if (vehicleLoanProgram.getLoanProgram().equals("INCOME")) {
                log.info("old and new same program check for doctype");
                log.info("doctype old-: {} new-: {}", vehicleLoanProgram.getDoctype(), previousLoanProgram.getDoctype());
                if (!vehicleLoanProgram.getDoctype().equals(previousLoanProgram.getDoctype())) {
                    log.info("doctype old-: {} new-: {}", vehicleLoanProgram.getDoctype(), previousLoanProgram.getDoctype());
                    if ("PAYSLIP".equals(previousLoanProgram.getDoctype())) {
                        vehicleLoanProgram.setSalSlipMonths(null);
                        vehicleLoanProgram.setNumSalSlipFiles(null);
                        vehicleLoanProgramSalaryRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                    } else if ("ITR".equals(previousLoanProgram.getDoctype())) {
                        vehicleLoanProgram.setItrFlg(null);
                        vehicleLoanProgram.setItrMonths(null);
                        itrAlertRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                    } else if ("OVERSEASABB".equals(previousLoanProgram.getDoctype())) {

                    } else if ("MONTHLY".equals(previousLoanProgram.getDoctype())) {
                        log.info("reset the NRI MONTHLY");
                        vehicleLoanProgramNriRepository.deleteByApplicantIdAndWiNum(vehicleLoanProgram.getApplicantId(), vehicleLoanProgram.getWiNum());
                    }
                }
            }
        }
    }


    @Override
    public TabResponse fetchData(FormData request) {
        return new TabResponse();
    }
}