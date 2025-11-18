package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VLEmploymentemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VLEmploymentempRepository extends JpaRepository<VLEmploymentemp, Long> {
    List<VLEmploymentemp> findVLEmploymentempByApplicantIdAndWiNumAndDelFlg(Long ApplicantId,String wiNum,String delFlg);

}
