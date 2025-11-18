package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanVkyc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleLoanVkycRepository extends RevisionRepository<VehicleLoanVkyc, Long,Long>, JpaRepository<VehicleLoanVkyc, Long>{
    VehicleLoanVkyc findByVkycUniqueId(String uniqueId);
}
