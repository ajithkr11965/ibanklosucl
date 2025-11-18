package com.sib.ibanklosucl.repository.mssf;

import com.sib.ibanklosucl.model.mssf.MSSFLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MSSFLockRepository extends JpaRepository<MSSFLock, String> {
    Optional<MSSFLock> findByRefNo(String refNo);
}
