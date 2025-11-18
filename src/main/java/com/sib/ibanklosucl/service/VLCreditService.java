package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.model.VLCredit;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.VLCreditrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VLCreditService {


    @Autowired
    private VLCreditrepository vlcreditrepository;
    @Autowired
    private FetchRepository fetchRepository;


    public VLCredit save(VLCredit vlcreditdetails) {
        return vlcreditrepository.save(vlcreditdetails);
    }

//    public VLEmployment findByWiNumAndSlno(String wiNum, Long slno) {
//        Optional<VLEmployment> employmentOptional = vlemploymentrepository.findByWiNumAndSlno(wiNum, slno);
//        return employmentOptional.orElse(null);
//    }
    public VLCredit findBySlno(Long slno) {
        return vlcreditrepository.findBySlno(slno);
    }
//
//
//    public VLEmployment getEmploymentDetails(Long slno) {
//        return vlemploymentrepository.findBySlno(slno);
//    }

    public VLCredit findByApplicantIdAndDelFlg(Long applicant_id) {
        return vlcreditrepository.findByApplicantIdAndDelFlg(applicant_id,"N");
    }

    public VLCredit getVLCreditDetails(String wiNum, Long slNo) {
        return vlcreditrepository.findVLCreditDetails(wiNum, slNo).get(0);
    }

    public List<VLCredit> getVLCreditDetailstoCheckBureua(String wiNum, Long slNo) {
        return vlcreditrepository.findVLCreditDetails(wiNum, slNo);
    }



}
