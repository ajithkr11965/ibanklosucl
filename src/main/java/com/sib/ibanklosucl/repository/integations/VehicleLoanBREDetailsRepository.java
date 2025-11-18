package com.sib.ibanklosucl.repository.integations;

import com.sib.ibanklosucl.model.integrations.VehicleLoanBREDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface VehicleLoanBREDetailsRepository extends RevisionRepository<VehicleLoanBREDetails, Long,Long>, JpaRepository<VehicleLoanBREDetails, Long> {
    Optional<VehicleLoanBREDetails> findByWiNumAndSlnoAndDelFlg(String wiNum,Long slno, String delFlg);
    @Modifying
    @Transactional
    @Query("UPDATE VehicleLoanBREDetails v SET v.delFlg = 'Y' WHERE v.wiNum = :wiNum and v.slno=:slno AND v.delFlg = 'N'")
    void updateDelFlgByWiNumAndSlno(@Param("wiNum") String wiNum,@Param("slno")Long slno);
     Optional<VehicleLoanBREDetails> findTopByWiNumAndSlnoOrderByIdDesc(String wiNum, Long slno);
}
