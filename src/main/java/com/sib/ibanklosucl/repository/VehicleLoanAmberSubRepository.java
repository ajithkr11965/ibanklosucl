package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanAmberSub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface VehicleLoanAmberSubRepository extends RevisionRepository<VehicleLoanAmberSub, Long, Long>,JpaRepository<VehicleLoanAmberSub, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE VehicleLoanAmberSub v SET v.activeFlg = 'N' WHERE v.wiNum = :wiNum AND v.slno = :slNo AND v.activeFlg = 'Y'")
    void updateActiveFlag(@Param("wiNum") String wiNum, @Param("slNo") Long slNo);

    @Query("SELECT v FROM VehicleLoanAmberSub v WHERE v.wiNum = :wiNum AND v.slno = :slNo AND v.activeFlg = 'Y' AND v.delFlg = 'N'")
    List<VehicleLoanAmberSub> findActiveByWiNumAndSlno(@Param("wiNum") String wiNum, @Param("slNo") Long slNo);

    @Query("SELECT v FROM VehicleLoanAmberSub v WHERE v.wiNum = :wiNum AND v.slno = :slNo AND v.amberCode = :amberCode AND v.activeFlg = 'Y' AND v.delFlg = 'N'")
    List<VehicleLoanAmberSub> findActiveByWiNumAndSlnoAndAmberCode(@Param("wiNum") String wiNum, @Param("slNo") Long slNo, @Param("amberCode") String amberCode);

    @Query("SELECT v FROM VehicleLoanAmberSub v WHERE v.wiNum = :wiNum AND v.slno = :slNo AND v.applicantId = :applicantId AND v.activeFlg = 'Y' AND v.delFlg = 'N'")
    List<VehicleLoanAmberSub> findActiveByWiNumAndSlnoAndApplicantId(@Param("wiNum") String wiNum, @Param("slNo") Long slNo, @Param("applicantId") Long applicantId);
}
