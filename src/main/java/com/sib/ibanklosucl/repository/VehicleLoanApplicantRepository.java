package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface VehicleLoanApplicantRepository extends RevisionRepository<VehicleLoanApplicant, Long,Long>, JpaRepository<VehicleLoanApplicant, Long> {

    @Query("SELECT v FROM VehicleLoanApplicant v WHERE v.slno = :slno AND v.delFlg = 'N'")
    List<VehicleLoanApplicant> findBySlnoAndDelFlg(@Param("slno") Long slno);

    VehicleLoanApplicant findByApplicantIdAndDelFlg(Long appid,String delflag);
     VehicleLoanApplicant findBySlnoAndDelFlgAndApplicantType(Long slno,String delflag,String applicantType);
    List<VehicleLoanApplicant> findBySlnoAndDelFlg(Long slno,String delflag);

    @Query("SELECT v FROM VehicleLoanApplicant v WHERE v.slno = :slno AND v.delFlg = :delFlg  and v.applicantType IN :applicantTypes")
    List<VehicleLoanApplicant> findBySlnoAndDelFlgAndApplicantTypes(@Param("slno") Long slno,@Param("delFlg") String delFlg,@Param("applicantTypes") List<String> applicantTypes);

    List<VehicleLoanApplicant> findByWiNumAndSlno(String winum, Long slno);

    Long countAllBySlnoAndDelFlgAndApplicantType(Long slno,String delflg,String apptype);
    Optional<VehicleLoanApplicant> findAllBySlnoAndApplicantTypeAndDelFlg(Long slno,String app, String delflg);

    @Modifying
    @Query("UPDATE VehicleLoanApplicant v set v.loanComplete='N' WHERE v.applicantType='A' and v.slno=:slno" )
    void updateLoanFlg(@Param("slno") Long slno);
    @Modifying
    @Query("UPDATE VehicleLoanApplicant v set v.raceScore=:score WHERE v.applicantId = :appid AND v.delFlg = 'N'")
    void updateApplicantRaceScore(@Param("appid") Long appid,@Param("score") String score);
    @Query("SELECT new map(" +
           "k.panDob as panDob, " +
           "k.panNo as panNo, " +
           "b.mobileCntryCode as mobileCntryCode, " +
           "b.mobileNo as mobileNo, " +
           "a.applicantType as applicantType, " +
           "a.residentFlg as residentFlg) " +
           "FROM VehicleLoanApplicant a " +
           "JOIN a.kycapplicants k " +
           "JOIN a.basicapplicants b " +
           "WHERE a.slno = :slno " +
           "AND a.wiNum = :wiNum " +
           "AND a.applicantId = :applicantId")
    Map<String, Object> findApplicantDetails(@Param("slno") Long slno,
                                             @Param("wiNum") String wiNum,
                                             @Param("applicantId") Long applicantId);

}


