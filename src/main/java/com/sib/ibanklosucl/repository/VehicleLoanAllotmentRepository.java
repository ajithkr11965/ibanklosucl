package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanAllotment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleLoanAllotmentRepository extends JpaRepository<VehicleLoanAllotment, Long> {
    Optional<VehicleLoanAllotment> findByWiNumAndSlnoAndActiveFlg(String wiNum, Long slno, String activeFlg);
    Optional<VehicleLoanAllotment> findByWiNumAndSlnoAndDoPpcAndActiveFlg(String wiNum, Long slno, Integer doPpc, String activeFlg);
    VehicleLoanAllotment findBySlnoAndWiNumAndActiveFlgAndDelFlg(Long slno, String wiNum, String activeFlg, String delFlg);

    Optional<VehicleLoanAllotment> findByWiNumAndSlnoAndDoPpcAndActiveFlgAndDelFlg(String wiNum, Long slno, Integer employeePpcno, String y, String n);
}
