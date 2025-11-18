package com.sib.ibanklosucl.service.vlsr;

import com.sib.ibanklosucl.dto.DedupRequestDTO;
import com.sib.ibanklosucl.dto.LosApiResponse;
import com.sib.ibanklosucl.model.FinDedupEntity;

import com.sib.ibanklosucl.repository.FinacleDedupeRepository;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
@Service
public class FinacleLosDedupeService {

    @Autowired
    private FinacleDedupeRepository finacleLosDedupeService;
    @Autowired
    private UserSessionData usd;

    @Transactional
    public void saveAll(List<LosApiResponse.customer> dataList, DedupRequestDTO requestDTO) {
        List<FinDedupEntity> entities = dataList.stream().map(dto -> toEntity(dto,requestDTO)).toList();
        finacleLosDedupeService.saveAll(entities);
    }
    @Transactional
    public void saveMsg(String msg,DedupRequestDTO requestDTO) {
        FinDedupEntity entity=new FinDedupEntity();
        entity.setActiveFlag("Y");
        entity.setDelFlag("N");
        entity.setDedupflag("N");
        entity.setDedupmsg(msg);
        entity.setWiNum(requestDTO.getWinum());
        entity.setApplicantId(Long.valueOf(requestDTO.getAppid()));
        entity.setSlno(Long.valueOf(requestDTO.getSlno()));
        entity.setCmuser(usd.getPPCNo());
        entity.setCmdate(new Date());
        entity.setHomeSol(usd.getSolid());
        finacleLosDedupeService.save(entity);
    }
    @Transactional
    public void updateActiveFlag(String appid) {
        finacleLosDedupeService.updateActiveFlagToInactive(Long.valueOf(appid));
    }

       public List<FinDedupEntity> getFinDupByID(Long slno) {
        return finacleLosDedupeService.findAllBySlnoAndDelFlagAndActiveFlag(slno,"N","Y");
    }
       public List<FinDedupEntity> getFinDupByAppID(Long slno,Long appid) {
        return finacleLosDedupeService.findAllBySlnoAndDelFlagAndActiveFlagAndApplicantId(slno,"N","Y",appid);
    }
       public boolean isDeduped(Long slno) {
        return finacleLosDedupeService.countBySlnoAndDelFlagAndActiveFlag(slno,"N","Y")>0;
    }

    public List<FinDedupEntity> getLosByID(Long slno) {
        return finacleLosDedupeService.findAllBySlnoAndDelFlagAndActiveFlagAndDedupflag(slno,"N","Y","Y");
    }
    private FinDedupEntity toEntity(LosApiResponse.customer dto,DedupRequestDTO requestDTO) {
        FinDedupEntity entity = new FinDedupEntity();
        entity.setCreatedChannelId(dto.getCreated_channel_id());
        entity.setSuspendStatus(dto.getSuspend_status());
        entity.setCustomerid(dto.getCustomerid());
        entity.setEmailid(dto.getEmailid());
        entity.setMobilephone(dto.getMobilephone());
        entity.setVoterid(dto.getVoterid());
        entity.setAadharRefNo(dto.getAadhar_ref_no());
        entity.setPan(dto.getPan());
        entity.setDob(dto.getDob());
        entity.setName(dto.getName());
        entity.setTdsCustomerid(dto.getTds_customerid());
        entity.setActiveFlag("Y");
        entity.setDelFlag("N");
        entity.setDedupflag("Y");
        entity.setDedupmsg("");
        entity.setWiNum(requestDTO.getWinum());
        entity.setApplicantId(Long.valueOf(requestDTO.getAppid()));
        entity.setSlno(Long.valueOf(requestDTO.getSlno()));
        entity.setCmuser(usd.getPPCNo());
        entity.setCmdate(new Date());
        entity.setHomeSol(usd.getSolid());
        return entity;
    }

}
