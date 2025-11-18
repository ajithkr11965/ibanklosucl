package com.sib.ibanklosucl.repository;
import com.sib.ibanklosucl.model.VehicleLoanEligibilityDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;
@Repository
public interface VehicleLoanEligibilityDetailsRepository extends RevisionRepository<VehicleLoanEligibilityDetails, Long,Long>, JpaRepository<VehicleLoanEligibilityDetails, Long> {
    Optional<VehicleLoanEligibilityDetails> findByWiNumAndSlnoAndDelFlg(String wiNum,Long slno, String delFlg);
    @Modifying
    @Transactional
    @Query("UPDATE VehicleLoanEligibilityDetails v SET v.delFlg = 'Y',v.delDate = CURRENT_TIMESTAMP WHERE v.wiNum = :wiNum and v.slno=:slno AND v.delFlg = 'N'")
    void updateDelFlgByWiNumAndSlno(@Param("wiNum") String wiNum,@Param("slno")Long slno);
    Optional<VehicleLoanEligibilityDetails> findTopByWiNumAndSlnoOrderByInoDesc(String wiNum, Long slno);
}
