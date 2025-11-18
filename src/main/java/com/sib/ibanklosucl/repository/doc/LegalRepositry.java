package com.sib.ibanklosucl.repository.doc;
import com.sib.ibanklosucl.model.doc.LegalityInvitees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface LegalRepositry extends RevisionRepository<LegalityInvitees, Long,Long>,JpaRepository<LegalityInvitees, Long> {
    Optional<List<LegalityInvitees>> findBySlno(Long slno);

    @Query("select count(*)  from LegalityInvitees l where l.slno=:slno and sysdate<=l.expiryDate")
    Long isExpired(Long slno);

    LegalityInvitees findBySlnoAndSignUrl(Long slno,String signUrl);
    @Query("select to_char(max(l.completionDate),'yyyy-mm-dd')  from LegalityInvitees l where l.slno=:slno")
    String getCompletedDate(Long slno);

    @Modifying
    @Transactional
    @Query("DELETE FROM LegalityInvitees l WHERE l.wiNum = :wiNum")
    void deleteByWiNum(@Param("wiNum") String wiNum);

}

