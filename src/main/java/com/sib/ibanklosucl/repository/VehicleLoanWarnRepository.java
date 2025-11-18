package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanWarn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleLoanWarnRepository extends RevisionRepository<VehicleLoanWarn, Long,Long>,JpaRepository<VehicleLoanWarn, Long> {

    @Query("SELECT v FROM VehicleLoanWarn v,VehicleLoanApplicant  a WHERE v.delFlg = 'N' AND v.activeFlg = 'Y' and a.applicantId=v.applicantId and a.slno=v.slno and a.delFlg!='Y' and v.slno=:slno")
    List<VehicleLoanWarn> findActiveAndNotDeleted(Long slno);

    @Modifying
    @Query("UPDATE VehicleLoanWarn v SET v.activeFlg = 'N' WHERE v IN :vehicleLoanWarns")
    void updateActiveFlgToN(@Param("vehicleLoanWarns") List<VehicleLoanWarn> vehicleLoanWarns);


    Long countBySlnoAndActiveFlgAndDelFlg(Long slno,String  activeFlg,String delFlg);




}
