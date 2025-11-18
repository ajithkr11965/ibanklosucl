package com.sib.ibanklosucl.service.vlsr;

import com.sib.ibanklosucl.dto.DedupRequestDTO;
import com.sib.ibanklosucl.dto.LosApiResponse;
import com.sib.ibanklosucl.model.LosDedupeEntity;
import com.sib.ibanklosucl.model.Misrct;
import com.sib.ibanklosucl.repository.LosDedupeRepository;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class LosDedupeService {

    @Autowired
    private LosDedupeRepository losDedupeRepository;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private MisrctService misrctService;

    @Transactional
    public void saveAll(List<LosApiResponse.BpmdedupeData> dataList,DedupRequestDTO requestDTO) {
        List<LosDedupeEntity> entities = dataList.stream().map(dto -> toEntity(dto,requestDTO)).toList();
        losDedupeRepository.saveAll(entities);
    }
    @Transactional
    public void saveMsg(String msg,DedupRequestDTO requestDTO) {
        LosDedupeEntity entity=new LosDedupeEntity();
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
        losDedupeRepository.save(entity);
    }
    @Transactional
    public void updateActiveFlag(String appid) {
        losDedupeRepository.updateActiveFlagToInactive(Long.valueOf(appid));
    }

    public List<LosDedupeEntity> getLosByID(Long slno) {
        return losDedupeRepository.findAllBySlnoAndDelFlagAndActiveFlag(slno,"N","Y");
    }


    public List<Misrct> getSingleDedupeRelations(String code) {
        return misrctService.getCodeValuesByType(code);
    }


    private LosDedupeEntity toEntity(LosApiResponse.BpmdedupeData dto, DedupRequestDTO requestDTO) {
        LosDedupeEntity entity = new LosDedupeEntity();
        entity.setWiName(dto.getWi_name());
        entity.setCustName(dto.getCustName());
        entity.setLoanType(dto.getLoanType());
        entity.setWiStatus(dto.getWiStatus());
        entity.setAppType(dto.getAppType());
        entity.setRejectReason(dto.getRejectReason());
        entity.setDoRemarks(dto.getDoRemarks());
        entity.setDob(dto.getDOB());
        entity.setAadhaar(dto.getAadhaar());
        entity.setPanNo(dto.getPanNo());
        entity.setVoterID(dto.getVoterID());
        entity.setPassportNo(dto.getPassportNo());
        entity.setDriveLic(dto.getDriveLic());
        entity.setGstNo(dto.getGstNo());
        entity.setCorpID(dto.getCorpID());
        entity.setMessage(dto.getMessage());
        entity.setActiveFlag("Y");
        entity.setDelFlag("N");
        entity.setDedupflag("Y");
        entity.setDedupmsg("");
        entity.setCmuser(usd.getPPCNo());
        entity.setCmdate(new Date());
        entity.setHomeSol(usd.getSolid());
        entity.setWiNum(requestDTO.getWinum());
        entity.setApplicantId(Long.valueOf(requestDTO.getAppid()));
        entity.setSlno(Long.valueOf(requestDTO.getSlno()));
        return entity;
    }

}
