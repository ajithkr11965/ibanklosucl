package com.sib.ibanklosucl.repository.doc;

import com.sib.ibanklosucl.model.doc.VehicleLoanRoiWaiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleLoanRoiWaiverRepository extends RevisionRepository<VehicleLoanRoiWaiver, Long,Long>, JpaRepository<VehicleLoanRoiWaiver, Long> {
    @Query("SELECT v FROM VehicleLoanRoiWaiver v WHERE v.taskId = :taskId")
    Optional<VehicleLoanRoiWaiver> findByTaskId(@Param("taskId") Long taskId);

    List<VehicleLoanRoiWaiver> findBySlnoOrderByInoDesc(Long slno);
}

