package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VLFinliab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VLFinliabrepository extends JpaRepository<VLFinliab, Long> {
    List<VLFinliab> findVLFinliabByApplicantIdAndWiNumAndDelFlg(Long ApplicantId,String wiNum,String delFlg);

}
