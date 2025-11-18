package com.sib.ibanklosucl.repository.program;

import com.sib.ibanklosucl.model.VehicleLoanFD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleLoanFDRepository extends RevisionRepository<VehicleLoanFD, Long,Long>, JpaRepository<VehicleLoanFD, Long> {
    @Query("SELECT v from VehicleLoanFD v where v.vlfd.ino = :programIno and v.wiNum = :wiNum")
    List<VehicleLoanFD> findByProgramInoAndWiNum(@Param("programIno") Long programIno, @Param("wiNum") String wiNum);

     boolean existsByVlfdInoAndWiNumAndFdaccnumAndDelFlg(Long programIno, String wiNum, String fdaccnum, String delFlg);

    List<VehicleLoanFD> findByVlfdInoAndWiNumAndDelFlg(Long programIno, String wiNum, String delFlg);
    @Query("SELECT v from VehicleLoanFD v where v.applicantId=:applicantId and v.wiNum=:wiNum")
    List<VehicleLoanFD> findByApplicantIdAndWiNum(@Param("applicantId") Long applicantId,@Param("wiNum") String wiNum);
    boolean existsByApplicantIdAndWiNumAndFdaccnumAndDelFlg(Long applicantId, String wiNum, String fdaccnum,String delFlg);

    List<VehicleLoanFD> findByApplicantIdAndWiNumAndDelFlg(Long applicantId, String wiNum, String delFlg);
    void deleteByApplicantIdAndWiNum(Long applicantId,String wiNum);
    List<VehicleLoanFD> findByFdaccnumAndWiNumAndDelFlg(String fdaccnum, String wiNum, String delFlg);

    List<VehicleLoanFD> findByWiNumAndDelFlg(String wiNum, String delFlg);
    Optional<VehicleLoanFD> findByFdaccnumAndApplicantIdAndWiNumAndDelFlg(String fdaccnum, Long applicantId, String wiNum, String delFlg);
    List<VehicleLoanFD> findByApplicantIdAndWiNumAndEligibleAndDelFlg(Long applicantId,String wiNum,boolean eligible,String delFlg);
    List<VehicleLoanFD> findByWiNumAndEligibleAndDelFlgAndLienStatusIsNull(String wiNum, boolean eligible, String delFlg);

}
