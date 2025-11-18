package com.sib.ibanklosucl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.model.VLCredit;
import com.sib.ibanklosucl.model.VLFinliab;
import com.sib.ibanklosucl.repository.VLFinliabrepository;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class VLFinliabService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private VLFinliabrepository vlFinliabrepository;

    @Autowired
    private UserSessionData usd;


    @Transactional
    public void saveAll(List<VLFinliab> vlFinliabdetails, VLCredit vlcredit) {
        for (VLFinliab finliab : vlFinliabdetails) {
            List<VLFinliab> vltmplist = vlFinliabrepository.findVLFinliabByApplicantIdAndWiNumAndDelFlg(vlcredit.getApplicantId(), vlcredit.getWiNum(), "N");
            if (vltmplist != null && !vltmplist.isEmpty()) {
                for (VLFinliab vltmp : vltmplist) {
                    vltmp.setDelFlg("Y");
                    vlFinliabrepository.saveAndFlush(vltmp);
                }

            }
        }
        for (VLFinliab finliabdetails : vlFinliabdetails) {
            if (finliabdetails.getBankName() != null && !finliabdetails.getBankName().isEmpty()) {
                log.info("finliabdetails {}", finliabdetails);
                finliabdetails.setDelDate(new Date());
                finliabdetails.setDelUser(usd.getPPCNo());
                finliabdetails.setDelFlg("N");
                // finliabdetails.setReqIpAddr(CommonUtils.getIP(fs.getReqip()));
                finliabdetails.setApplicantId(vlcredit.getApplicantId());
                finliabdetails.setSlno(vlcredit.getSlno());
                finliabdetails.setWiNum(vlcredit.getWiNum());
                finliabdetails.setVlliabkey(vlcredit);
                if(finliabdetails.getModifiedEmi() == null) {
                    finliabdetails.setModifiedEmi(finliabdetails.getEmi());
                }
                vlFinliabrepository.save(finliabdetails);
            }

        }
    }

    public void updateDelFlg(Long applicantId, String wiNum, String delFlg) {
        List<VLFinliab> vltmplist = vlFinliabrepository.findVLFinliabByApplicantIdAndWiNumAndDelFlg(applicantId, wiNum, delFlg);
        if (vltmplist != null && !vltmplist.isEmpty()) {
            for (VLFinliab vltmp : vltmplist) {
                vltmp.setDelFlg("Y");
                vlFinliabrepository.saveAndFlush(vltmp);
            }
        }
    }


    public List<VLFinliab> convertStringToVLEmploymentempList(String value) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(value, new TypeReference<List<VLFinliab>>() {
        });
    }
}
