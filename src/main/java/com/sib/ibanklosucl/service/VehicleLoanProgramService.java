package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.AnnualIncomeAndBankBalance;
import com.sib.ibanklosucl.model.VehicleLoanBSA;
import com.sib.ibanklosucl.model.VehicleLoanProgram;
import com.sib.ibanklosucl.model.VehicleLoanProgramNRI;
import com.sib.ibanklosucl.repository.VehicleLoanProgramRepository;
import com.sib.ibanklosucl.repository.program.BSADetailsRepository;
import com.sib.ibanklosucl.service.vlsr.FDAccountService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VehicleLoanProgramService {

    @Autowired
    private VehicleLoanProgramRepository repository;
    @Autowired
    private FDAccountService fdAccountService;
    @Autowired
    private BSADetailsRepository bsaDetailsRepository;
    @Autowired
    private UserSessionData usd;

    @Transactional
    public VehicleLoanProgram insertVehicleLoanProgram(VehicleLoanProgram vehicleLoanProgram) {
        vehicleLoanProgram= repository.save(vehicleLoanProgram);
        fdAccountService.updateVehicleLoanFDWithProgram(vehicleLoanProgram.getApplicantId(),vehicleLoanProgram);
        return vehicleLoanProgram;
    }

    @Transactional
    public VehicleLoanProgram updateVehicleLoanProgram(String ApplicantID, VehicleLoanProgram updatedProgram) {
        Optional<VehicleLoanProgram> optionalProgram = repository.findByApplicantIdAndDelFlg(Long.parseLong(ApplicantID),"N");
        if (optionalProgram.isPresent()) {
            VehicleLoanProgram existingProgram = optionalProgram.get();
            // Copy properties from updatedProgram to existingProgram
            existingProgram.setApplicantId(updatedProgram.getApplicantId());
            existingProgram.setReqIpAddr(updatedProgram.getReqIpAddr());
            existingProgram.setIncomeConsidered(updatedProgram.getIncomeConsidered());
            existingProgram.setLoanProgram(updatedProgram.getLoanProgram());
            existingProgram.setDoctype(updatedProgram.getDoctype());
            existingProgram.setDob(updatedProgram.getDob());
            existingProgram.setPan(updatedProgram.getPan());
            existingProgram.setSalSlipMonths(updatedProgram.getSalSlipMonths());
            existingProgram.setAcctStmtMonths(updatedProgram.getAcctStmtMonths());
            existingProgram.setItrMonths(updatedProgram.getItrMonths());
            existingProgram.setNumSalSlipFiles(updatedProgram.getNumSalSlipFiles());
            existingProgram.setNumAcctStmtFiles(updatedProgram.getNumAcctStmtFiles());
            existingProgram.setNumItrFiles(updatedProgram.getNumItrFiles());
            existingProgram.setResidentType(updatedProgram.getResidentType());
            existingProgram.setItrFlg(updatedProgram.getItrFlg());
            existingProgram.setForm16Flg(updatedProgram.getForm16Flg());
            existingProgram.setAvgSal(updatedProgram.getAvgSal());
            existingProgram.setAbb(updatedProgram.getAbb());
            existingProgram.setSanctionDate(updatedProgram.getSanctionDate());
            existingProgram.setDepAmt(updatedProgram.getDepAmt());
            existingProgram.setCmUser(updatedProgram.getCmUser());
            existingProgram.setCmDate(updatedProgram.getCmDate());
            existingProgram.setDelFlg(updatedProgram.getDelFlg());
            existingProgram.setHomeSol(updatedProgram.getHomeSol());
            existingProgram.setVehicleLoanFDList(updatedProgram.getVehicleLoanFDList());
            repository.save(existingProgram);
            fdAccountService.updateVehicleLoanFDWithProgram(updatedProgram.getApplicantId(),updatedProgram);
            return existingProgram;
        } else {
            throw new EntityNotFoundException("Vehicle Loan Program not found");
        }
    }

    @Transactional(readOnly = true)
    public List<VehicleLoanProgram> getVehicleLoanProgram(String wiNum, Long slNo) {
        return repository.findByWiNumAndSlNoAndDelFlg(wiNum, slNo,"N");
    }

    public BigDecimal getSumOfAbbWhereDelFlgIsN(String wiNum, Long slNo) {
        Optional<BigDecimal> amt=repository.findSumOfAbbWhereDelFlgIsN(wiNum, slNo);
        return amt.orElse(BigDecimal.ZERO);
    }
    public BigDecimal getSumOfDepAmtWhereDelFlgIsN(String wiNum, Long slNo) {
        Optional<BigDecimal> amt=repository.findSumOfDepAmtWhereDelFlgIsN(wiNum, slNo);
        return amt.orElse(BigDecimal.ZERO);
    }

    public VehicleLoanProgram findVehicleLoanProgrambyAppID(Long ApplicantID) {
        return repository.findByApplicantIdAndDelFlg(ApplicantID, "N")
                .orElseThrow(() -> new EntityNotFoundException("Vehicle Loan Program not found for ApplicantID: " + ApplicantID));

    }
    public Optional<VehicleLoanProgram> findVehicleLoanProgrambyApplicantId(Long ApplicantID) {
        return repository.findByApplicantIdAndDelFlg(ApplicantID,"N");
    }

    public boolean validateProgram(String winum, Long slno) {
        List<VehicleLoanProgram> vps = getVehicleLoanProgram(winum, slno);
        vps = vps.stream().filter(program -> !"NONE".equals(program.getLoanProgram())).collect(Collectors.toList());
        return vps.stream().map(VehicleLoanProgram::getLoanProgram).distinct().count()==1;
    }

    public String getProgramName(String winum, Long slno) {
        List<VehicleLoanProgram> vps = getVehicleLoanProgram(winum, slno);
        vps = vps.stream().filter(program -> !"NONE".equals(program.getLoanProgram())).collect(Collectors.toList());
        return vps.stream().map(VehicleLoanProgram::getLoanProgram).distinct().findFirst().orElse(null);
    }

    public AnnualIncomeAndBankBalance getAnnualIncomeAndBankBalance( Long slNo) {
        return repository.findAnnualIncomeAndBankBalance( slNo);
    }

    public Map<String, Object> getProgramDetails(VehicleLoanProgram program,Long slNo) {
        if (program == null) {
            return null;
        }

        Map<String, Object> details = new HashMap<>();
        details.put("loanProgram", program.getLoanProgram());
        details.put("incomeConsidered", program.getIncomeConsidered());
        details.put("doctype", program.getDoctype());
        details.put("residentialStatus",program.getResidentType());

        switch (program.getLoanProgram()) {
            case "INCOME":
                details.put("itrFlg", program.getItrFlg());
                details.put("form16Flg", program.getForm16Flg());
                details.put("vehicleLoanProgramNRIList",program.getVehicleLoanProgramNRIList());
                details.put("vehicleLoanITRList", program.getVehicleLoanITRList());
                details.put("vehicleLoanProgramSalaryList", program.getVehicleLoanProgramSalaryList());
                details.put("avgSal",program.getAvgSal());

                // Add NRI remittance details if resident type is NRI
                if ("N".equals(program.getResidentType())) {
                    Map<String, Object> nriRemittanceDetails = new HashMap<>();

                    // Add monthly salary and averages
                    nriRemittanceDetails.put("monthlySalary", program.getNriNetSalary());
                    nriRemittanceDetails.put("avgTotalRemittance", program.getAvgTotalRemittance());
                    nriRemittanceDetails.put("avgBulkRemittance", program.getAvgBulkRemittance());
                    nriRemittanceDetails.put("avgNetRemittance", program.getAvgNetRemittance());

                    // Format remittance months data for JavaScript
                    if (program.getVlnriList() != null && !program.getVlnriList().isEmpty()) {
                        List<Map<String, Object>> remittanceMonths = new ArrayList<>();
                        for (VehicleLoanProgramNRI nri : program.getVlnriList()) {
                            if ("N".equals(nri.getDelFlg())) {
                                Map<String, Object> monthData = new HashMap<>();
                                // Format as "yyyy-MM" to match JSP format
                                monthData.put("monthYear", String.format("%d-%02d", nri.getRemitYear(), nri.getRemitMonth()));
                                monthData.put("totalRemittance", nri.getTotRemittance());
                                monthData.put("bulkRemittance", nri.getBulkRemittance());
                                monthData.put("netRemittance", nri.getNetRemittance());
                                remittanceMonths.add(monthData);
                            }
                        }
                        nriRemittanceDetails.put("remittanceMonths", remittanceMonths);
                    }

                    details.put("nriRemittanceDetails", nriRemittanceDetails);
                }
                break;
            case "SURROGATE":
                details.put("vehicleLoanBSAList", program.getVehicleLoanBSAList());
                details.put("abb",program.getAbb());
                break;
            case "60/40":
                // Add 60/40 specific details
                break;
            case "LOANFD":
                Map<String, Object> fdAccountResponses = fdAccountService.getAccountDetails(program.getApplicantId(), program.getWiNum(),slNo);
                details.put("vehicleLoanFDList", fdAccountResponses);
                details.put("depAmt",program.getDepAmt());
                break;
        }

        return details;
    }
        public Optional<VehicleLoanProgram> findProgrambyApplicantId(Long ApplicantID) {
        return repository.findByApplicantIdAndDelFlg(ApplicantID, "N");
    }

    public int markNullProgramInoRecordsAsDeleted(Long applicantId, String wiNum) {
        List<String> statementTypes = Arrays.asList("SURROGATE-1", "SURROGATE-2", "SURROGATE-3");
        List<VehicleLoanBSA> recordsWithNullProgramIno = bsaDetailsRepository.findRecordsWithNullProgramIno(
                applicantId,
                wiNum,
                "N", // Only consider records that are not already deleted
                statementTypes
        );

        int updatedCount = 0;

        if (!recordsWithNullProgramIno.isEmpty()) {
            for (VehicleLoanBSA record : recordsWithNullProgramIno) {
                record.setDelFlg("Y");
                record.setDelDate(new Date());
                record.setDelUser(usd.getPPCNo());
                bsaDetailsRepository.save(record);
                updatedCount++;
            }
        }

        return updatedCount;
    }

}
