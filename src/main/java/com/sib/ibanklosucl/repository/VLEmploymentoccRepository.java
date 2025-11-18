package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VLEmploymentocc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VLEmploymentoccRepository extends RevisionRepository<VLEmploymentocc, Long,Long>, JpaRepository<VLEmploymentocc, Long> {
    List<VLEmploymentocc> findByApplicantIdAndWiNumAndDelFlg(Long applicantId,String wiNum,String delFlg);

    VLEmploymentocc findByWiNumAndApplicantIdAndDelFlg(String wiNum,Long applicantId,String delFlg);

}
