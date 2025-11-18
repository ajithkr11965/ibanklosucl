package com.sib.ibanklosucl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.model.VLEmployment;
import com.sib.ibanklosucl.model.VLEmploymentocc;
import com.sib.ibanklosucl.repository.VLEmploymentoccRepository;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class VLEmploymentoccService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private VLEmploymentoccRepository vlemploymentoccrepository;

    @Autowired
    private UserSessionData usd;


    public VLEmploymentocc save(VLEmploymentocc vlemploymentoccdetails) {
        return vlemploymentoccrepository.save(vlemploymentoccdetails);
    }

    @Transactional
    public void saveAll(List<VLEmploymentocc> vlemploymentoccdetails, VLEmployment vlEmployment)
    {
        for (VLEmploymentocc vlEmploymentemp : vlemploymentoccdetails)
        {
            List<VLEmploymentocc> vltmplist = vlemploymentoccrepository.findByApplicantIdAndWiNumAndDelFlg(vlEmployment.getApplicantId(),vlEmployment.getWiNum(),"N");
            if(vltmplist !=null && !vltmplist.isEmpty())
            {
                for(VLEmploymentocc vltmp : vltmplist)
                {
                    vltmp.setDelFlg("Y");
                    vlemploymentoccrepository.saveAndFlush(vltmp);
                }

            }
        }
        for (VLEmploymentocc vlEmploymentocc : vlemploymentoccdetails)
        {

            vlEmploymentocc.setApplicantId(vlEmployment.getApplicantId());
            vlEmploymentocc.setWiNum(vlEmployment.getWiNum());
            vlEmploymentocc.setSlno(vlEmployment.getSlno());
            vlEmploymentocc.setCmdate(new Date());
            vlEmploymentocc.setCmuser(usd.getPPCNo());
            vlEmploymentocc.setHomeSol(usd.getSolid());
            vlEmploymentocc.setDelFlg("N");
            vlEmploymentocc.setVlEmployment(vlEmployment);
            vlemploymentoccrepository.save(vlEmploymentocc);
        }
    }




    public void updateDelFlg(VLEmployment vlEmployment)
    {
        List<VLEmploymentocc> vltmplist = vlemploymentoccrepository.findByApplicantIdAndWiNumAndDelFlg(vlEmployment.getApplicantId(),vlEmployment.getWiNum(),"N");
        if(vltmplist !=null && !vltmplist.isEmpty())
        {
            for(VLEmploymentocc vltmp : vltmplist)
            {
                vltmp.setDelFlg("Y");
                vlemploymentoccrepository.save(vltmp);
            }
        }
    }



    public List<VLEmploymentocc> convertStringToVLEmploymentempList(String value){

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(value, new TypeReference<List<VLEmploymentocc>>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
