package com.sib.ibanklosucl.service.vlsr;

import com.sib.ibanklosucl.model.VehicleLoanLock;
import com.sib.ibanklosucl.repository.VehicleLoanLockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class VehicleLoanLockService {

    @Autowired
    private VehicleLoanLockRepository repository;


    public VehicleLoanLock saveLock(VehicleLoanLock lock) {
        return repository.save(lock);
    }

    @Transactional
    public void ReleaseLock(Long ino,String lockedby) {
         repository.releaseLock(ino,lockedby,new Date());
    }
    @Transactional
    public void ReleaseAllLock(String lockedby) {
         repository.releaseAllLock(lockedby,new Date());
    }

    public VehicleLoanLock getLockById(Long ino) {
        return repository.findById(ino).orElse(null);
    }
    public List<VehicleLoanLock> getLockBySlno(Long ino,String lockedby) {
        return repository.findBySlnoAndLockedByAndLockFlgAndDelFlgAndReleasedOnIsNull(ino,lockedby,"Y","N");
    }

    public String Locked(Long slno) {
        return repository.lockBySlnoAndAndLockedByAndDelFlgAAndReleasedBy(slno);
    }

}
