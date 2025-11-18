package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleLoanDecisionRepository extends JpaRepository<VehicleLoanDecision, Long> {
    // You can add custom query methods here if needed
}
