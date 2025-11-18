package com.sib.ibanklosucl.service.vlsr;


import com.sib.ibanklosucl.model.VehicleLoanBpm;
import com.sib.ibanklosucl.repository.VehicleLoanBpmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleLoanBpmRepositoryService {

    @Autowired
    private VehicleLoanBpmRepository repository;
    public VehicleLoanBpm getById(Long ino,String wi) {
        return repository.findByWiNumAndSlno(wi,ino);
    }


    public VehicleLoanBpm save(VehicleLoanBpm vehicleLoanKyc) {
        return repository.save(vehicleLoanKyc);
    }

}
