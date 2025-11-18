package com.sib.ibanklosucl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.model.VLCredit;
import com.sib.ibanklosucl.model.VLFinasset;
import com.sib.ibanklosucl.repository.VLFinastrepository;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class VLFinastService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private VLFinastrepository vlFinastrepository;

    @Autowired
    private UserSessionData usd;


//    public VLEmploymentemp save(VLEmploymentemp vlemploymentempdetails) {
//        return vlemploymentemprepository.save(vlemploymentempdetails);
//    }

    @Transactional
    public void saveAll(List<VLFinasset> vlFinastdetails, VLCredit vlcredit)
    {
        for (VLFinasset finast : vlFinastdetails)
        {
            List<VLFinasset> vltmplist = vlFinastrepository.findVLFinassetByApplicantIdAndWiNumAndDelFlg(vlcredit.getApplicantId(),vlcredit.getWiNum(),"N");
            if(vltmplist !=null && !vltmplist.isEmpty())
            {
                for(VLFinasset vltmp : vltmplist)
                {
                    vltmp.setDelFlg("Y");
                    vlFinastrepository.saveAndFlush(vltmp);
                }

            }
        }
        for (VLFinasset finlastdetails : vlFinastdetails)
        {
            if(finlastdetails.getAssetType()!=null && !finlastdetails.getAssetType().isEmpty() )
            {
                finlastdetails.setDelDate(new Date());
                finlastdetails.setDelUser(usd.getPPCNo());
                finlastdetails.setDelFlg("N");
                finlastdetails.setApplicantId(vlcredit.getApplicantId());
                finlastdetails.setSlno(vlcredit.getSlno());
                finlastdetails.setWiNum(vlcredit.getWiNum());
                finlastdetails.setVlastkey(vlcredit);
                vlFinastrepository.save(finlastdetails);
            }

        }
    }

//    public List<VLEmploymentemp> findByEmploymentInoAndDelFlg(Long employmentIno, String delFlg) {
//        return vlemploymentemprepository.findByEmploymentInoAndDelFlg(employmentIno, delFlg);
//    }

    public void updateDelFlg(Long applicantId,String wiNum, String delFlg)
    {
        List<VLFinasset> vltmplist = vlFinastrepository.findVLFinassetByApplicantIdAndWiNumAndDelFlg(applicantId,wiNum,delFlg);
        if(vltmplist !=null && !vltmplist.isEmpty())
        {
            for(VLFinasset vltmp : vltmplist)
            {
                vltmp.setDelFlg("Y");
                vlFinastrepository.saveAndFlush(vltmp);
            }
        }
    }


    public List<VLFinasset> convertStringToVLEmploymentempList(String value) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(value, new TypeReference<List<VLFinasset>>(){});
    }
}
