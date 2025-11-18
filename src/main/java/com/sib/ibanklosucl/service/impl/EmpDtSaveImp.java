package com.sib.ibanklosucl.service.impl;

import com.sib.ibanklosucl.dto.DataItem;
import com.sib.ibanklosucl.dto.FormData;
import com.sib.ibanklosucl.dto.FormSave;
import com.sib.ibanklosucl.dto.TabResponse;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.EligibilityDetailsRepository;
import com.sib.ibanklosucl.service.VLEmploymentService;
import com.sib.ibanklosucl.service.VLEmploymentempService;
import com.sib.ibanklosucl.service.VLEmploymentoccService;
import com.sib.ibanklosucl.service.VlSaveService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EmpDtSaveImp implements VlSaveService {
    @Autowired
    private VehicleLoanApplicantService repository;
    @Autowired
    private VLEmploymentService vlEmploymentService;
    @Autowired
    private VLEmploymentempService vlEmploymentempService;

    @Autowired
    private VehicleLoanMasterService masterService;

    @Autowired
    private VLEmploymentoccService vlEmploymentoccService;
    @Autowired
    private EligibilityDetailsRepository eligibilityDetailsRepository;
    @Autowired
    private UserSessionData usd;

    @Override
    @SneakyThrows
    @Transactional
    public TabResponse executeSave(FormSave fs)  {
        String slno = fs.getBody().getSlno();
        String appid = fs.getBody().getAppid();
        String multirecdata="",employment_type="";
        VehicleLoanApplicant applicant = repository.getById(Long.valueOf(appid));
        //mark delflg for existing rec
        VLEmployment vl=vlEmploymentService.findByApplicantIdAndDelFlg(applicant.getApplicantId());
        if(vl == null)
        {
            vl=new VLEmployment();
        }

        vl.setReqIpAddr(CommonUtils.getIP(fs.getReqip()));
        vl.setSlno(Long.valueOf(slno));
        vl.setWiNum(fs.getBody().getWinum());
        vl.setApplicantId(applicant.getApplicantId());
        vl.setDelFlg("N");
        vl.setCmdate(new Date());
        vl.setCmuser(usd.getPPCNo());
        vl.setHomeSol(usd.getSolid());

        for (DataItem data : fs.getBody().getData()) {
            switch (data.getKey().trim()) {
                case "employment_type":
                    employment_type=data.getValue();
                    vl.setEmployment_type(data.getValue());
                    break;
                case "retirement_age":
                    vl.setRetirement_age(data.getValue());
                    break;
                case "total_experience":
                    vl.setTotal_experience(data.getValue());
                    break;
                case "vlemploymentemp":
                    multirecdata=data.getValue();
                    break;
            }
        }
        applicant.setEmploymentComplete("Y");

        VLEmployment savedVLEmployment = vlEmploymentService.save(vl);
        Long savedEmploymentIno = savedVLEmployment.getIno();


        if(employment_type.trim().equals("SALARIED") || employment_type.trim().equals("PENSIONER")  )
        {
            List<VLEmploymentemp> employmentemp =vlEmploymentempService.convertStringToVLEmploymentempList(multirecdata);
            vl.setVlEmploymentempList(employmentemp);
            vlEmploymentempService.saveAll(employmentemp,vl);
            vlEmploymentoccService.updateDelFlg(vl);
        }

        else if(employment_type.trim().equals("SEP")  || employment_type.trim().equals("SENP") || employment_type.trim().equals("AGRICULTURIST") )
        {

            List<VLEmploymentocc> employmentocc =vlEmploymentoccService.convertStringToVLEmploymentempList(multirecdata);
            vl.setVlEmploymentoccList(employmentocc);
            vlEmploymentoccService.saveAll(employmentocc,vl);
            vlEmploymentempService.updateDelFlg(vl.getApplicantId(),vl.getWiNum(),"N");
        }
        else if(employment_type.trim().equals("NONE") )
        {
            vlEmploymentoccService.updateDelFlg(vl);
            vlEmploymentempService.updateDelFlg(vl.getApplicantId(),vl.getWiNum(),"N");
        }

        repository.resetLoanFlg(Long.valueOf(slno));
        Optional<EligibilityDetails> eligibilityDetails_ = eligibilityDetailsRepository.findBySlnoAndDelFlg(applicant.getSlno(), "N");
        EligibilityDetails eligibilityDetails =null;
        if (eligibilityDetails_.isPresent()) {
            eligibilityDetails=eligibilityDetails_.get();
            eligibilityDetails.setEligibilityFlg("N");
            eligibilityDetails.setProceedFlag("N");
            eligibilityDetailsRepository.save(eligibilityDetails);
        }


        VehicleLoanMaster master = masterService.findById(Long.valueOf(slno));
        master.setCurrentTab(fs.getBody().getCurrenttab());
        masterService.saveLoan(master);

        repository.saveApplicant(applicant);
        return new TabResponse("S","",applicant.getApplicantId().toString());

    }

    @Override
    public TabResponse fetchData(FormData fs){
        try {
            return new TabResponse("S", "");
        }
        catch (Exception e){
            return new TabResponse("F", e.getMessage());
        }
    }
}
