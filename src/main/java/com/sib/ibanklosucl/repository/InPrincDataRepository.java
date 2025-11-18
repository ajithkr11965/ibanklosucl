package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.InPrincData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InPrincDataRepository extends JpaRepository<InPrincData, String> {
    List<InPrincData> findByWiNumAndSlno(String wiNum, Long slno);
}
