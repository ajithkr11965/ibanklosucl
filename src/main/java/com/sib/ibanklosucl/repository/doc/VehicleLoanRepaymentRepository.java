package com.sib.ibanklosucl.repository.doc;

import com.sib.ibanklosucl.model.doc.VehicleLoanRepayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleLoanRepaymentRepository  extends RevisionRepository<VehicleLoanRepayment, Long,Long>, JpaRepository<VehicleLoanRepayment, Long> {
    Optional<VehicleLoanRepayment> findBySlnoAndDelFlg(Long slno,String delflag);
    Optional<VehicleLoanRepayment> findByWiNumAndDelFlg(String WiNum,String delflag);

}
