package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanSubqueueTat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleLoanSubqueueTatRepository extends RevisionRepository<VehicleLoanSubqueueTat,Long,Long >,JpaRepository< VehicleLoanSubqueueTat,Long > {
      VehicleLoanSubqueueTat findBySubqueueExitDateIsNullAndSlnoAndDelFlg(Long slno, String delFlg);

      List<VehicleLoanSubqueueTat> findByTaskIdOrderBySubqueueEntryDateDesc(Long taskId);

      VehicleLoanSubqueueTat findBySubqueueExitDateIsNullAndTaskIdAndDelFlg(Long taskId, String n);
}
