package com.sib.ibanklosucl.repository;


import com.sib.ibanklosucl.model.VehicleLoanAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountOpeningRepository extends JpaRepository<VehicleLoanAccount, Long> {
    VehicleLoanAccount findBySlnoAndDelflag(Long slno, String delflag);
}