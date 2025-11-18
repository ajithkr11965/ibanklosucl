package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VLFinasset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VLFinastrepository extends JpaRepository<VLFinasset, Long> {
    List<VLFinasset> findVLFinassetByApplicantIdAndWiNumAndDelFlg(Long ApplicantId,String wiNum,String delFlg);

}
