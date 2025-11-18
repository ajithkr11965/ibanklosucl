package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.misprm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MisprmRepository extends JpaRepository<misprm, String> {

    misprm findByPCODE(String codetype);
}
