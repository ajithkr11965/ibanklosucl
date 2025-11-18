package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.CommonDTO;
import com.sib.ibanklosucl.dto.FileUploadForm;
import com.sib.ibanklosucl.dto.ResponseDTO;
import com.sib.ibanklosucl.dto.TabResponse;
import org.springframework.stereotype.Service;

@Service
public interface CommonService {
    TabResponse processData(CommonDTO request) ;
    ResponseDTO processDataDoc(FileUploadForm request) ;


}