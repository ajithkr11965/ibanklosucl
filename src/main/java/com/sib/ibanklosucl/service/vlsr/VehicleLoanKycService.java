package com.sib.ibanklosucl.service.vlsr;

import com.sib.ibanklosucl.model.VehicleLoanKyc;
import com.sib.ibanklosucl.repository.VehicleLoanKycRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleLoanKycService {

    @Autowired
    private VehicleLoanKycRepository repository;

    public List<VehicleLoanKyc> findAll() {
        return repository.findAll();
    }

    //    public VehicleLoanKyc findById(Long id) {
//        return repository.findById(id).orElseThrow(() -> new RuntimeException("Not Found"));
//    }
    public VehicleLoanKyc findByAppId(Long id) {
        return repository.findByApplicantIdAndDelFlg(id, "N");
    }

    @Transactional
    public VehicleLoanKyc save(VehicleLoanKyc vehicleLoanKyc) {
        return repository.save(vehicleLoanKyc);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Optional<VehicleLoanKyc> findActiveVehicleLoanKyc(Long applicantId, String wiNum, Long slno) {
        return repository.findActiveVehicleLoanKyc(wiNum, slno, applicantId);
    }


}
