package com.sib.ibanklosucl.repository.integations;

import com.sib.ibanklosucl.model.NACHMandate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NACHMandateRepository  extends RevisionRepository<NACHMandate, Long, Long>,JpaRepository<NACHMandate, Long> {
     Optional<NACHMandate> findBySlnoAndDelFlg(Long slno,String delFlg);
     boolean existsBySlnoAndDelFlg(Long slno,String delFlg);
     Optional<NACHMandate> findByWinumAndDelFlg(String winum,String delFlg);
     Optional<NACHMandate> findByReference(String reference);
}
