package com.sib.ibanklosucl.service.vlsr;

import com.sib.ibanklosucl.model.VehicleLoanBasic;
import com.sib.ibanklosucl.repository.VehicleLoanBasicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleLoanBasicService {

    @Autowired
    private VehicleLoanBasicRepository vehicleLoanBasicRepository;
    public VehicleLoanBasic findByAppId(Long id) {
        return vehicleLoanBasicRepository.findByApplicantIdAndDelFlg(id,"N");
    }

    public boolean isKerala(Long slno){
            return vehicleLoanBasicRepository.findAppState(slno).equalsIgnoreCase("KL");
    }
    public VehicleLoanBasic save(VehicleLoanBasic vehicleLoanBasic) {
        return vehicleLoanBasicRepository.save(vehicleLoanBasic);
    }

}
