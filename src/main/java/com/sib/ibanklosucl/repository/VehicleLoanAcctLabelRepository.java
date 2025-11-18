package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanAcctLabels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleLoanAcctLabelRepository extends JpaRepository<VehicleLoanAcctLabels, Long> {
    void deleteBySlno(Long slno);
}