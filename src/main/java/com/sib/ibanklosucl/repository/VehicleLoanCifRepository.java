package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanCIF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.*;

public interface VehicleLoanCifRepository extends RevisionRepository<VehicleLoanCIF, Long, Long>, JpaRepository<VehicleLoanCIF, Long> {
    Optional<VehicleLoanCIF> findByApplicantIdAndDelFlag(Long applicantId, String delFlg);

    Optional<List<VehicleLoanCIF>> findByWiNum(String wiNum);

}
