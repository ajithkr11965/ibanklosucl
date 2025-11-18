package com.sib.ibanklosucl.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.dto.DataItem;
import com.sib.ibanklosucl.dto.FormData;
import com.sib.ibanklosucl.dto.FormSave;
import com.sib.ibanklosucl.dto.TabResponse;
import com.sib.ibanklosucl.model.MaterialListData;
import com.sib.ibanklosucl.service.VlSaveService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import com.sib.ibanklosucl.repository.MaterialListDataRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MaterialListSaveImpl implements VlSaveService {
    @Autowired
    private VehicleLoanApplicantService repository;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private MaterialListDataRepository materialListDataRepository;
    @Autowired
    private ObjectMapper mapper;
    @Override
    @SneakyThrows
    @Transactional(rollbackOn = Exception.class)
    public TabResponse executeSave(FormSave fs)  {
        String slno = fs.getBody().getSlno();
        String wiNum = fs.getBody().getWinum();
        String crtdata="";
        List<DataItem> dataList = fs.getBody().getData();
        for (DataItem item : dataList) {
            String key = item.getKey();
            String value = item.getValue();
            switch (key) {
                case "crtdata":
                    crtdata=value;
                    break;
            }
        }
        List<MaterialListData> materialListData=  materialListDataRepository.findBySlno(Long.valueOf(slno));
        if(materialListData!=null)
            materialListDataRepository.deleteAll(materialListData);
        List<MaterialListData> input=convertStringToList(crtdata);
        materialListDataRepository.saveAll(input);
        return new TabResponse("S","Data updated successfully");
    }


    public List<MaterialListData> convertStringToList(String value) {
        try {
            List<MaterialListData> emplist= mapper.readValue(value, new TypeReference<List<MaterialListData>>(){});
            return emplist.stream()
                    .peek(emp -> {
                                emp.setCmdate(new Date());
                                emp.setCmuser(usd.getPPCNo());
                                emp.setSolId(usd.getSolid());
                            }
                    )
                    .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TabResponse fetchData(FormData fs) {
        try {
            log.info("Fetching credit data for request: {}", fs);
            return new TabResponse("S", "");
        } catch (Exception e) {
            log.error("Error fetching credit data: {}", e.getMessage(), e);
            return new TabResponse("F", e.getMessage());
        }
    }
}
