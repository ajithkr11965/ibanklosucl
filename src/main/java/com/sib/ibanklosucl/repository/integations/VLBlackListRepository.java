package com.sib.ibanklosucl.repository.integations;

import com.sib.ibanklosucl.model.integrations.VLBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VLBlackListRepository extends RevisionRepository<VLBlackList, Long, Long>, JpaRepository<VLBlackList, Long> {
    Optional<VLBlackList> findByApplicantIdAndDelFlg(Long applicantId, String delFlg);

    @Query("SELECT v FROM VLBlackList v WHERE v.applicantId = :applicantId AND v.delFlg = 'N' ORDER BY v.cmDate DESC")
    List<VLBlackList> findAllActiveByApplicantIdOrderByCmDateDesc(@Param("applicantId") Long applicantId);

    @Query("SELECT COUNT(v) > 0 FROM VLBlackList v WHERE v.applicantId = :applicantId AND v.delFlg = 'N'")
    boolean existsActiveByApplicantId(@Param("applicantId") Long applicantId);

    @Query("SELECT v FROM VLBlackList v WHERE v.wiNum = :wiNum AND v.delFlg = 'N'")
    Optional<VLBlackList> findActiveByWiNum(@Param("wiNum") String wiNum);

    @Query("SELECT v FROM VLBlackList v WHERE v.delFlg = 'N'")
    List<VLBlackList> findAllActive();

    @Query("UPDATE VLBlackList v SET v.delFlg = 'Y' WHERE v.applicantId = :applicantId AND v.delFlg = 'N'")
    int markAllAsDeletedByApplicantId(@Param("applicantId") Long applicantId);

}
