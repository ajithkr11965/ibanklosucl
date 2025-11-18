package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VLCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VLCreditrepository extends JpaRepository<VLCredit, Long> {


//    @Query("SELECT m FROM VLEmployment m  where m.slno=:slno and m.delFlg='N'" )
//    VLEmployment findBySlnoWithApplicants(@Param("slno") Long slno);


//    Optional<VLEmployment> findByWiNumAndSlno(String wiNum, Long slno);

    VLCredit findBySlno(Long slno);


    @Query("SELECT v FROM VLCredit  v join VehicleLoanApplicant vla on vla.applicantId=v.applicantId  WHERE v.wiNum = :wiNum AND v.slno = :slNo AND vla.delFlg='N' AND v.delFlg = 'N' and vla.applicantType='A' ")
    List<VLCredit> findVLCreditDetails(@Param("wiNum") String wiNum, @Param("slNo") Long slNo);

    VLCredit findByApplicantIdAndDelFlg(Long appid, String delflg);
}
