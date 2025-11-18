package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanFinancial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;


public interface VehicleLoanFinancialRepository extends RevisionRepository<VehicleLoanFinancial, Long,Long>, JpaRepository<VehicleLoanFinancial, Long> {


    VehicleLoanFinancial findByApplicantId(Long applicantId);
}
