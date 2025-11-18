package com.sib.ibanklosucl.service.vlsr;

import com.sib.ibanklosucl.dto.ExperianPincodeMasterDTO;
import com.sib.ibanklosucl.model.PincodeMaster;
import com.sib.ibanklosucl.repository.PincodeMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PincodeMasterService {

    @Autowired
    private PincodeMasterRepository pincodeMasterRepository;


    public List<PincodeMaster> findAll() {
        return pincodeMasterRepository.findAll();
    }


    public Optional<PincodeMaster> findById(String pincode) {
        return pincodeMasterRepository.findById(pincode);
    }


    public Optional<ExperianPincodeMasterDTO> getexperianaddressdata(String pincode) {
        return pincodeMasterRepository.getexperianaddressdata(pincode);
    }

    public boolean isValidIndianPincode(String pincode,String finacleStatecode,String city){
        return pincodeMasterRepository.countByFinacleStateCodeAndPincodeAndFinacleCityCode(finacleStatecode,pincode,city)==1;
    }



    public PincodeMaster save(PincodeMaster pincodeMaster) {
        return pincodeMasterRepository.save(pincodeMaster);
    }


    public void deleteById(String pincode) {
        pincodeMasterRepository.deleteById(pincode);
    }

}
