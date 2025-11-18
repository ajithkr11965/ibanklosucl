package com.sib.ibanklosucl.repository.mssf;

import com.sib.ibanklosucl.model.mssf.MSSFDealerSol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MSSFDealerSolRepository extends JpaRepository<MSSFDealerSol, String> {
    Optional<MSSFDealerSol> findByDlrCode(String dlrCode);
}
