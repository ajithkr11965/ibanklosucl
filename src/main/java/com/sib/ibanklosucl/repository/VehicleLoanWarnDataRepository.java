package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanWarnData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface VehicleLoanWarnDataRepository extends RevisionRepository<VehicleLoanWarnData, Long,Long>, JpaRepository<VehicleLoanWarnData, Long> {

}