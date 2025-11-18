package com.sib.ibanklosucl.repository.integations;

import com.sib.ibanklosucl.model.integrations.BureauCheckDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BureauCheckRepository extends RevisionRepository<BureauCheckDetails, Long, Long>, JpaRepository<BureauCheckDetails, Long> {
    List<BureauCheckDetails> findByWiNumAndSlnoAndApplicantIdAndDelFlg(
        String wiNum, Long slno, Long applicantId, String delFlg);
}
