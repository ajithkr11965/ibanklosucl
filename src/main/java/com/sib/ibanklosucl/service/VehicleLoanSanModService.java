package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.repository.VehicleLoanSanModRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sib.ibanklosucl.model.VehicleLoanSanMod;

@Service
public class VehicleLoanSanModService {

    @Autowired
    private VehicleLoanSanModRepository vehicleLoanSanModRepository;
    public void save(VehicleLoanSanMod vehicleLoanSanMod){
        vehicleLoanSanModRepository.save(vehicleLoanSanMod);
    }
    /*
    public VehicleLoanSanMod findBySlno(Long slno){
        return vehicleLoanSanModRepository.findBySlno(slno)
                .orElse(null);
    }
     */

    public VehicleLoanSanMod findByTaskId(Long taskId){
        return vehicleLoanSanModRepository.findByTaskId(taskId)
                .orElse(null);
    }
}
