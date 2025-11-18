package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VLEmployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VLEmploymentRepository extends RevisionRepository<VLEmployment, Long,Long>, JpaRepository<VLEmployment, Long> {


    @Query("SELECT m FROM VLEmployment m  where m.slno=:slno and m.delFlg='N'" )
    List<VLEmployment> findBySlnoWithApplicants(@Param("slno") Long slno);


    //Optional<VLEmployment> findByWiNumAndSlno(String wiNum, Long slno);



    VLEmployment findBySlno(Long slno);


    VLEmployment findByApplicantIdAndDelFlg(Long appid, String delflg);
}
