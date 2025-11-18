package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.bpm.BPMFileUpload;
import com.sib.ibanklosucl.dto.bpm.BpmRequest;
import com.sib.ibanklosucl.model.DealerCodeResponse;
import com.sib.ibanklosucl.model.DealerNameResponse;
import com.sib.ibanklosucl.model.VLFileUpload;
import com.sib.ibanklosucl.model.VehicleLoanVehicle;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.VehicleDetailsRepository;
import com.sib.ibanklosucl.repository.VehicleLoanVehicleRepository;
import com.sib.ibanklosucl.service.bpmsr.BpmService;
import com.sib.ibanklosucl.service.vlsr.VLFileUploadService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class VehicleDetailsService {

    @Autowired
    private VehicleDetailsRepository vehicleDetailsRepository;

    @Autowired
    private VehicleLoanVehicleRepository repository;
    @Autowired
    private FetchRepository fetchRepository;

    @Autowired
    private VLFileUploadService vlFileUploadService;
    @Autowired
    private BpmService bpmService;


    public List<Map<String, String>> getAllStates() {
        return vehicleDetailsRepository.findAllStates();
    }

    public List<Map<String, String>> getAllDealers() {
        return vehicleDetailsRepository.findAllDealers();
    }
    public DealerNameResponse getDealerNames(String dealerName,String dealerCode) {
        return vehicleDetailsRepository.getDealerNames(dealerName,dealerCode);
    }
    public DealerCodeResponse getDealerCodes(String dealerName) {
        return vehicleDetailsRepository.getDealerCodes(dealerName);
    }
    public List<Map<String, String>> getLocationsByDealerInfo(String dealerSubcode, String dealerCode, String dealerName) {
        return vehicleDetailsRepository.findLocationsByDealerInfo(dealerSubcode, dealerCode, dealerName);
    }

    public List<Map<String, String>> getMakesByDealerAndLocation(String dealerSubcode, String dealerCode, String dealerName, String cityId) {
        return vehicleDetailsRepository.findMakesByDealerAndLocation(dealerSubcode, dealerCode, dealerName, cityId);
    }

    //    public List<Map<String, String>> getAllDealersCode() {
//        return vehicleDetailsRepository.findAllDealersCode();
//    }
    public List<Map<String, String>> getCitiesByState(String stateCode) {
        return vehicleDetailsRepository.findCitiesByState(stateCode);
    }

    public List<Map<String, String>> getAllMakes() {
        return vehicleDetailsRepository.findAllMakes();
    }

    public Map<String, List<Map<String, String>>>  getModelsByMake(String makeId,String dealerCode, String dealerSubCode,String dealerCityId) {
        Map<String, List<Map<String, String>>> response = new HashMap<>();
        List<Map<String,String>> ifscMap=fetchRepository.getIFSCAccountDtls(dealerCode,dealerSubCode,makeId,dealerCityId);
        List<Map<String,String>> modelMap=vehicleDetailsRepository.findModelsByMake(makeId);
        response.put("ifscMap", ifscMap);
        response.put("modelMap", modelMap);
        return response ;
    }

    public List<Map<String, String>> getVariantsByModel(String modelId) {
        return vehicleDetailsRepository.findVariantsByModel(modelId);
    }

    public Map<String, String> getPricesByVariantAndCity(String variantId, String cityId) {
        List<Map<String, String>>  vcs =vehicleDetailsRepository.findPricesByVariantAndCity(variantId, cityId);
        if(!vcs.isEmpty())
        {
            return vcs.get(0);
        }
        return null;
    }


    @Transactional
    public VehicleLoanVehicle checkAndInsertOrUpdate(VehicleLoanVehicle vehicleLoanVehicle) {
        return repository.save(vehicleLoanVehicle); // This handles both insert and update
    }

    public VehicleLoanVehicle fetchExisting(String wiNum, Long slno, Long applicantId) {
        return repository.findExistingEntry(wiNum, slno, applicantId);
    }


    public VehicleLoanVehicle fetchExistingbyWinumandSlno(String wiNum, Long slno) {
        return repository.findExistingByWiNumAndSlno(wiNum, slno);
    }
     public VehicleLoanVehicle fetchExistingbySlno(Long slno) {
        return repository.findExistingBySlno(slno);
    }

    public TabResponse processAndUploadFiles(String winum,Long slno,String reqip, List<FileDetails> files, String childFlg, List<VLFileUpload> vlFileUploads) {
        if (files == null || files.isEmpty()) return new TabResponse("S", "");

        BPMFileUpload bpmFileUpload = new BPMFileUpload();
        bpmFileUpload.setWI_NAME(winum);
        bpmFileUpload.setCHILD(childFlg);
        bpmFileUpload.setSystemIP(reqip);
        List<DOC_ARRAY> docArrayList = new ArrayList<>();

        for (FileDetails fileDetails : files) {
            TypeCount tc = CommonUtils.parseString(fileDetails.getFiletype());
            bpmFileUpload.setCHILD_FOLDER(CommonUtils.expandReq(tc));
            DOC_ARRAY docArray = new DOC_ARRAY();
            docArray.setDOC_NAME(fileDetails.getFilename()+CommonUtils.getCurrentTimestamp());
            docArray.setDOC_EXT(fileDetails.getExtention());
            docArray.setDOC_BASE64(fileDetails.getBase64Content());
            docArrayList.add(docArray);

            if (vlFileUploads.stream().noneMatch(t -> fileDetails.getFilecode().equals(t.getFileCode()) && fileDetails.getFiletype().equals(t.getAppType()))) {
                VLFileUpload vlFileUpload = new VLFileUpload();
                vlFileUpload.setFileCode(fileDetails.getFilecode());
                vlFileUpload.setFileName(fileDetails.getFilename());
                vlFileUpload.setFileExtension(fileDetails.getExtention());
                vlFileUpload.setSlno(slno);
                vlFileUpload.setWorkItem(winum);
                vlFileUpload.setAppType(fileDetails.getFiletype());
                vlFileUpload.setAppId(Long.valueOf(fileDetails.getAppid().isEmpty() ? "0" : fileDetails.getAppid()));
                vlFileUploadService.saveFile(vlFileUpload);
            }
        }

        bpmFileUpload.setDOC_ARRAY(docArrayList);
        BpmRequest bpmRequest = new BpmRequest();
        bpmRequest.setRequest(bpmFileUpload);

        return bpmService.BpmUpload(bpmRequest);
    }




}
