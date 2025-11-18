package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanWarnMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleLoanWarnMasterRepository  extends JpaRepository<VehicleLoanWarnMaster, Long> {

    List<VehicleLoanWarnMaster> findAllByDelFlg(String delflag);
}
