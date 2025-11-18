package com.sib.ibanklosucl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.model.VLEmployment;
import com.sib.ibanklosucl.model.VLEmploymentemp;
import com.sib.ibanklosucl.repository.VLEmploymentempRepository;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class VLEmploymentempService {

    @Autowired
    private VLEmploymentempRepository vlemploymentemprepository;

    @Autowired
    private UserSessionData usd;


    public VLEmploymentemp save(VLEmploymentemp vlemploymentempdetails) {
        return vlemploymentemprepository.save(vlemploymentempdetails);
    }

    @Transactional
    public void saveAll(List<VLEmploymentemp> vlemploymentempdetails, VLEmployment vlEmployment)
    {
        for (VLEmploymentemp vlEmploymentemp : vlemploymentempdetails)
        {
            List<VLEmploymentemp> vltmplist = vlemploymentemprepository.findVLEmploymentempByApplicantIdAndWiNumAndDelFlg(vlEmployment.getApplicantId(),vlEmployment.getWiNum(),"N");
            if(vltmplist !=null && !vltmplist.isEmpty())
            {
                for(VLEmploymentemp vltmp : vltmplist)
                {
                    vltmp.setDelFlg("Y");
                    vlemploymentemprepository.saveAndFlush(vltmp);
                }

            }
        }
        for (VLEmploymentemp vlEmploymentemp : vlemploymentempdetails)
        {
            vlEmploymentemp.setApplicantId(vlEmployment.getApplicantId());
            vlEmploymentemp.setWiNum(vlEmployment.getWiNum());
            vlEmploymentemp.setSlno(vlEmployment.getSlno());
            vlEmploymentemp.setCmdate(new Date());
            vlEmploymentemp.setCmuser(usd.getPPCNo());
            vlEmploymentemp.setHomeSol(usd.getSolid());
            vlEmploymentemp.setDelFlg("N");
            vlEmploymentemp.setVlempkey(vlEmployment);
            vlemploymentemprepository.save(vlEmploymentemp);
        }
    }

    public List<VLEmploymentemp> findByEmploymentInoAndDelFlg(Long applicantId,String wiNum, String delFlg) {
        return vlemploymentemprepository.findVLEmploymentempByApplicantIdAndWiNumAndDelFlg(applicantId, wiNum, delFlg);
    }

    public void updateDelFlg(Long applicantId,String wiNum, String delFlg)
    {
        List<VLEmploymentemp> vltmplist = vlemploymentemprepository.findVLEmploymentempByApplicantIdAndWiNumAndDelFlg(applicantId, wiNum, delFlg);
        if(vltmplist !=null && !vltmplist.isEmpty())
        {
            for(VLEmploymentemp vltmp : vltmplist)
            {
                vltmp.setDelFlg("Y");
                vlemploymentemprepository.saveAndFlush(vltmp);
            }
        }
    }


    public List<VLEmploymentemp> convertStringToVLEmploymentempList(String value) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(value, new TypeReference<List<VLEmploymentemp>>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
