package com.sib.ibanklosucl.repository.integations;

import com.sib.ibanklosucl.model.integrations.VLHunterDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExperianHunterResponseRepository extends RevisionRepository<VLHunterDetails, Long,Long>, JpaRepository<VLHunterDetails, Long> {

//public interface ExperianHunterResponseRepository extends RevisionRepository<VLHunterDetails, Long,Long>, JpaRepository<VLHunterDetails, Long> {

    Optional<VLHunterDetails> findByWiNumAndDelFlg(String wiNum, String delFlg);

    List<VLHunterDetails> findAllByApplicantIdAndDelFlg(Long applicantId, String delFlg);

    Optional<VLHunterDetails> findByWiNumAndApplicantIdAndDelFlg(String wiNum, Long applicantId, String delFlg);

    List<VLHunterDetails> findAllByDecisionAndDelFlg(String decision, String delFlg);

    List<VLHunterDetails> findAllByScoreGreaterThanAndDelFlg(Integer score, String delFlg);

    List<VLHunterDetails> findAllByResponseCodeAndDelFlg(String responseCode, String delFlg);

    Optional<VLHunterDetails> findTopByApplicantIdAndDelFlgOrderByTimestampDesc(Long applicantId, String delFlg);
    List<VLHunterDetails> findAllByWiNumAndDelFlgOrderByTimestampDesc(String wiNum, String delFlg);

    List<VLHunterDetails> findAllByScoreBetweenAndDelFlg(Integer minScore, Integer maxScore, String delFlg);

    Optional<VLHunterDetails> findByAppReferenceAndDelFlg(String appReference, String delFlg);

    @Query("SELECT e FROM VLHunterDetails e WHERE e.delFlg = :delFlg")
    List<VLHunterDetails> findAllNonDeleted(@Param("delFlg") String delFlg);

    @Query("SELECT e FROM VLHunterDetails e WHERE e.id = :ino AND e.delFlg = :delFlg")
    Optional<VLHunterDetails> findByIdAndNotDeleted(@Param("ino") Long ino, @Param("delFlg") String delFlg);
}
