package com.sib.ibanklosucl.service.impl;


import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.user.VehicleCustomUpload;
import com.sib.ibanklosucl.repository.CustomUploadRepository;
import com.sib.ibanklosucl.service.CommonService;
import com.sib.ibanklosucl.service.bpmsr.BpmService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service

public class CustDocSaveImpl implements CommonService {

    @Autowired
    private BpmService bpmService;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private CustomUploadRepository customUploadRepository;

    @Override
    public TabResponse processData(CommonDTO request) {
        CommonDTO.DocUpload dto=request.getDocRequest();
        String childFlg=dto.getFoldername().equals("NA")?"N":"Y";

        VehicleCustomUpload customUpload=new VehicleCustomUpload();
        customUpload.setWiNum(dto.getWinum());
        customUpload.setFolderName(dto.getFoldername());
        customUpload.setChildFlg(childFlg);
        customUpload.setCmUser(usd.getPPCNo());
        customUpload.setCmDate(new Date());
        customUpload.setIpaddress(usd.getRemoteIP());
        customUpload.setRemarks(dto.getRemarks());
        customUpload.setFileName(dto.getFileName());
        customUpload.setFileExt(dto.getFileExtension());
        customUpload.setSolID(usd.getSolid());
        customUploadRepository.save(customUpload);
        TabResponse tb=bpmService.BpmUpload(bpmService.bpmRequest(dto.getWinum(),dto.getBase64Data(),childFlg,dto.getFoldername(),dto.getFileName()+"_"+customUpload.getIno(),dto.getFileExtension()));
        if(!"S".equalsIgnoreCase(tb.getStatus())){
            throw new ValidationException(ValidationError.COM001,tb.getMsg());
        }
        return new TabResponse("S","File Uploaded Successfully");
    }

    @Override
    public ResponseDTO processDataDoc(FileUploadForm request) {
        return null;
    }
}
