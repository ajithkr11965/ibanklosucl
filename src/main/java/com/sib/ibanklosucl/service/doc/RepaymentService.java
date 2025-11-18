package com.sib.ibanklosucl.service.doc;

import com.sib.ibanklosucl.model.doc.VehicleLoanRepayment;
import com.sib.ibanklosucl.repository.doc.VehicleLoanRepaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepaymentService {
    @Autowired
    private VehicleLoanRepaymentRepository repaymentRepository;
    public VehicleLoanRepayment getRepaymentDetails(Long slno){
       return repaymentRepository.findBySlnoAndDelFlg(slno,"N").orElse(null);
    }
    public VehicleLoanRepayment getRepaymentDetails(String wiNum){
       return repaymentRepository.findByWiNumAndDelFlg(wiNum,"N").orElse(null);
    }

    public void save(VehicleLoanRepayment vlr){
         repaymentRepository.save(vlr);
    }
}
