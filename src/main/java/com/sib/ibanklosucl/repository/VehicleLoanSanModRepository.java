package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanSanMod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleLoanSanModRepository extends JpaRepository<VehicleLoanSanMod, Long> {
    //Optional<VehicleLoanSanMod> findBySlno(Long slno);

    @Query("SELECT t FROM VehicleLoanSanMod t WHERE t.vlsanmod.taskId = :taskId")
    Optional<VehicleLoanSanMod> findByTaskId(@Param("taskId") Long taskId);
}
