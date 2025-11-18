package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.VLEmploymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VLEmploymentService {


    @Autowired
    private VLEmploymentRepository vlemploymentrepository;


    public VLEmployment save(VLEmployment vlemploymentdetails) {
        return vlemploymentrepository.save(vlemploymentdetails);
    }

    public List<VLEmployment> findByWiNumAndSlno(String wiNum, Long slno) {
        List<VLEmployment> employmentOptional = vlemploymentrepository.findBySlnoWithApplicants( slno);
        return employmentOptional;
    }
    public List<VLEmployment> findBySlno(Long slno) {
        return vlemploymentrepository.findBySlnoWithApplicants(slno);
    }


    public VLEmployment getEmploymentDetails(Long slno) {
        return vlemploymentrepository.findBySlno(slno);
    }

    public VLEmployment findByApplicantIdAndDelFlg(Long applicant_id) {
        return vlemploymentrepository.findByApplicantIdAndDelFlg(applicant_id,"N");
    }





}
