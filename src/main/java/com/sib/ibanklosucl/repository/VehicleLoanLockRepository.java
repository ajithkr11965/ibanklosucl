package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface VehicleLoanLockRepository  extends RevisionRepository<VehicleLoanLock, Long,Long>, JpaRepository<VehicleLoanLock, Long> {

    @Query(nativeQuery = true, value = "select LOCKED_BY from VEHICLE_LOAN_LOCK  where slno=:slno and LOCK_FLG='Y' and DEL_FLG='N' and RELEASED_ON is null ")
    String  lockBySlnoAndAndLockedByAndDelFlgAAndReleasedBy(Long slno);

    List<VehicleLoanLock> findBySlnoAndLockedByAndLockFlgAndDelFlgAndReleasedOnIsNull(Long slno,String lockedby,String lockflg,String delflag);

    Optional<VehicleLoanLock> findByWiNumAndDelFlgAndLockFlg(String wiNum,String delflag,String lockflg);

    @Modifying
    @Transactional
    @Query("update VehicleLoanLock  k set k.releasedBy=:lockedby ,k.releasedOn=:releasedOn,k.lockFlg='N' where k.slno=:slno and k.lockedBy=:lockedby and  k.releasedOn is null")
    void releaseLock(Long slno, String lockedby, Date releasedOn);


    @Modifying
    @Transactional
    @Query("update VehicleLoanLock  k set k.releasedBy=:lockedby ,k.releasedOn=:releasedOn,k.lockFlg='N' where   k.lockedBy=:lockedby and  k.releasedOn is null")
    void releaseAllLock(String lockedby, Date releasedOn);
}
