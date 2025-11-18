package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.PincodeDTO;
import com.sib.ibanklosucl.model.PincodeMaster;
import com.sib.ibanklosucl.repository.PincodeMasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class PincodeService {

    private final PincodeMasterRepository pincodeRepository;

    @Autowired
    public PincodeService(PincodeMasterRepository pincodeRepository) {
        this.pincodeRepository = pincodeRepository;
    }

    public List<PincodeMaster> getAllLocations(String stateCode) {
        return pincodeRepository.findByState(stateCode);
    }

    public List<PincodeDTO> getPincodesByStateAndCity(String stateCode, String cityCode) {
        return pincodeRepository.findByStateAndCity(stateCode, cityCode)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    @Transactional
    public void createPincode(String pincode,String stateCode, String cityCode) {
        log.info("Creating new pincode entry with and pincode: {}", pincode);

        if (pincodeRepository.existsByPincode(pincode)) {
            throw new RuntimeException("Pincode already exists");
        }

//        PincodeMaster source = pincodeRepository.findById(entryId)
//                .orElseThrow(() -> new EntityNotFoundException("Source entry not found"));
        PincodeMaster pdto = pincodeRepository.findByStateAndCity(stateCode, cityCode).get(0);
        PincodeMaster newEntry = new PincodeMaster();
        newEntry.setStateCode(stateCode);
        newEntry.setFinacleCityCode(pdto.getFinacleCityCode());
        newEntry.setFinacleStateCode(pdto.getFinacleStateCode());
        newEntry.setFinacleState(pdto.getFinacleState());
        newEntry.setDistrict(pdto.getDistrict());
        newEntry.setFinacleCity(pdto.getFinacleCity());
        newEntry.setStateName(pdto.getStateName());
        newEntry.setPincode(pincode);

        pincodeRepository.save(newEntry);
        log.info("Created new pincode entry successfully");
    }


    private PincodeDTO convertToDTO(PincodeMaster entity) {
        return new PincodeDTO(
                entity.getPincode(),
                entity.getPincode(),
                entity.getDistrict(),
                entity.getStateCode(),
                entity.getFinacleCityCode()
        );
    }

    private void validatePincode(String pincode) {
        if (!pincode.matches("^[1-9][0-9]{5}$")) {
            throw new RuntimeException("Invalid pincode format");
        }
    }
}
