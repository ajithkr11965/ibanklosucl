package com.sib.ibanklosucl.repository;


import com.sib.ibanklosucl.model.EligibilityDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EligibilityDetailsRepository  extends RevisionRepository<EligibilityDetails, Long,Long>, JpaRepository<EligibilityDetails, Long> {
    Optional<EligibilityDetails> findByWiNumAndSlno(String wiNum, Long slno);

    Optional<EligibilityDetails> findBySlnoAndDelFlg(Long slno,String delflag);

}
