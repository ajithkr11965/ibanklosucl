package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.dto.AnnualIncomeAndBankBalance;
import com.sib.ibanklosucl.model.VehicleLoanProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleLoanProgramRepository extends RevisionRepository<VehicleLoanProgram, Long,Long>, JpaRepository<VehicleLoanProgram, Long> {
    List<VehicleLoanProgram> findByWiNumAndSlNoAndDelFlg(String wiNum, Long slNo, String delFlag);
    @Query("SELECT SUM(v.abb) FROM VehicleLoanProgram v WHERE v.delFlg = 'N' and v.wiNum = :wiNum AND v.slNo = :slNo AND v.incomeConsidered = 'Y'")
    Optional<BigDecimal> findSumOfAbbWhereDelFlgIsN(String wiNum, Long slNo);
    @Query("SELECT SUM(v.depAmt) FROM VehicleLoanProgram v WHERE v.delFlg = 'N' and v.wiNum = :wiNum AND v.slNo = :slNo AND v.incomeConsidered = 'Y'")
    Optional<BigDecimal> findSumOfDepAmtWhereDelFlgIsN(String wiNum, Long slNo);

    @Query("SELECT SUM(vlp.avgSal) AS annualIncome, SUM(vlp.abb) AS annualBankBalance FROM VehicleLoanProgram vlp WHERE vlp.wiNum = :wiNum AND vlp.slNo = :slNo AND vlp.delFlg = 'N'")
    AnnualIncomeAndBankBalance findAnnualIncomeAndBankBalance( @Param("slNo") Long slNo);



    Optional<VehicleLoanProgram> findByApplicantIdAndDelFlg(Long applicantId,String delflag);

    List<VehicleLoanProgram> findByWiNumAndIncomeConsideredAndDelFlg(String wiNum, String y, String n);
}
