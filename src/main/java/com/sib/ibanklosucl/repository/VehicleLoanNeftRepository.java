package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanNeftInputs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VehicleLoanNeftRepository extends JpaRepository<VehicleLoanNeftInputs, Long> {
    VehicleLoanNeftInputs findBySlnoAndDelflag(Long slno, String delflag);
}