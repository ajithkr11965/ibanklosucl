package com.sib.ibanklosucl.repository.integations;

import com.sib.ibanklosucl.model.integrations.VLPartialBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface VLPartialBlacklistRepository extends RevisionRepository<VLPartialBlackList, Long, Long>, JpaRepository<VLPartialBlackList, Long> {
    Optional<VLPartialBlackList> findByApplicantIdAndDelFlg(Long applicantId, String delFlg);
}
