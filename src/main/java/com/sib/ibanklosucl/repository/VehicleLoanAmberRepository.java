package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanAmber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleLoanAmberRepository extends RevisionRepository<VehicleLoanAmber, Long, Long>,JpaRepository<VehicleLoanAmber, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE VehicleLoanAmber v SET v.activeFlg = 'N' WHERE v.wiNum = :wiNum AND v.slno = :slNo AND v.activeFlg = 'Y'")
    void updateActiveFlag(@Param("wiNum") String wiNum, @Param("slNo") Long slNo);

    @Query("SELECT v FROM VehicleLoanAmber v WHERE v.wiNum = :wiNum AND v.slno = :slNo AND v.activeFlg = 'Y' AND v.delFlg = 'N'")
    List<VehicleLoanAmber> findActiveByWiNumAndSlno(@Param("wiNum") String wiNum, @Param("slNo") Long slNo);


    @Query("SELECT v FROM VehicleLoanAmber v WHERE v.wiNum = :wiNum AND v.slno = :slNo AND v.amberCode = :amberCode AND v.activeFlg = 'Y' AND v.delFlg = 'N'")
    Optional<VehicleLoanAmber> findActiveByWiNumAndSlnoAndAmberCode(@Param("wiNum") String wiNum, @Param("slNo") Long slNo, @Param("amberCode") String amberCode);

    @Query("SELECT MAX(v.amberCode) from VehicleLoanAmber v WHERE v.wiNum = :wiNum AND v.slno = :slno AND v.amberCode like 'RM%'")
    String findHighestAmberCodeByWiNumAndSlno(String wiNum, Long slno);

        @Query("SELECT v FROM VehicleLoanAmber v WHERE v.wiNum = :wiNum AND v.slno = :slNo AND v.activeFlg = 'Y' AND v.delFlg = 'N' AND v.colour='amber'")
    List<VehicleLoanAmber> findActiveByWiNumAndSlnoAndColour(@Param("wiNum") String wiNum, @Param("slNo") Long slNo);

    @Query("SELECT v.colour FROM VehicleLoanAmber v WHERE v.wiNum = :wiNum AND v.slno = :slNo AND v.activeFlg = 'Y' AND v.delFlg = 'N' AND v.amberCode= :ambercode")
    Optional<String> getambercolor(@Param("wiNum") String wiNum, @Param("slNo") Long slNo, @Param("ambercode") String ambercode);

    @Query("SELECT v FROM VehicleLoanAmber v WHERE v.wiNum = :wiNum AND v.slno = :slNo AND v.activeFlg = 'Y' AND v.delFlg = 'N' AND v.colour= :colour AND v.approvingAuth is null")
    List<VehicleLoanAmber> findActiveByWiNumAndSlnoAndColourAndApprovingAuthIsEmpty(@Param("wiNum") String wiNum, @Param("slNo") Long slNo,String colour);
     List<VehicleLoanAmber> findByWiNumAndActiveFlgOrderById(String wiNum, String activeFlag);

}
