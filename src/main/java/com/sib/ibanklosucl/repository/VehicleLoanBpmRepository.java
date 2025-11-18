package com.sib.ibanklosucl.repository;


import com.sib.ibanklosucl.model.VehicleLoanBpm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface VehicleLoanBpmRepository  extends RevisionRepository<VehicleLoanBpm, Long, Long>, JpaRepository<VehicleLoanBpm, Long> {

    VehicleLoanBpm findByWiNumAndSlno(String winum,Long slno);

}