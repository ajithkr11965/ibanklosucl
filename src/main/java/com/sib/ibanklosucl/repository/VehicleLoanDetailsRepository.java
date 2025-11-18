package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface VehicleLoanDetailsRepository extends RevisionRepository<VehicleLoanDetails, Long, Long>, JpaRepository<VehicleLoanDetails, Long> {
        VehicleLoanDetails findBySlnoAndDelFlg(Long slno,String DelFlg);


        @Modifying
        @Transactional
        @Query("update VehicleLoanDetails  l set l.delFlg='Y' where l.slno=:slno ")
        void deleteRest(Long slno);
        List<VehicleLoanDetails> findByWiNumAndSlnoAndDelFlg(String wiNum, Long slno, String n);
}


