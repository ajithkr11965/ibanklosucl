package com.sib.ibanklosucl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sib.ibanklosucl.dto.FormData;
import com.sib.ibanklosucl.dto.FormSave;
import com.sib.ibanklosucl.dto.TabResponse;
import org.springframework.stereotype.Service;

@Service
public interface VlSaveService{
        TabResponse executeSave(FormSave request) throws JsonProcessingException;
        TabResponse fetchData(FormData request);

}
