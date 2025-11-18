package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.model.VehicleLoanDetails;
import com.sib.ibanklosucl.repository.VehicleLoanDetailsRepository;
import com.sib.ibanklosucl.repository.VehicleLoanDetailsRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class VehicleLoanDetailsService {

    @Autowired
    private VehicleLoanDetailsRepository loanDetailsRepository;

    @Autowired
    private VehicleLoanDetailsRepositoryImpl loanDetailsImpl;

    public VehicleLoanDetails findBySlnoAndDelFlg(Long slno) {
        return loanDetailsRepository.findBySlnoAndDelFlg( slno,"N");

    }
    public List<VehicleLoanDetails> findByWiNumAndSlno(String wiNum, Long slno) {
        return loanDetailsRepository.findByWiNumAndSlnoAndDelFlg(wiNum, slno,"N");
    }
@Transactional
    public VehicleLoanDetails save(VehicleLoanDetails loanDetails) {
       // loanDetailsRepository.deleteRest(loanDetails.getSlno());
        return loanDetailsRepository.save(loanDetails);
    }

    public boolean ApplicantNRI(String wiNum, String slno) {
        return  loanDetailsImpl.isApplicantNRI(wiNum,slno);

    }
    public boolean isProgramSet(String wiNum, String slno) {
        return  loanDetailsImpl.isProgramSet(wiNum,slno);

    }
    public boolean isBureauValid(String wiNum, String slno) {
        return  loanDetailsImpl.isBureauValid(wiNum,slno);

    }

    public boolean checkLoanAmount(String P_Name, String Emp_name, Float LoanAmount) {
        return loanDetailsImpl.checkLoanAmount(P_Name, Emp_name, LoanAmount);
    }

    public String getViewData(String P_Name, String Emp_name, Float LoanAmount) {
        return loanDetailsImpl.getViewDataAmtLimit(P_Name, Emp_name, LoanAmount);
    }

    public boolean checkLoanTenor(String P_Name, String Emp_name, int tenor) {
        return loanDetailsImpl.checkTenor(P_Name, Emp_name, tenor);
    }

    public String getViewDataTenor(String P_Name, String Emp_name, int tenor) {
        return loanDetailsImpl.getViewDataTenorLimit(P_Name, Emp_name, tenor);
    }

}
